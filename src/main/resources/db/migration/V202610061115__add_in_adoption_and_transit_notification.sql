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
    UUID_TO_BIN('dd700d2e-7128-434f-95da-74ae91f2063e'),
    '2026-10-06 11:15:00.000000',
    '🏠 Un animal necesita hogar de tránsito',
    'Hay un animal buscando tránsito. ¿Podés recibirlo?',
    'IN_ADOPTION_AND_TRANSIT_PET',
    NULL,
    '/animal/detalle/{postId}'
);

-- ROLLBACK
-- DELETE FROM notification WHERE type = 'IN_ADOPTION_AND_TRANSIT_PET';
