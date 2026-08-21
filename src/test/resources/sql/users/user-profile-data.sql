MERGE INTO PROFILE (
    ID,
    NAME,
    LASTNAME,
    EMAIL,
    AREA_CODE,
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
    '3533',
    '436249',
    'cf-profile-1',
    'COMMUNITY'
);
