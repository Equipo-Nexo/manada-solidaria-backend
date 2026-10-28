UPDATE notification
SET actions = JSON_ARRAY(
    JSON_OBJECT(
        'type', 'REDIRECT',
        'title', 'Ver publicación',
        'order', 1,
        'url', '/animal/detalle/{postId}'
    ),
    JSON_OBJECT(
        'type', 'CLOSE',
        'title', 'Cerrar',
        'order', 0
    )
)
WHERE id = UUID_TO_BIN('5a1c9e3e-2f4b-4d7a-9c61-8b0e2f6d4a17');

UPDATE notification
SET actions = JSON_ARRAY(
    JSON_OBJECT(
        'type', 'REDIRECT',
        'title', 'Ver colecta',
        'order', 1,
        'url', '/colectas/{fundraisingId}'
    ),
    JSON_OBJECT(
        'type', 'CLOSE',
        'title', 'Cerrar',
        'order', 0
    )
)
WHERE id = UUID_TO_BIN('cb3daadc-960e-46a4-aa6b-cb133ec477b7');

UPDATE notification
SET actions = JSON_ARRAY(
    JSON_OBJECT(
        'type', 'REDIRECT',
        'title', 'Ver publicación',
        'order', 1,
        'url', '/animal/detalle/{postId}'
    ),
    JSON_OBJECT(
        'type', 'CLOSE',
        'title', 'Cerrar',
        'order', 0
    )
)
WHERE id = UUID_TO_BIN('dd700d2e-7128-434f-95da-74ae91f2063e');