-- =========================================================
-- 1. NOTIFICACIONES (plantillas, una por tipo)
-- =========================================================

MERGE INTO NOTIFICATION (
    ID, TITLE, MESSAGE, ICON, ACTIONS, TYPE, CREATED_AT
)
KEY (ID)
VALUES (
    '44444444-4444-4444-4444-444444444441',
    'Mascota perdida',
    'Se perdio una mascota cerca tuyo',
    'lost.png',
    JSON '[
        {
            "type": "REDIRECT",
            "title": "Ver publicación",
            "order": 1,
            "url": "/animal-posts/{postId}"
        },
        {
            "type": "CLOSE",
            "title": "Cerrar",
            "order": 0
        }
    ]',
    'LOST_PET',
    '2025-03-01 10:00:00'
);

MERGE INTO NOTIFICATION (
    ID, TITLE, MESSAGE, ICON, ACTIONS, TYPE, CREATED_AT
)
KEY (ID)
VALUES (
    '44444444-4444-4444-4444-444444444442',
    'Necesitan transporte',
    'Una publicacion necesita transporte',
    'carriage.png',
    JSON '[
        {
            "type": "REDIRECT",
            "title": "Ver publicación",
            "order": 1,
            "url": "/animal-posts/{postId}"
        },
        {
            "type": "CLOSE",
            "title": "Cerrar",
            "order": 0
        }
    ]',
    'NEW_CARRIAGE_REQUEST',
    '2025-03-01 10:00:00'
);

-- =========================================================
-- 2. CAMPANITA DE ADMIN (IN_APP)
--    La mas vieja esta leida, las dos mas nuevas no.
--    Los payloads tienen los parametros ya reemplazados.
-- =========================================================

MERGE INTO NOTIFICATION_DELIVERY (
    ID, RECIPIENT_ID, CHANNEL, CREATED_AT, TYPE, PAYLOAD
)
KEY (ID)
VALUES (
    '55555555-5555-5555-5555-555555555551',
    '22222222-2222-2222-2222-222222222222',
    'IN_APP',
    '2025-03-10 10:00:00',
    'LOST_PET',
    JSON '{
        "title": "Mascota perdida",
        "message": "Se perdio una mascota cerca tuyo",
        "icon": "lost.png",
        "actions": [
            {
                "type": "REDIRECT",
                "title": "Ver publicación",
                "order": 1,
                "url": "/animal-posts/lost"
            },
            {
                "type": "CLOSE",
                "title": "Cerrar",
                "order": 0
            }
        ]
    }'
);

MERGE INTO NOTIFICATION_DELIVERY (
    ID, RECIPIENT_ID, CHANNEL, CREATED_AT, TYPE, PAYLOAD
)
KEY (ID)
VALUES (
    '55555555-5555-5555-5555-555555555553',
    '22222222-2222-2222-2222-222222222222',
    'IN_APP',
    '2025-03-11 10:00:00',
    'LOST_PET',
    JSON '{
        "title": "Mascota perdida",
        "message": "Se perdio una mascota cerca tuyo",
        "icon": "lost.png",
        "actions": [
            {
                "type": "REDIRECT",
                "title": "Ver publicación",
                "order": 1,
                "url": "/animal-posts/lost"
            },
            {
                "type": "CLOSE",
                "title": "Cerrar",
                "order": 0
            }
        ]
    }'
);

MERGE INTO NOTIFICATION_DELIVERY (
    ID, RECIPIENT_ID, CHANNEL, CREATED_AT, TYPE, PAYLOAD
)
KEY (ID)
VALUES (
    '55555555-5555-5555-5555-555555555552',
    '22222222-2222-2222-2222-222222222222',
    'IN_APP',
    '2025-03-12 10:00:00',
    'NEW_CARRIAGE_REQUEST',
    JSON '{
        "title": "Necesitan transporte para Firulais",
        "message": "Firulais necesita que lo lleven",
        "icon": "carriage.png",
        "actions": [
            {
                "type": "REDIRECT",
                "title": "Ver publicación",
                "order": 1,
                "url": "/animal-posts/a3f9c001"
            },
            {
                "type": "CLOSE",
                "title": "Cerrar",
                "order": 0
            }
        ]
    }'
);

-- =========================================================
-- 3. RUIDO A PROPOSITO
--    Ambas son mas nuevas que las de la campanita de admin.
-- =========================================================

-- PUSH de admin: mismo usuario, otro canal.
MERGE INTO NOTIFICATION_DELIVERY (
    ID, RECIPIENT_ID, CHANNEL, CREATED_AT, TYPE, PAYLOAD
)
KEY (ID)
VALUES (
    '55555555-5555-5555-5555-555555555554',
    '22222222-2222-2222-2222-222222222222',
    'PUSH',
    '2025-03-13 10:00:00',
    'LOST_PET',
    JSON '{
        "title": "Mascota perdida",
        "message": "Se perdio una mascota cerca tuyo",
        "icon": "lost.png",
        "actions": [
            {
                "type": "REDIRECT",
                "title": "Ver publicación",
                "order": 1,
                "url": "/animal-posts/lost"
            },
            {
                "type": "CLOSE",
                "title": "Cerrar",
                "order": 0
            }
        ]
    }'
);

-- IN_APP de NOTADMIN: mismo canal, otro usuario.
MERGE INTO NOTIFICATION_DELIVERY (
    ID, RECIPIENT_ID, CHANNEL, CREATED_AT, TYPE, PAYLOAD
)
KEY (ID)
VALUES (
    '55555555-5555-5555-5555-555555555555',
    '22222222-2222-2222-2222-222222222223',
    'IN_APP',
    '2025-03-14 10:00:00',
    'LOST_PET',
    JSON '{
        "title": "Mascota perdida",
        "message": "Se perdio una mascota cerca tuyo",
        "icon": "lost.png",
        "actions": [
            {
                "type": "REDIRECT",
                "title": "Ver publicación",
                "order": 1,
                "url": "/animal-posts/lost"
            },
            {
                "type": "CLOSE",
                "title": "Cerrar",
                "order": 0
            }
        ]
    }'
);

-- =========================================================
-- 4. HISTORIAL DE ESTADOS
--    Solo la "...551" llega a READ.
-- =========================================================

MERGE INTO NOTIFICATION_STATUS_HISTORY (
    ID, STATUS, FINISHED_AT, CREATED_AT, NOTIFICATION_DELIVERY_ID
)
KEY (ID)
VALUES
(
    '66666666-6666-6666-6666-666666666611',
    'PENDING', NULL, '2025-03-10 10:00:00',
    '55555555-5555-5555-5555-555555555551'
),
(
    '66666666-6666-6666-6666-666666666612',
    'SENT', NULL, '2025-03-10 10:00:01',
    '55555555-5555-5555-5555-555555555551'
),
(
    '66666666-6666-6666-6666-666666666613',
    'READ', NULL, '2025-03-10 11:00:00',
    '55555555-5555-5555-5555-555555555551'
),
(
    '66666666-6666-6666-6666-666666666631',
    'PENDING', NULL, '2025-03-11 10:00:00',
    '55555555-5555-5555-5555-555555555553'
),
(
    '66666666-6666-6666-6666-666666666632',
    'SENT', NULL, '2025-03-11 10:00:01',
    '55555555-5555-5555-5555-555555555553'
),
(
    '66666666-6666-6666-6666-666666666621',
    'PENDING', NULL, '2025-03-12 10:00:00',
    '55555555-5555-5555-5555-555555555552'
),
(
    '66666666-6666-6666-6666-666666666622',
    'SENT', NULL, '2025-03-12 10:00:01',
    '55555555-5555-5555-5555-555555555552'
),
(
    '66666666-6666-6666-6666-666666666641',
    'SENT', NULL, '2025-03-13 10:00:00',
    '55555555-5555-5555-5555-555555555554'
),
(
    '66666666-6666-6666-6666-666666666651',
    'SENT', NULL, '2025-03-14 10:00:00',
    '55555555-5555-5555-5555-555555555555'
);