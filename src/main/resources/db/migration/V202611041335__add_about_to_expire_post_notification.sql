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
    '2026-11-04 13:537:13.551601',
    'Pong',
    'Ping',
    'PING',
    NULL,
    NULL
);