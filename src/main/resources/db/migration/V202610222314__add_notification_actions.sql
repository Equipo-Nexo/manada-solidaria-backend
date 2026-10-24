ALTER TABLE notification
ADD COLUMN actions JSON NULL,
DROP COLUMN redirect_to;

UPDATE notification
SET actions = JSON_ARRAY(
    JSON_OBJECT(
        'type', 'REDIRECT',
        'title', 'Ver publicación',
        'order', '1',
        'url', '/animal/detalle/{postId}'
    ),
    JSON_OBJECT(
        'type', 'CLOSE',
        'title', 'Cerrar',
        'order', '0'
    )
)
WHERE id = UUID_TO_BIN('72b69587-ec7e-4d78-b7e4-07dd6c76826e');

-- ALTER TABLE notification DROP COLUMN actions;