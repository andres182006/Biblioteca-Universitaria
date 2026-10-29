# Biblioteca-Universitaria

## Conectar el backend a Supabase

El backend se conecta a PostgreSQL mediante JDBC. Para usar Supabase:

1. En Supabase, abre **SQL Editor** y crea el esquema que utiliza la aplicación:

   ```sql
   CREATE SCHEMA IF NOT EXISTS biblioteca;
   ```

   La configuración JPA existente crea/actualiza las tablas en ese esquema.
2. Desde la carpeta `Backend`, copia `.env.example` como `.env` y completa los
   datos de **Shared pooler** de Supabase:

   ```powershell
   Copy-Item .env.example .env
   ```

   En `.env`, configura `DB_URL` con el formato
   `jdbc:postgresql://<host-del-pooler>:5432/postgres?sslmode=require`,
   `DB_USERNAME` con el usuario del pooler y `DB_PASSWORD` con la contraseña
   de la base de datos. No incluyas la contraseña en la URL; al ir en una
   variable separada no necesita codificación porcentual. Completa también
   `ADMIN_PASSWORD` y `JWT_SECRET`.
3. Ejecuta el backend desde `Backend`:

   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

`.env` está excluido de Git; no compartas ni subas ese archivo. Si no existe,
la URL y el usuario conservan los valores locales anteriores, y la contraseña
se sigue leyendo de `DB_PASSWORD`.

La contraseña de base de datos se compartió en el mensaje de configuración.
Rótala desde Supabase antes de conectar y guarda únicamente la nueva en `.env`.
Puedes administrar la base de datos desde Supabase Studio; pgAdmin ya no es
necesario para que el backend funcione.