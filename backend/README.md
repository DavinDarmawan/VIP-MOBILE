IBS Core — Minimal Backend (Node.js + Express)

This minimal backend is for local development and prototyping. It connects to the MySQL database started via Docker Compose (`ibs-db`) and exposes a few simple endpoints:

- POST /api/auth/register  { full_name, phone_number, password }
- POST /api/auth/login     { phone_number, password }
- GET  /api/accounts/:accountId/transactions
- GET  /health

How to run (development)

1) Copy env example:
   cp backend/.env.example backend/.env
   # Edit values if needed

2) From repository root (where docker-compose.yml is), run:
   docker-compose up -d --build

3) The backend will be available at http://localhost:3000

Notes
- This backend is intentionally minimal and does not implement production security hardening.
- Passwords are hashed with bcrypt.
- JWT secret is read from env (BACKEND_JWT_SECRET). Change it for any real testing.

Backup & restore

- A simple PowerShell helper to backup the database is provided at `docker/backup_db.ps1`.
- Usage (from project root):
  - `powershell -ExecutionPolicy Bypass -File .\docker\backup_db.ps1 -OutDir .\backups`

Migrations behavior

- On first-run the MySQL image will initialize the database using `docker/mysql/ibs_core_schema.sql`.
- The backend's migration runner (`backend/src/migrate.js`) will detect if the database already contains tables and will mark migration files as applied to avoid re-running the initial schema. New migrations (files in `backend/migrations/*.sql`) will be executed on first backend startup when appropriate.


