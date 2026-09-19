INSERT INTO notification (
    id,
    created_at,
    message,
    title,
    type,
    icon,
    redirect_to
)
VALUES
(
    UUID_TO_BIN('f39c1467-477d-4f3d-9f86-17b87ca72db1'),
    '2026-09-06 20:52:13.551601',
    'Pong',
    'Ping',
    'PING',
    NULL,
    NULL
),
(
    UUID_TO_BIN('72b69587-ec7e-4d78-b7e4-07dd6c76826e'),
    '2026-09-07 00:56:31.753404',
    'Se busca transporte para trasladar un nuevo animal publicado. ¿Podés ayudar?',
    '🚗 Se necesita ayuda con un traslado.',
    'NEW_CARRIAGE_REQUEST',
    NULL,
    '/animal/detalle/{postId}'
);