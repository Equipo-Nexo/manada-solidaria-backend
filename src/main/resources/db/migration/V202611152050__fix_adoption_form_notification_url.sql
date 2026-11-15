UPDATE notification
SET actions = JSON_ARRAY(
    JSON_OBJECT(
        'type', 'REDIRECT',
        'title', 'Revisar formulario',
        'order', 1,
        'url', '/formularios-recibidos/{adoptionFormId}'
    ),
    JSON_OBJECT(
        'type', 'CLOSE',
        'title', 'Cerrar',
        'order', 0
    )
)
WHERE type = 'NEW_ADOPTION_FORM';

-- ROLLBACK
-- UPDATE notification
-- SET actions = JSON_ARRAY(
--     JSON_OBJECT(
--         'type', 'REDIRECT',
--         'title', 'Revisar formulario',
--         'order', 1,
--         'url', '/adoption-forms/{adoptionFormId}'
--     ),
--     JSON_OBJECT(
--         'type', 'CLOSE',
--         'title', 'Cerrar',
--         'order', 0
--     )
-- )
-- WHERE type = 'NEW_ADOPTION_FORM';