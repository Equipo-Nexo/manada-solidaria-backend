-- =========================================================
-- 1. UBICACIÓN - CAMPAÑA 1
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
    '33333333-3333-3333-3333-333333333333',
    'Villa María',
    'Av. Sabattini 100',
    -32.4075,
    -63.2402
);

-- =========================================================
-- 2. CAMPAÑA 1
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
    '44444444-4444-4444-4444-444444444444',
    'Campaña de Vacunación',
    'Vacunación gratuita para perros y gatos.',
    NULL,
    NULL,
    NULL,
    TIMESTAMP '2026-06-23 21:00:52.362888',
    '33333333-3333-3333-3333-333333333333',
    '22222222-2222-2222-2222-222222222222'
);

-- =========================================================
-- 3. HISTORIAL CAMPAÑA 1
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
    '55555555-5555-5555-5555-555555555555',
    TIMESTAMP '2026-06-23 21:00:52.362888',
    NULL,
    'CREATED',
    '44444444-4444-4444-4444-444444444444'
);

-- =========================================================
-- 4. UBICACIÓN - CAMPAÑA 2
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
    '33333333-3333-3333-3333-333333333334',
    'Villa Nueva',
    'Av. Carranza 450',
    -32.4321,
    -63.2478
);

-- =========================================================
-- 5. CAMPAÑA 2
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
    '44444444-4444-4444-4444-444444444445',
    'Jornada de Adopción Responsable',
    'Acercate a conocer perros y gatos que buscan una familia. Habrá asesoramiento sobre adopción responsable.',
    NULL,
    NULL,
    NULL,
    TIMESTAMP '2026-06-24 10:00:00',
    '33333333-3333-3333-3333-333333333334',
    '22222222-2222-2222-2222-222222222222'
);

-- =========================================================
-- 6. HISTORIAL CAMPAÑA 2
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
    '55555555-5555-5555-5555-555555555556',
    TIMESTAMP '2026-06-24 10:00:00',
    NULL,
    'CREATED',
    '44444444-4444-4444-4444-444444444445'
);