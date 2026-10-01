# Metabase: ver las métricas en local

1. Con Docker Desktop abierto, desde la raíz del repo (donde está el `pom.xml`) correr `cd metabase` y después `docker compose up -d` (en PowerShell, en dos líneas separadas). Este comando ya Levanta un MySQL propio llamado `db-metricas`, que ya trae las tablas y los datos de prueba cargados, y Metabase. La primera vez tarda un minuto. Abrir `http://localhost:3000` para el asistente inicial (esperar un poco). Si el puerto 3306 está ocupado por otro MySQL, antes de levantar correr `$env:DB_METRICAS_PORT="3307"`.
2. Crear el usuario de solo lectura: reemplazar `CAMBIAR_ESTA_CLAVE` por una clave en `grants.sql` y correr
   ```powershell
   docker cp grants.sql db-metricas:/tmp/grants.sql
   docker exec -it db-metricas mysql -uroot -proot -e "source /tmp/grants.sql"
   ```
3. En Metabase, conectar la base: Admin → Bases de datos → MySQL, host `db-metricas`, puerto `3306`, base `manada_solidaria`, usuario `metabase_ro` con la clave del paso anterior y, en opciones JDBC, `allowPublicKeyRetrieval=true&useSSL=false`.
4. Crear el dashboard. Hace falta tener Python instalado.
   1. En `http://localhost:3000/admin/settings/public-sharing`, dejar activado **Habilitar uso compartido público**.
   2. En PowerShell, dentro de la carpeta `metabase/`, correr los cuatro comandos. En el primero hay que poner el mail y la clave del usuario admin que se creó en el asistente de Metabase:
      ```powershell
      $r = Invoke-RestMethod -Method Post -Uri http://localhost:3000/api/session -ContentType "application/json" -Body '{"username":"<mail del admin>","password":"<clave>"}'
      $env:MB_SESSION = $r.id
      $env:DB_ID = "2"
      python scripts/crear_dashboard.py
      ```
      Los dos primeros inician sesión en Metabase; el tercero indica el número de la base de datos (`2`, el de `manada_solidaria`, se ve en la URL de Admin → Bases de datos); el cuarto crea las tarjetas y el dashboard.
   3. Si todo salió bien, el último comando imprime `dashcards: 22`.
   4. En Metabase, abrir **Nuestros análisis** → **Métricas Manada Solidaria**. Para obtener un link que se pueda embeber: ícono de compartir → **Link público**.

