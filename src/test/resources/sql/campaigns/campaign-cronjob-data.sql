-- =========================================================
-- LOCATIONS
-- =========================================================

MERGE INTO LOCATION (
    ID,
    NAME,
    ADDRESS,
    LATITUDE,
    LONGITUDE
)
KEY(ID)
VALUES (
    '30000000-0000-0000-0000-000000000001',
    'Villa María',
    'Av. Sabattini 100',
    -32.40,
    -63.24
);

MERGE INTO LOCATION (
    ID,
    NAME,
    ADDRESS,
    LATITUDE,
    LONGITUDE
)
KEY(ID)
VALUES (
    '30000000-0000-0000-0000-000000000002',
    'Villa Nueva',
    'Av. Carranza 200',
    -32.41,
    -63.25
);

MERGE INTO LOCATION (
    ID,
    NAME,
    ADDRESS,
    LATITUDE,
    LONGITUDE
)
KEY(ID)
VALUES (
    '30000000-0000-0000-0000-000000000003',
    'Córdoba',
    'Colón 100',
    -31.41,
    -64.18
);


-- =========================================================
-- CAMPAÑAS VENCIDAS
-- =========================================================

-- =========================================================
-- Donation vencida
-- Estado inicial: CREATED
-- =========================================================

MERGE INTO DONATION_CAMPAIGN (
    ID,
    TITLE,
    DESCRIPTION,
    IMAGE_ID,
    AREA_CODE,
    PHONE_NUMBER,
    CREATED_AT,
    LOCATION_ID,
    OWNER_ID,
    CAMPAIGN_END_DATE
)
KEY(ID)
VALUES (
    '40000000-0000-0000-0000-000000000011',
    'Donation Expirada',
    'Donation para test de cronjob',
    NULL,
    '3534',
    '000000',
    CURRENT_TIMESTAMP,
    '30000000-0000-0000-0000-000000000001',
    '22222222-2222-2222-2222-222222222222',
    DATEADD('DAY', -1, CURRENT_DATE)
);

MERGE INTO DONATION_CAMPAIGN_STATUS_HISTORY (
    ID,
    CREATED_AT,
    STATUS,
    DONATION_CAMPAIGN_ID
)
KEY(ID)
VALUES (
    '50000000-0000-0000-0000-000000000011',
    CURRENT_TIMESTAMP,
    'CREATED',
    '40000000-0000-0000-0000-000000000011'
);

MERGE INTO DONATION_ITEM (
    ID,
    NAME,
    CATEGORY,
    IS_COMPLETED,
    DONATION_CAMPAIGN_ID
)
KEY(ID)
VALUES (
    '60000000-0000-0000-0000-000000000011',
    'Balanceado',
    'FOOD',
    FALSE,
    '40000000-0000-0000-0000-000000000011'
);


-- =========================================================
-- Fundraising vencida
-- Estado inicial: CREATED
-- =========================================================

MERGE INTO FUNDRAISING_CAMPAIGN (
    ID,
    TITLE,
    DESCRIPTION,
    AREA_CODE,
    PHONE_NUMBER,
    CREATED_AT,
    LOCATION_ID,
    OWNER_ID,
    ACCOUNT_ALIAS,
    AMOUNT_TO_BE_COLLECTED,
    AMOUNT_COLLECTED,
    CAMPAIGN_END_DATE
)
KEY(ID)
VALUES (
    '40000000-0000-0000-0000-000000000012',
    'Fundraising Expirada',
    'Fundraising para test de cronjob',
    '3534',
    '000000',
    CURRENT_TIMESTAMP,
    '30000000-0000-0000-0000-000000000003',
    '22222222-2222-2222-2222-222222222222',
    'alias.cronjob',
    100000,
    5000,
    DATEADD('DAY', -1, CURRENT_DATE)
);

MERGE INTO FUNDRAISING_CAMPAIGN_STATUS_HISTORY (
    ID,
    CREATED_AT,
    STATUS,
    FUNDRAISING_CAMPAIGN_ID
)
KEY(ID)
VALUES (
    '50000000-0000-0000-0000-000000000012',
    CURRENT_TIMESTAMP,
    'CREATED',
    '40000000-0000-0000-0000-000000000012'
);


-- =========================================================
-- News vencida
-- Estado inicial: STARTED
-- =========================================================

MERGE INTO NEWS_CAMPAIGN (
    ID,
    TITLE,
    DESCRIPTION,
    AREA_CODE,
    PHONE_NUMBER,
    CREATED_AT,
    LOCATION_ID,
    OWNER_ID,
    NEWS_START_DATE_TIME,
    NEWS_END_DATE_TIME,
    CATEGORY
)
KEY(ID)
VALUES (
    '40000000-0000-0000-0000-000000000013',
    'News Expirada',
    'News para test de cronjob',
    '3534',
    '000000',
    CURRENT_TIMESTAMP,
    '30000000-0000-0000-0000-000000000002',
    '22222222-2222-2222-2222-222222222222',
    DATEADD('DAY', -2, CURRENT_TIMESTAMP),
    DATEADD('DAY', -1, CURRENT_TIMESTAMP),
    'VACCINATION'
);

MERGE INTO NEWS_CAMPAIGN_STATUS_HISTORY (
    ID,
    CREATED_AT,
    STATUS,
    NEWS_CAMPAIGN_ID
)
KEY(ID)
VALUES (
    '50000000-0000-0000-0000-000000000013',
    CURRENT_TIMESTAMP,
    'STARTED',
    '40000000-0000-0000-0000-000000000013'
);


-- =========================================================
-- Fundraising vencida y COMPLETED
-- Debe pasar de COMPLETED -> FINISHED
-- =========================================================

MERGE INTO FUNDRAISING_CAMPAIGN (
    ID,
    TITLE,
    DESCRIPTION,
    AREA_CODE,
    PHONE_NUMBER,
    CREATED_AT,
    LOCATION_ID,
    OWNER_ID,
    ACCOUNT_ALIAS,
    AMOUNT_TO_BE_COLLECTED,
    AMOUNT_COLLECTED,
    CAMPAIGN_END_DATE
)
KEY(ID)
VALUES (
    '40000000-0000-0000-0000-000000000014',
    'Fundraising Completed Expirada',
    'Fundraising completed para test de cronjob',
    '3534',
    '000000',
    CURRENT_TIMESTAMP,
    '30000000-0000-0000-0000-000000000003',
    '22222222-2222-2222-2222-222222222222',
    'alias.cronjob.completed',
    100000,
    100000,
    DATEADD('DAY', -1, CURRENT_DATE)
);

MERGE INTO FUNDRAISING_CAMPAIGN_STATUS_HISTORY (
    ID,
    CREATED_AT,
    STATUS,
    FUNDRAISING_CAMPAIGN_ID
)
KEY(ID)
VALUES (
    '50000000-0000-0000-0000-000000000014',
    CURRENT_TIMESTAMP,
    'COMPLETED',
    '40000000-0000-0000-0000-000000000014'
);


-- =========================================================
-- CAMPAÑAS NO VENCIDAS
-- =========================================================

-- =========================================================
-- Donation no vencida
-- Estado: CREATED
-- =========================================================

MERGE INTO DONATION_CAMPAIGN (
    ID,
    TITLE,
    DESCRIPTION,
    IMAGE_ID,
    AREA_CODE,
    PHONE_NUMBER,
    CREATED_AT,
    LOCATION_ID,
    OWNER_ID,
    CAMPAIGN_END_DATE
)
KEY(ID)
VALUES (
    '40000000-0000-0000-0000-000000000001',
    'Título Donación Test',
    'Descripción Donación',
    NULL,
    '3534',
    '000000',
    CURRENT_TIMESTAMP,
    '30000000-0000-0000-0000-000000000001',
    '22222222-2222-2222-2222-222222222222',
    DATE '2027-01-01'
);

MERGE INTO DONATION_CAMPAIGN_STATUS_HISTORY (
    ID,
    CREATED_AT,
    STATUS,
    DONATION_CAMPAIGN_ID
)
KEY(ID)
VALUES (
    '50000000-0000-0000-0000-000000000001',
    CURRENT_TIMESTAMP,
    'CREATED',
    '40000000-0000-0000-0000-000000000001'
);

MERGE INTO DONATION_ITEM (
    ID,
    NAME,
    CATEGORY,
    IS_COMPLETED,
    DONATION_CAMPAIGN_ID
)
KEY(ID)
VALUES (
    '60000000-0000-0000-0000-000000000001',
    'Balanceado',
    'FOOD',
    FALSE,
    '40000000-0000-0000-0000-000000000001'
);


-- =========================================================
-- News no vencida
-- Estado: CREATED
-- La campaña todavía no comenzó
-- =========================================================

MERGE INTO NEWS_CAMPAIGN (
    ID,
    TITLE,
    DESCRIPTION,
    AREA_CODE,
    PHONE_NUMBER,
    CREATED_AT,
    LOCATION_ID,
    OWNER_ID,
    NEWS_START_DATE_TIME,
    NEWS_END_DATE_TIME,
    CATEGORY
)
KEY(ID)
VALUES (
    '40000000-0000-0000-0000-000000000002',
    'Título Noticia Test',
    'Descripción News',
    '3534',
    '000000',
    CURRENT_TIMESTAMP,
    '30000000-0000-0000-0000-000000000002',
    '22222222-2222-2222-2222-222222222222',
    DATEADD('DAY', 1, CURRENT_TIMESTAMP),
    DATEADD('DAY', 2, CURRENT_TIMESTAMP),
    'VACCINATION'
);

MERGE INTO NEWS_CAMPAIGN_STATUS_HISTORY (
    ID,
    CREATED_AT,
    STATUS,
    NEWS_CAMPAIGN_ID
)
KEY(ID)
VALUES (
    '50000000-0000-0000-0000-000000000002',
    CURRENT_TIMESTAMP,
    'CREATED',
    '40000000-0000-0000-0000-000000000002'
);


-- =========================================================
-- Fundraising no vencida
-- Estado: CREATED
-- =========================================================

MERGE INTO FUNDRAISING_CAMPAIGN (
    ID,
    TITLE,
    DESCRIPTION,
    AREA_CODE,
    PHONE_NUMBER,
    CREATED_AT,
    LOCATION_ID,
    OWNER_ID,
    ACCOUNT_ALIAS,
    AMOUNT_TO_BE_COLLECTED,
    AMOUNT_COLLECTED,
    CAMPAIGN_END_DATE
)
KEY(ID)
VALUES (
    '40000000-0000-0000-0000-000000000003',
    'Título Fundraising Test',
    'Descripción Fundraising',
    '3534',
    '000000',
    CURRENT_TIMESTAMP,
    '30000000-0000-0000-0000-000000000003',
    '22222222-2222-2222-2222-222222222222',
    'alias123',
    100000,
    5000,
    DATE '2027-01-01'
);

MERGE INTO FUNDRAISING_CAMPAIGN_STATUS_HISTORY (
    ID,
    CREATED_AT,
    STATUS,
    FUNDRAISING_CAMPAIGN_ID
)
KEY(ID)
VALUES (
    '50000000-0000-0000-0000-000000000003',
    CURRENT_TIMESTAMP,
    'CREATED',
    '40000000-0000-0000-0000-000000000003'
);