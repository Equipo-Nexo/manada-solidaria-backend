INSERT INTO notification (
    id,
    created_at,
    title,
    message,
    type,
    icon,
    redirect_to
)
VALUES
(
    UUID_TO_BIN('5a1c9e3e-2f4b-4d7a-9c61-8b0e2f6d4a17'),
    '2026-10-03 18:30:00.000000',
    '🚨 ¡Publicaron un animal perdido!',
    'Se publicó un animal en {location}. Revisá si puede ser tu mascota perdida.',
    'SIMILAR_ANIMAL_RECENTLY_LOST',
    NULL,
    '/animal/detalle/{postId}'
);

-- ROLLBACK
-- DELETE FROM notification WHERE type = 'SIMILAR_ANIMAL_RECENTLY_LOST';
