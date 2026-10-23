ALTER TABLE notification
ADD COLUMN actions JSON NULL;

UPDATE notification
SET actions = JSON_ARRAY(
    JSON_OBJECT(
        'type', 'REDIRECT',
        'title', 'Ver publicación',
        'url', '/animal/detalle/{postId}'
    ),
    JSON_OBJECT(
        'type', 'CLOSE',
        'title', 'Cerrar'
    )
)
WHERE id = UUID_TO_BIN('72b69587-ec7e-4d78-b7e4-07dd6c76826e');

-- DROP TABLE IF EXISTS notification_actions;