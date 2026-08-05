-- =========================================================
-- 1. UBICACIÓN - CAMPAÑA DE RECAUDACIÓN
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
-- 2. CAMPAÑA DE RECAUDACIÓN
-- =========================================================

MERGE INTO FUNDRAISING_CAMPAIGN (
    ID,
    TITLE,
    DESCRIPTION,
    IMAGE_ID,
    SHARE_CAMPAIGN_URL,
    PHONE_NUMBER,
    UPDATED_AT,
    CREATED_AT,
    LOCATION_ID,
    OWNER_ID,
    ACCOUNT_ALIAS,
    AMOUNT_TO_BE_COLLECTED,
    AMOUNT_COLLECTED,
    CAMPAIGN_END_DATE
)
KEY (ID)
VALUES (
    '44444444-4444-4444-4444-444444444444',
    'Recaudación para tratamiento veterinario',
    'Recaudación destinada a cubrir los gastos del tratamiento veterinario.',
    NULL,
    NULL,
    '+5493534000000',
    NULL,
    TIMESTAMP '2026-08-02 19:55:00',
    '33333333-3333-3333-3333-333333333333',
    '22222222-2222-2222-2222-222222222222',
    'MANADA.SOLIDARIA',
    500000,
    0,
    DATE '2026-09-30'
);

-- =========================================================
-- 3. HISTORIAL DE ESTADOS - CAMPAÑA DE RECAUDACIÓN
-- =========================================================

MERGE INTO FUNDRAISING_CAMPAIGN_STATUS_HISTORY (
    ID,
    STATUS,
    FINISHED_AT,
    CREATED_AT,
    FUNDRAISING_CAMPAIGN_ID
)
KEY (ID)
VALUES (
    '55555555-5555-5555-5555-555555555555',
    'CREATED',
    NULL,
    TIMESTAMP '2026-08-02 19:55:00',
    '44444444-4444-4444-4444-444444444444'
);