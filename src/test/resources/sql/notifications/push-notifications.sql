MERGE INTO PUSH_SUBSCRIPTION (
    ID,
    USER_ID,
    ENDPOINT,
    ENDPOINT_HASH,
    P256DH,
    AUTH,
    CREATED_AT,
    UPDATED_AT
)
KEY (ID)
VALUES (
    '33333333-3333-3333-3333-333333333333',
    '22222222-2222-2222-2222-222222222222',
    'https://example.com/push-service/send',
    'hased_code',
    'test-p256dh-key',
    'test-auth-key',
    '2025-03-14 10:00:00',
    NULL
);