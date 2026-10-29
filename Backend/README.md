# Backend

Spring Boot API backed by Supabase PostgreSQL.

## Local setup

1. In Supabase SQL Editor, create the application schema:

   ```sql
   CREATE SCHEMA IF NOT EXISTS biblioteca;
   ```

2. Copy `.env.example` to `.env` in this directory and set `DB_PASSWORD`,
   `ADMIN_PASSWORD`, and `JWT_SECRET` to fresh values. Do not commit `.env`.
   Generate a Base64-encoded 32-byte JWT secret in PowerShell with:

   ```powershell
   [Convert]::ToBase64String([Security.Cryptography.RandomNumberGenerator]::GetBytes(32))
   ```

3. Run from `Backend`:

   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

The Supabase connection uses the shared pooler over TLS. Database credentials,
admin password, JWT secret, and allowed CORS origins are supplied through
environment variables.

## Deploy on Render

The repository-root `render.yaml` and this directory's `Dockerfile` describe
the Spring Boot service. In Render, create a Blueprint from the repository and
provide the prompted `DB_PASSWORD`, `ADMIN_PASSWORD`, and `JWT_SECRET` values.
Rotate the database password that was previously shared before using it.

The backend creates/updates application tables in the `biblioteca` schema.
Create that schema in Supabase before the first deployment. The Angular
frontend is hosted separately and its production API URL must match the
Render service URL.
