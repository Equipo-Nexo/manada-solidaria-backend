UPDATE notification
SET title   = '🚨 Vieron un animal en la calle',
    message = 'Vieron un animal en {location}. Revisá si puede ser tu mascota perdida.'
WHERE type = 'SIMILAR_ANIMAL_RECENTLY_LOST';

-- ROLLBACK
-- UPDATE notification
-- SET title   = '🚨 ¡Publicaron un animal perdido!',
--     message = 'Se publicó un animal en {location}. Revisá si puede ser tu mascota perdida.'
-- WHERE type = 'SIMILAR_ANIMAL_RECENTLY_LOST';
