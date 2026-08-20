MERGE INTO PROFILE (
    ID,
    NAME,
    LASTNAME,
    EMAIL,
    PHONE_NUMBER,
    PROFILE_IMAGEURL,
    ROLES
)
KEY (ID)
VALUES (
    '11111111-1111-1111-1111-111111111111',
    'Elian',
    'Enria',
    'admin@mail.com',
    '1133334444',
    'cf-profile-1',
    'COMMUNITY'
);
