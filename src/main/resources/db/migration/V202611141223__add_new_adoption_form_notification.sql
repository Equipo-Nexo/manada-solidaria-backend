INSERT INTO notification (
    id,
    created_at,
    title,
    message,
    type,
    actions
)
VALUES
(
    UUID_TO_BIN('e8b4f123-4567-89ab-cdef-0123456789ab'),
    NOW(),
    '¡Nuevo formulario de adopción!',
    '{applicantName} completó un formulario para adoptar a tu mascota. Ingresá para revisarlo.',
    'NEW_ADOPTION_FORM',
    JSON_ARRAY(
        JSON_OBJECT(
            'type', 'REDIRECT',
            'title', 'Revisar formulario',
            'order', 1,
            'url', '/adoption-forms/{adoptionFormId}'
        ),
        JSON_OBJECT(
            'type', 'CLOSE',
            'title', 'Cerrar',
            'order', 0
        )
    )
);

-- ROLLBACK
-- DELETE FROM notification WHERE type = 'NEW_ADOPTION_FORM';