-- =========================================================
-- 1. UBICACIÓN
-- =========================================================

MERGE INTO LOCATION (
    ID,
    NAME,
    ADDRESS,
    LATITUDE,
    LONGITUDE
)
KEY (ID)
VALUES (
    '33333333-3333-3333-3333-333333333336',
    'Córdoba',
    'Av. Colón 1500',
    -31.4135,
    -64.1810
);

-- =========================================================
-- 2. CAMPAÑA
-- =========================================================

MERGE INTO NEWS_CAMPAIGN (
    ID,
    TITLE,
    DESCRIPTION,
    IMAGE_ID,
    SHARE_CAMPAIGN_URL,
    UPDATED_AT,
    CREATED_AT,
    LOCATION_ID,
    OWNER_ID
)
KEY (ID)
VALUES (
    '44444444-4444-4444-4444-444444444446',
    'Colecta de Alimentos para Refugios',
    'Se reciben donaciones de alimento balanceado, mantas y medicamentos para refugios de la zona.',
    NULL,
    NULL,
    NULL,
    TIMESTAMP '2026-06-25 09:00:00',
    '33333333-3333-3333-3333-333333333336',
    '22222222-2222-2222-2222-222222222223'
);

-- =========================================================
-- 3. HISTORIAL DE ESTADOS
-- =========================================================

MERGE INTO NEWS_CAMPAIGN_STATUS_HISTORY (
    ID,
    CREATED_AT,
    FINISHED_AT,
    STATUS,
    NEWS_CAMPAIGN_ID
)
KEY (ID)
VALUES (
    '55555555-5555-5555-5555-555555555557',
    TIMESTAMP '2026-06-25 09:00:00',
    NULL,
    'CREATED',
    '44444444-4444-4444-4444-444444444446'
);