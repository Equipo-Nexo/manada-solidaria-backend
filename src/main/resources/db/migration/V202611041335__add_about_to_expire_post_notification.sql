INSERT INTO notification (
    id,
    created_at,
    message,
    title,
    type,
    actions
)
VALUES
(
    UUID_TO_BIN('3aa9b234-b53b-4658-968f-d735cd8a3d94'),
    '2026-11-04 13:53:13.551601',
    'Tu publicación sobre {animalName} se eliminará el {expirationDate}. Tocá «Renovar» para darle mayor visibilidad y evitar que se elimine.',
    'Tu publicación está a punto de vencer',
    'POST_ABOUT_TO_EXPIRE',
    JSON_ARRAY(
        JSON_OBJECT(
            'type', 'REQUEST',
            'title', 'Renovar',
            'order', 1,
            'url', '/animal-posts/{animalPostId}/repost',
            'httpMethod', 'POST',
            'headers', JSON_OBJECT(
                'Authorization', 'OTP {otp}'
            )
        ),
        JSON_OBJECT(
            'type', 'CLOSE',
            'title', 'Cerrar',
            'order', 0
        )
    )
);