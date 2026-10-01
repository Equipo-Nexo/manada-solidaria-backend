-- Usuario de SOLO LECTURA para Metabase sobre la base de la app (MySQL).
-- Reemplazar CAMBIAR_ESTA_CLAVE por una clave larga y unica. No se guarda en ningun archivo del proyecto.
-- Regla: GRANT tabla por tabla y, donde hay datos personales, columna por columna. Nunca ON base.*
CREATE USER IF NOT EXISTS 'metabase_ro'@'%' IDENTIFIED BY 'CAMBIAR_ESTA_CLAVE';

-- Casos (sin telefono, nombre, descripcion ni dueno de la publicacion)
GRANT SELECT (id, created_at, updated_at, animal_id, location_id) ON manada_solidaria.animal_post TO 'metabase_ro'@'%';
GRANT SELECT ON manada_solidaria.lost_post TO 'metabase_ro'@'%';
GRANT SELECT ON manada_solidaria.lost_post_status_history TO 'metabase_ro'@'%';
GRANT SELECT ON manada_solidaria.adoption_post TO 'metabase_ro'@'%';
GRANT SELECT ON manada_solidaria.adoption_post_status_history TO 'metabase_ro'@'%';
GRANT SELECT (id, type, age, size, gender) ON manada_solidaria.animal TO 'metabase_ro'@'%';

-- Comunidad y red veterinaria (sin usernames, emails, telefonos ni direcciones)
GRANT SELECT (id, created_at) ON manada_solidaria.users TO 'metabase_ro'@'%';
GRANT SELECT ON manada_solidaria.profile_roles TO 'metabase_ro'@'%';
GRANT SELECT (id) ON manada_solidaria.vet_information TO 'metabase_ro'@'%';
GRANT SELECT ON manada_solidaria.schedule TO 'metabase_ro'@'%';

-- Campanas (sin titulo, descripcion, telefono ni dueno)
GRANT SELECT (id, created_at, finished_at, amount_collected, amount_to_be_collected, campaign_end_date) ON manada_solidaria.fundraising_campaign TO 'metabase_ro'@'%';
GRANT SELECT (id, created_at, finished_at, campaign_end_date) ON manada_solidaria.donation_campaign TO 'metabase_ro'@'%';
GRANT SELECT (id, created_at, finished_at, category, news_start_date_time, news_end_date_time) ON manada_solidaria.news_campaign TO 'metabase_ro'@'%';
GRANT SELECT ON manada_solidaria.fundraising_campaign_status_history TO 'metabase_ro'@'%';
GRANT SELECT ON manada_solidaria.donation_campaign_status_history TO 'metabase_ro'@'%';
GRANT SELECT ON manada_solidaria.news_campaign_status_history TO 'metabase_ro'@'%';

FLUSH PRIVILEGES;

-- Verificar (deben fallar con "command denied"):
--   SELECT username FROM users;  SELECT email FROM profile;  SELECT phone_number FROM vet_information;
--   SELECT owner_id FROM animal_post;  SELECT address FROM location;  DELETE FROM lost_post_status_history;

-- ROLLBACK (quitar el usuario):
-- DROP USER 'metabase_ro'@'%';
