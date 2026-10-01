-- Metricas de organizacion de Manada Solidaria (MySQL). Todo agregado: ninguna fila por persona.
-- Son las mismas 18 consultas que arma scripts/crear_dashboard.py en Metabase, en SQL plano.
--
-- PERIODO: las marcadas "filtra por fecha" aceptan acotar por fecha de REPORTE (created_at) de la tabla principal.
-- En Metabase eso es el filtro `fecha` ([[ AND {{fecha}} ]]); aca esta como linea comentada para descomentar.
-- "Resuelto" = estado VIGENTE hoy (history.finished_at IS NULL): RESCUED, FOUND o ADOPTED.

-- 1. Rescatados (filtra por fecha)
SELECT COUNT(*) AS rescatados
FROM animal_post
JOIN lost_post l ON l.id = animal_post.id AND l.has_owner = 0
JOIN lost_post_status_history h ON h.lost_post_id = l.id AND h.finished_at IS NULL AND h.status = 'RESCUED'
WHERE 1=1
-- AND animal_post.created_at >= '2026-09-01' AND animal_post.created_at < '2026-10-01'
;

-- 2. Encontrados: mascotas con dueño reencontradas (filtra por fecha)
SELECT COUNT(*) AS reencontradas
FROM animal_post
JOIN lost_post l ON l.id = animal_post.id AND l.has_owner = 1
JOIN lost_post_status_history h ON h.lost_post_id = l.id AND h.finished_at IS NULL AND h.status = 'FOUND'
WHERE 1=1
-- AND animal_post.created_at >= '2026-09-01' AND animal_post.created_at < '2026-10-01'
;

-- 3. Adoptados (filtra por fecha)
SELECT COUNT(*) AS adoptados
FROM animal_post
JOIN adoption_post a ON a.id = animal_post.id
JOIN adoption_post_status_history h ON h.adoption_post_id = a.id AND h.finished_at IS NULL AND h.status = 'ADOPTED'
WHERE 1=1
-- AND animal_post.created_at >= '2026-09-01' AND animal_post.created_at < '2026-10-01'
;

-- 4. Usuarios registrados desde el inicio
SELECT COUNT(*) AS usuarios FROM users;

-- 5. Veterinarias asociadas hoy
SELECT COUNT(*) AS veterinarias FROM vet_information;

-- 6. Campañas creadas desde el inicio: recaudaciones, donaciones y jornadas
SELECT
  (SELECT COUNT(*) FROM fundraising_campaign) + (SELECT COUNT(*) FROM donation_campaign) + (SELECT COUNT(*) FROM news_campaign) AS campanias;

-- 7. Resultado de los casos (filtra por fecha)
SELECT 'En la calle' AS caso, COUNT(*) AS reportados, SUM(h.status = 'RESCUED') AS resueltos,
       ROUND(100 * SUM(h.status = 'RESCUED') / COUNT(*), 1) AS tasa
FROM animal_post
JOIN lost_post l ON l.id = animal_post.id AND l.has_owner = 0
JOIN lost_post_status_history h ON h.lost_post_id = l.id AND h.finished_at IS NULL
WHERE 1=1
-- AND animal_post.created_at >= '2026-09-01' AND animal_post.created_at < '2026-10-01'
UNION ALL
SELECT 'Perdidos con dueño', COUNT(*), SUM(h.status = 'FOUND'), ROUND(100 * SUM(h.status = 'FOUND') / COUNT(*), 1)
FROM animal_post
JOIN lost_post l ON l.id = animal_post.id AND l.has_owner = 1
JOIN lost_post_status_history h ON h.lost_post_id = l.id AND h.finished_at IS NULL
WHERE 1=1
-- AND animal_post.created_at >= '2026-09-01' AND animal_post.created_at < '2026-10-01'
UNION ALL
SELECT 'En adopción', COUNT(*), SUM(h.status = 'ADOPTED'), ROUND(100 * SUM(h.status = 'ADOPTED') / COUNT(*), 1)
FROM animal_post
JOIN adoption_post a ON a.id = animal_post.id
JOIN adoption_post_status_history h ON h.adoption_post_id = a.id AND h.finished_at IS NULL
WHERE 1=1
-- AND animal_post.created_at >= '2026-09-01' AND animal_post.created_at < '2026-10-01'
;

-- 8. Tiempo medio hasta resolver un caso, en dias (filtra por fecha). TIMESTAMPDIFF en SECOND: en HOUR trunca.
SELECT 'En la calle (rescatados)' AS caso, COUNT(*) AS resueltos,
       ROUND(AVG(TIMESTAMPDIFF(SECOND, animal_post.created_at, h.created_at)) / 86400, 1) AS dias
FROM animal_post
JOIN lost_post l ON l.id = animal_post.id AND l.has_owner = 0
JOIN lost_post_status_history h ON h.lost_post_id = l.id AND h.status = 'RESCUED'
WHERE 1=1
-- AND animal_post.created_at >= '2026-09-01' AND animal_post.created_at < '2026-10-01'
UNION ALL
SELECT 'Perdidos con dueño (encontrados)', COUNT(*), ROUND(AVG(TIMESTAMPDIFF(SECOND, animal_post.created_at, h.created_at)) / 86400, 1)
FROM animal_post
JOIN lost_post l ON l.id = animal_post.id AND l.has_owner = 1
JOIN lost_post_status_history h ON h.lost_post_id = l.id AND h.status = 'FOUND'
WHERE 1=1
-- AND animal_post.created_at >= '2026-09-01' AND animal_post.created_at < '2026-10-01'
UNION ALL
SELECT 'En adopción (adoptados)', COUNT(*), ROUND(AVG(TIMESTAMPDIFF(SECOND, animal_post.created_at, h.created_at)) / 86400, 1)
FROM animal_post
JOIN adoption_post a ON a.id = animal_post.id
JOIN adoption_post_status_history h ON h.adoption_post_id = a.id AND h.status = 'ADOPTED'
WHERE 1=1
-- AND animal_post.created_at >= '2026-09-01' AND animal_post.created_at < '2026-10-01'
;

-- 9. Casos reportados por mes y tipo (filtra por fecha)
SELECT DATE_FORMAT(animal_post.created_at, '%Y-%m') AS mes,
       CASE WHEN a.id IS NOT NULL THEN 'En adopción' WHEN l.has_owner = 1 THEN 'Perdidos con dueño' ELSE 'En la calle' END AS tipo,
       COUNT(*) AS casos
FROM animal_post
LEFT JOIN lost_post l ON l.id = animal_post.id
LEFT JOIN adoption_post a ON a.id = animal_post.id
WHERE 1=1
-- AND animal_post.created_at >= '2026-01-01' AND animal_post.created_at < '2027-01-01'
GROUP BY mes, tipo
ORDER BY mes;

-- 10. Casos abiertos hoy
SELECT etiqueta AS estado, COUNT(*) AS casos FROM (
  SELECT CASE WHEN l.has_owner = 1
              THEN CASE h.status WHEN 'CREATED' THEN 'Perdidos: sin gestionar' ELSE 'Perdidos: en búsqueda' END
              ELSE CASE h.status WHEN 'CREATED' THEN 'En la calle: sin gestionar' ELSE 'En la calle: a rescatar' END END AS etiqueta
  FROM lost_post l
  JOIN lost_post_status_history h ON h.lost_post_id = l.id AND h.finished_at IS NULL AND h.status IN ('CREATED', 'SEARCHING', 'TO_RESCUE')
  UNION ALL
  SELECT CASE h.status WHEN 'CREATED' THEN 'Adopción: sin gestionar' ELSE 'Adopción: buscando familia' END
  FROM adoption_post_status_history h
  WHERE h.finished_at IS NULL AND h.status <> 'ADOPTED'
) abiertos
GROUP BY etiqueta
ORDER BY casos DESC;

-- 11. Tipo de animal (filtra por fecha)
SELECT CASE an.type WHEN 'DOG' THEN 'Perros' WHEN 'CAT' THEN 'Gatos' ELSE 'Otros' END AS tipo, COUNT(*) AS casos
FROM animal_post
JOIN animal an ON an.id = animal_post.animal_id
WHERE 1=1
-- AND animal_post.created_at >= '2026-01-01' AND animal_post.created_at < '2027-01-01'
GROUP BY tipo;

-- 12. Campañas creadas por mes y tipo
SELECT mes, tipo, COUNT(*) AS campanias FROM (
  SELECT DATE_FORMAT(created_at, '%Y-%m') AS mes, 'Recaudación' AS tipo FROM fundraising_campaign
  UNION ALL SELECT DATE_FORMAT(created_at, '%Y-%m'), 'Donaciones' FROM donation_campaign
  UNION ALL SELECT DATE_FORMAT(created_at, '%Y-%m'), 'Jornadas' FROM news_campaign
) c GROUP BY mes, tipo ORDER BY mes;

-- 13. Campañas de recaudación: meta, recaudado y % de la meta (filtra por fecha)
SELECT COUNT(*) AS cantidad, SUM(h.status = 'COMPLETED') AS completadas,
       SUM(amount_to_be_collected) AS meta, SUM(amount_collected) AS recaudado,
       ROUND(100 * SUM(amount_collected) / SUM(amount_to_be_collected), 1) AS pct
FROM fundraising_campaign
JOIN fundraising_campaign_status_history h ON h.fundraising_campaign_id = fundraising_campaign.id AND h.finished_at IS NULL
WHERE 1=1
-- AND fundraising_campaign.created_at >= '2026-01-01' AND fundraising_campaign.created_at < '2027-01-01'
;

-- 14. Campañas de donaciones: completadas (filtra por fecha)
SELECT COUNT(*) AS cantidad, SUM(h.status = 'COMPLETED') AS completadas,
       ROUND(100 * SUM(h.status = 'COMPLETED') / COUNT(*), 1) AS pct
FROM donation_campaign
JOIN donation_campaign_status_history h ON h.donation_campaign_id = donation_campaign.id AND h.finished_at IS NULL
WHERE 1=1
-- AND donation_campaign.created_at >= '2026-01-01' AND donation_campaign.created_at < '2027-01-01'
;

-- 15. Jornadas comunitarias por categoria (filtra por fecha)
SELECT CASE category WHEN 'CASTRATION' THEN 'Castración' WHEN 'VACCINATION' THEN 'Vacunación'
       WHEN 'DEWORMING' THEN 'Desparasitación' ELSE 'Otras' END AS categoria, COUNT(*) AS jornadas
FROM news_campaign
WHERE 1=1
-- AND news_campaign.created_at >= '2026-01-01' AND news_campaign.created_at < '2027-01-01'
GROUP BY categoria
ORDER BY jornadas DESC;

-- 16. Usuarios nuevos por mes (filtra por fecha)
SELECT DATE_FORMAT(created_at, '%Y-%m') AS mes, COUNT(*) AS usuarios
FROM users
WHERE 1=1
-- AND users.created_at >= '2026-01-01' AND users.created_at < '2027-01-01'
GROUP BY mes
ORDER BY mes;

-- 17. Roles especializados: personas que ofrecen ayuda (una persona puede tener mas de un rol)
SELECT CASE role WHEN 'RESCUER' THEN 'Rescatistas' WHEN 'VET' THEN 'Veterinarios' WHEN 'TRANSITIONAL_HOME' THEN 'Hogares de tránsito'
       WHEN 'CARRIAGE' THEN 'Transportistas' END AS rol, COUNT(DISTINCT profile_id) AS personas
FROM profile_roles
WHERE role <> 'COMMUNITY'
GROUP BY rol
ORDER BY personas DESC;

-- 18. Cobertura de la red veterinaria hoy
SELECT 'Veterinarias asociadas' AS cobertura, COUNT(*) AS veterinarias FROM vet_information
UNION ALL SELECT 'Con horarios cargados', COUNT(DISTINCT vet_id) FROM schedule
UNION ALL SELECT 'Atienden los sábados', COUNT(DISTINCT vet_id) FROM schedule WHERE day_of_week = 'SATURDAY'
UNION ALL SELECT 'Atienden los domingos', COUNT(DISTINCT vet_id) FROM schedule WHERE day_of_week = 'SUNDAY'
UNION ALL SELECT 'Cierran a las 20 hs o más tarde', COUNT(DISTINCT vet_id) FROM schedule WHERE closing_time >= '20:00:00';
