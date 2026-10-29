# Biblioteca Universitaria

Monorepo for the library application:

- `Backend/` — Spring Boot API, deployed on Render and connected to Supabase PostgreSQL.
- `Frontend/app/` — Angular client, served by a Cloudflare Worker.

## Deployment

1. **Rotate the Supabase database password** that was previously shared. Do not
   put credentials in this repository or in chat.
2. In Supabase SQL Editor, run `CREATE SCHEMA IF NOT EXISTS biblioteca;`.
3. In Render, create a Blueprint from this repository using `render.yaml`.
   Enter the new database password, an admin password, and a Base64-encoded
   random 32-byte `JWT_SECRET` when prompted. The service is named
   `biblioteca-universitaria-api`; if Render assigns a different public URL,
   update `Frontend/app/src/environments/environment.production.ts` to match
   and redeploy the frontend.
4. Configure the Cloudflare Worker build command as `npm run build` in
   `Frontend/app`, then deploy with Wrangler from that directory. The
   `wrangler.toml` serves Angular's `dist/app/browser` output and falls back
   to `index.html` for client-side routes.

For local development, set `Backend/.env` from `Backend/.env.example` and run
the Spring Boot API from `Backend`. Angular's development configuration calls
`http://localhost:8080`; its production build calls the Render API.
