-- =====================================================================
-- IBS CORE (VIP MOBILE) - DATABASE SCHEMA
-- Engine   : MySQL 8.0+
-- Charset  : utf8mb4
-- Author   : Rancangan untuk pengembangan aplikasi VIP Mobile (Android)
-- Catatan  : Skema ini diakses via REST API backend, BUKAN langsung dari
--            aplikasi Android (alasan keamanan kredensial & driver JDBC
--            Android sudah deprecated).
-- =====================================================================

CREATE DATABASE IF NOT EXISTS ibs_core
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE ibs_core;

SET FOREIGN_KEY_CHECKS = 0;

-- =====================================================================
-- 1. USERS  (Nasabah)
-- =====================================================================
CREATE TABLE users (
    user_id             BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    full_name           VARCHAR(150)        NOT NULL,
    phone_number        VARCHAR(20)         NOT NULL UNIQUE, -- dipakai untuk login
    email               VARCHAR(150)        NULL UNIQUE,
    identity_number     VARCHAR(30)         NULL,            -- No. KTP/NIK
    password_hash       VARCHAR(255)        NOT NULL,         -- BCrypt/Argon2, JANGAN plaintext
    pin_hash            VARCHAR(255)        NULL,             -- PIN transaksi (6 digit, di-hash)
    date_of_birth       DATE                NULL,
    address             TEXT                NULL,
    status              ENUM('active','inactive','blocked','pending_verification')
                                            NOT NULL DEFAULT 'pending_verification',
    kyc_status          ENUM('unverified','verified','rejected')
                                            NOT NULL DEFAULT 'unverified',
    created_at          DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP
                                            ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_users_phone (phone_number),
    INDEX idx_users_status (status)
) ENGINE=InnoDB;

-- =====================================================================
-- 2. AUTH SESSIONS / TOKENS (login token, device tracking)
-- =====================================================================
CREATE TABLE auth_sessions (
    session_id      BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT UNSIGNED     NOT NULL,
    access_token    VARCHAR(500)        NOT NULL,
    device_info     VARCHAR(255)        NULL,
    ip_address      VARCHAR(45)         NULL,
    is_revoked      TINYINT(1)          NOT NULL DEFAULT 0,
    created_at      DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at      DATETIME            NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_session_user (user_id),
    INDEX idx_session_token (access_token(191))
) ENGINE=InnoDB;

-- =====================================================================
-- 3. ACCOUNTS (Rekening)
-- =====================================================================
CREATE TABLE accounts (
    account_id          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id             BIGINT UNSIGNED     NOT NULL,
    account_number      VARCHAR(20)         NOT NULL UNIQUE,
    account_type        ENUM('savings','current') NOT NULL DEFAULT 'savings',
    currency            CHAR(3)             NOT NULL DEFAULT 'IDR',
    balance             DECIMAL(18,2)       NOT NULL DEFAULT 0.00,
    status              ENUM('active','frozen','closed') NOT NULL DEFAULT 'active',
    opened_at           DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP
                                            ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE RESTRICT,
    INDEX idx_account_number (account_number),
    INDEX idx_account_user (user_id)
) ENGINE=InnoDB;

-- =====================================================================
-- 4. VIP CARDS (Kartu debit/virtual milik rekening)
-- =====================================================================
CREATE TABLE cards (
    card_id             BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    account_id          BIGINT UNSIGNED     NOT NULL,
    card_number         VARCHAR(25)         NOT NULL UNIQUE, -- simpan masked/tokenized
    card_type           ENUM('debit','virtual') NOT NULL DEFAULT 'virtual',
    card_holder_name    VARCHAR(150)        NOT NULL,
    expiry_month        TINYINT UNSIGNED    NOT NULL,
    expiry_year         SMALLINT UNSIGNED   NOT NULL,
    cvv_hash            VARCHAR(255)        NOT NULL,
    pin_hash            VARCHAR(255)        NULL,
    status              ENUM('active','blocked','expired') NOT NULL DEFAULT 'active',
    created_at          DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES accounts(account_id) ON DELETE CASCADE,
    INDEX idx_card_account (account_id)
) ENGINE=InnoDB;

-- =====================================================================
-- 5. MASTER BANK (untuk transfer ke bank lain)
-- =====================================================================
CREATE TABLE banks (
    bank_id             INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    bank_code           VARCHAR(10)         NOT NULL UNIQUE, -- kode SKN/RTGS
    bank_name           VARCHAR(100)        NOT NULL
) ENGINE=InnoDB;

-- =====================================================================
-- 6. MASTER BILLER (untuk pembayaran tagihan)
-- =====================================================================
CREATE TABLE billers (
    biller_id           INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    biller_code         VARCHAR(20)         NOT NULL UNIQUE,
    biller_name         VARCHAR(100)        NOT NULL,
    category            ENUM('listrik','air','internet','telepon','tv_kabel','pendidikan','asuransi','lainnya')
                                            NOT NULL DEFAULT 'lainnya',
    is_active           TINYINT(1)          NOT NULL DEFAULT 1
) ENGINE=InnoDB;

-- =====================================================================
-- 7. MASTER VOUCHER (pulsa, data, game, dsb)
-- =====================================================================
CREATE TABLE vouchers (
    voucher_id          INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    voucher_name        VARCHAR(100)        NOT NULL,
    category            ENUM('pulsa','paket_data','game','ewallet','lainnya') NOT NULL,
    provider            VARCHAR(50)         NULL,           -- Telkomsel, XL, dsb
    nominal             DECIMAL(18,2)       NOT NULL,
    price               DECIMAL(18,2)       NOT NULL,       -- harga jual (bisa beda dari nominal krn ada margin)
    stock               INT UNSIGNED        NOT NULL DEFAULT 0,
    is_active           TINYINT(1)          NOT NULL DEFAULT 1
) ENGINE=InnoDB;

-- =====================================================================
-- 8. TRANSACTIONS (Ledger utama - SEMUA transaksi tercatat di sini)
-- =====================================================================
CREATE TABLE transactions (
    transaction_id      BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    reference_number    VARCHAR(40)         NOT NULL UNIQUE, -- nomor referensi unik utk user
    account_id          BIGINT UNSIGNED     NOT NULL,
    transaction_type     ENUM(
                            'transfer_internal_out',
                            'transfer_internal_in',
                            'transfer_external',
                            'bill_payment',
                            'voucher_purchase',
                            'card_transaction',
                            'top_up',
                            'admin_fee'
                          )                 NOT NULL,
    amount              DECIMAL(18,2)       NOT NULL,
    admin_fee           DECIMAL(18,2)       NOT NULL DEFAULT 0,
    balance_before       DECIMAL(18,2)       NOT NULL,
    balance_after        DECIMAL(18,2)       NOT NULL,
    description         VARCHAR(255)        NULL,
    status              ENUM('pending','success','failed','reversed') NOT NULL DEFAULT 'pending',
    created_at          DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES accounts(account_id) ON DELETE RESTRICT,
    INDEX idx_txn_account (account_id),
    INDEX idx_txn_type (transaction_type),
    INDEX idx_txn_created (created_at)
) ENGINE=InnoDB;

-- =====================================================================
-- 9. TRANSFER ANTAR REKENING VIP (Internal)
-- =====================================================================
CREATE TABLE transfers_internal (
    transfer_id             BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    transaction_id_out      BIGINT UNSIGNED     NOT NULL,   -- baris di transactions utk pengirim
    transaction_id_in       BIGINT UNSIGNED     NOT NULL,   -- baris di transactions utk penerima
    source_account_id       BIGINT UNSIGNED     NOT NULL,
    destination_account_id  BIGINT UNSIGNED     NOT NULL,
    amount                  DECIMAL(18,2)       NOT NULL,
    notes                   VARCHAR(255)        NULL,
    created_at              DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (transaction_id_out) REFERENCES transactions(transaction_id),
    FOREIGN KEY (transaction_id_in) REFERENCES transactions(transaction_id),
    FOREIGN KEY (source_account_id) REFERENCES accounts(account_id),
    FOREIGN KEY (destination_account_id) REFERENCES accounts(account_id)
) ENGINE=InnoDB;

-- =====================================================================
-- 10. TRANSFER KE BANK LAIN (External)
-- =====================================================================
CREATE TABLE transfers_external (
    transfer_id                 BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    transaction_id              BIGINT UNSIGNED     NOT NULL,
    source_account_id           BIGINT UNSIGNED     NOT NULL,
    bank_id                     INT UNSIGNED        NOT NULL,
    destination_account_number  VARCHAR(30)         NOT NULL,
    destination_account_name    VARCHAR(150)        NOT NULL,
    amount                      DECIMAL(18,2)       NOT NULL,
    admin_fee                   DECIMAL(18,2)       NOT NULL DEFAULT 0,
    notes                       VARCHAR(255)        NULL,
    created_at                  DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (transaction_id) REFERENCES transactions(transaction_id),
    FOREIGN KEY (source_account_id) REFERENCES accounts(account_id),
    FOREIGN KEY (bank_id) REFERENCES banks(bank_id)
) ENGINE=InnoDB;

-- =====================================================================
-- 11. PEMBAYARAN TAGIHAN (Bill Payment)
-- =====================================================================
CREATE TABLE bill_payments (
    payment_id          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    transaction_id      BIGINT UNSIGNED     NOT NULL,
    account_id          BIGINT UNSIGNED     NOT NULL,
    biller_id           INT UNSIGNED        NOT NULL,
    customer_number     VARCHAR(50)         NOT NULL,  -- no pelanggan/ID pelanggan
    period              VARCHAR(20)         NULL,      -- misal periode tagihan "2026-06"
    amount              DECIMAL(18,2)       NOT NULL,
    admin_fee           DECIMAL(18,2)       NOT NULL DEFAULT 0,
    created_at          DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (transaction_id) REFERENCES transactions(transaction_id),
    FOREIGN KEY (account_id) REFERENCES accounts(account_id),
    FOREIGN KEY (biller_id) REFERENCES billers(biller_id)
) ENGINE=InnoDB;

-- =====================================================================
-- 12. PEMBELIAN VOUCHER
-- =====================================================================
CREATE TABLE voucher_purchases (
    purchase_id         BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    transaction_id      BIGINT UNSIGNED     NOT NULL,
    account_id          BIGINT UNSIGNED     NOT NULL,
    voucher_id          INT UNSIGNED        NOT NULL,
    target_number       VARCHAR(20)         NULL,   -- no HP tujuan (jika pulsa/data)
    voucher_code        VARCHAR(100)        NULL,   -- kode voucher hasil pembelian
    amount              DECIMAL(18,2)       NOT NULL,
    created_at          DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (transaction_id) REFERENCES transactions(transaction_id),
    FOREIGN KEY (account_id) REFERENCES accounts(account_id),
    FOREIGN KEY (voucher_id) REFERENCES vouchers(voucher_id)
) ENGINE=InnoDB;

-- =====================================================================
-- 13. TRANSAKSI VIPCARD (misal EDC/QRIS/online merchant)
-- =====================================================================
CREATE TABLE card_transactions (
    card_txn_id         BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    transaction_id      BIGINT UNSIGNED     NOT NULL,
    card_id             BIGINT UNSIGNED     NOT NULL,
    merchant_name       VARCHAR(150)        NULL,
    merchant_category   VARCHAR(50)         NULL,
    amount              DECIMAL(18,2)       NOT NULL,
    created_at          DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (transaction_id) REFERENCES transactions(transaction_id),
    FOREIGN KEY (card_id) REFERENCES cards(card_id)
) ENGINE=InnoDB;

-- =====================================================================
-- 14. APP CONFIG / BRANDING (utk fitur "Konfigurasi/Branding")
-- =====================================================================
CREATE TABLE app_configs (
    config_key          VARCHAR(50)         NOT NULL PRIMARY KEY,
    config_value        TEXT                NOT NULL,
    description         VARCHAR(255)        NULL,
    updated_at          DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP
                                            ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- =====================================================================
-- 15. AUDIT LOG (opsional tapi disarankan untuk aplikasi finansial)
-- =====================================================================
CREATE TABLE audit_logs (
    log_id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id             BIGINT UNSIGNED     NULL,
    action              VARCHAR(100)        NOT NULL,   -- e.g. "LOGIN", "TRANSFER", "UPDATE_PROFILE"
    description         TEXT                NULL,
    ip_address          VARCHAR(45)         NULL,
    created_at          DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_audit_user (user_id),
    INDEX idx_audit_created (created_at)
) ENGINE=InnoDB;

SET FOREIGN_KEY_CHECKS = 1;

-- =====================================================================
-- SEED DATA CONTOH (opsional)
-- =====================================================================
INSERT INTO banks (bank_code, bank_name) VALUES
('008', 'Bank Mandiri'),
('002', 'Bank BRI'),
('009', 'Bank BNI'),
('014', 'Bank BCA');

INSERT INTO billers (biller_code, biller_name, category) VALUES
('PLN01', 'PLN Prabayar/Pascabayar', 'listrik'),
('PDAM01', 'PDAM Air Bersih', 'air'),
('INDIHOME01', 'Indihome', 'internet');

INSERT INTO vouchers (voucher_name, category, provider, nominal, price, stock) VALUES
('Pulsa Telkomsel 25.000', 'pulsa', 'Telkomsel', 25000, 26000, 100),
('Paket Data XL 10GB', 'paket_data', 'XL', 10, 45000, 100);

INSERT INTO app_configs (config_key, config_value, description) VALUES
('primary_color', '#FF5BE7FF', 'Warna primary aplikasi (glassmorphism theme)'),
('app_name', 'VIP Mobile', 'Nama brand aplikasi');
