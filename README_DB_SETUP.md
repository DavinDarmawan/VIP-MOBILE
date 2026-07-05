Tujuan

Menjalankan MySQL (MySQL 8) lokal menggunakan Docker Compose dan meng-inisialisasi database `ibs_core` menggunakan file schema yang sudah kamu desain (`docker/mysql/ibs_core_schema.sql`).

Checklist singkat

- [x] Tambahkan file `docker-compose.yml` di root project
- [x] Tambahkan file schema di `docker/mysql/ibs_core_schema.sql` (akan dieksekusi otomatis saat pertama kali container dijalankan)
- [x] Tambahkan file konfigurasi `.env` untuk kredensial default (ubah sebelum produksi)
- [x] Sertakan contoh perintah PowerShell untuk menjalankan dan memeriksa database

Langkah cepat (PowerShell)

1) Pastikan Docker Desktop berjalan di Windows.

2) (Opsional) Edit `.env` di root project dan ubah `MYSQL_ROOT_PASSWORD` agar lebih aman.

3) Jalankan docker-compose:

```powershell
cd "C:\Users\Davin Darmawan\AndroidStudioProjects\IBS-CORE"
docker-compose up -d
```

4) Periksa status container:

```powershell
docker ps --filter "name=ibs-db"
```

5) Tampilkan database yang dibuat (gunakan password di `.env`):

```powershell
# Ganti ChangeMeRootPwd123! jika kamu mengubah password di .env
docker exec -i ibs-db mysql -uroot -pChangeMeRootPwd123! -e "SHOW DATABASES;"
```

Catatan teknis

- File `docker/mysql/ibs_core_schema.sql` akan dieksekusi otomatis oleh image resmi MySQL ketika direktori data container kosong (first-run). Jika kamu merestart container setelah data sudah ada, file tersebut tidak dijalankan ulang — itu normal.

- Jika ingin mengimpor ulang schema (misal setelah perubahan), hapus volume `db_data` lalu jalankan ulang compose:

```powershell
# Hati-hati: ini akan menghapus semua data di database
docker-compose down
docker volume rm "ibs-core_db_data"
docker-compose up -d
```

- Jangan simpan kredensial produksi di `.env` yang di-commit ke Git. Gunakan secret manager atau CI/CD pipeline.

Bagian selanjutnya

Jika kamu mau, saya bisa:
- Buatkan contoh backend minimal (Node.js/Express atau PHP) yang menyediakan endpoint `auth/login` dan `accounts/{id}/transactions` menggunakan schema ini.
- Atau buatkan skrip migrasi (Flyway/Liquibase) untuk manajemen versi skema.

Beritahu pilihanmu, dan saya akan lanjutkan implementasinya.

