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

