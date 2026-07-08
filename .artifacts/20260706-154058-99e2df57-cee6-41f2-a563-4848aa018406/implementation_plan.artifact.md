# Implementation Plan - Full Offline Functionality & Business Logic Separation

This plan outlines the steps to implement all banking features (Balance, Transfer, Bills) using the local SQLite database and organizing the business logic into a dedicated "backend" structure within the app.

## Proposed Changes

### 1. Database & Schema Updates
Update DAOs and entities to support comprehensive banking operations.

#### [IBSDao.kt](file:///D:/2026/Pemrograman Mobile/UAS/IBS-CORE/app/src/main/java/com/example/vip_mobile/data/dao/IBSDao.kt)
- Add queries for:
    - Fetching all accounts for a user.
    - Transaction history retrieval.
    - Atomically updating balances (Transfer logic).
    - Finding accounts by phone number (for transfers).

#### [Transaksi.kt](file:///D:/2026/Pemrograman Mobile/UAS/IBS-CORE/app/src/main/java/com/example/vip_mobile/data/entity/Transaksi.kt)
- Add `keterangan` (note) and `no_rekening_tujuan` (destination account) fields.

---

### 2. Business Logic Layer (The "Local Backend")
Create a service layer to handle banking logic, separating it from the UI.

#### [NEW] [BankingService.kt](file:///D:/2026/Pemrograman Mobile/UAS/IBS-CORE/app/src/main/java/com/example/vip_mobile/data/service/BankingService.kt)
- Methods for `cekSaldo()`, `transferVIP()`, `transferBank()`, `bayarTagihan()`.
- This acts as the "Internal Backend" using SQLite.

---

### 3. UI Connectivity
Connect the buttons in `MainActivity` to their respective activities (which we will create or update).

#### [MainActivity.kt](file:///D:/2026/Pemrograman Mobile/UAS/IBS-CORE/app/src/main/java/com/example/vip_mobile/MainActivity.kt)
- Update click listeners to open:
    - `BalanceActivity`
    - `TransferActivity`
    - `BillsActivity`

#### [NEW] [BalanceActivity.kt](file:///D:/2026/Pemrograman Mobile/UAS/IBS-CORE/app/src/main/java/com/example/vip_mobile/BalanceActivity.kt)
- Display current balance and account number from SQLite.

#### [NEW] [TransferActivity.kt](file:///D:/2026/Pemrograman Mobile/UAS/IBS-CORE/app/src/main/java/com/example/vip_mobile/TransferActivity.kt)
- Form to input destination and amount.
- Use `BankingService` to execute transfer logic.

---

## Verification Plan

### Manual Verification
1. **Register & Login**: Ensure local auth still works.
2. **Cek Saldo**: Verify initial balance (e.g., set a default balance on registration).
3. **Transfer**:
    - Perform a transfer between two local accounts.
    - Verify balances of both accounts decrease/increase correctly in **Database Inspector**.
    - Check if a record is added to the `transaksi` table.
4. **Offline Mode**: Turn off internet and verify all features still work.
