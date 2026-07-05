-- Seed data: banks, billers, vouchers, app_configs
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

