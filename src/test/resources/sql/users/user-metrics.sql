-- Usuario con un caso feliz propio
MERGE INTO PROFILE (ID, PROFILE_IMAGEURL)
KEY (ID)
VALUES ('960aa28d-f700-47e1-b473-47d5dad6206d', 'cf-perfil-con-caso');

MERGE INTO USERS (ID, USERNAME, PASSWORD, PROFILE_ID)
KEY (ID)
VALUES ('75e89eae-99f1-43e8-900a-c45826ec3153', 'con-caso-feliz', 'x', '960aa28d-f700-47e1-b473-47d5dad6206d');

-- Usuario sin publicaciones
MERGE INTO PROFILE (ID, PROFILE_IMAGEURL)
KEY (ID)
VALUES ('e1ea0db3-928e-444c-90c6-c2284b051279', 'cf-perfil-sin-posts');

MERGE INTO USERS (ID, USERNAME, PASSWORD, PROFILE_ID)
KEY (ID)
VALUES ('00bfedfe-3ed4-4962-9cfa-4d6540b596c2', 'sin-publicaciones', 'x', 'e1ea0db3-928e-444c-90c6-c2284b051279');


INSERT INTO animal (id, color, age, gender, size, type)
VALUES ('eea710e1-232c-4717-bc6e-deeaba851bd0', 'marron', 'ADULT', 'MALE', 'MEDIUM', 'DOG'),
       ('e25a154e-d1dd-4931-9810-25344b9cc751', 'gris', 'PUPPY', 'FEMALE', 'SMALL', 'CAT'),
       ('917544d4-2c9e-4cd8-8a9e-46deac18723f', 'negro', 'ADULT', 'MALE', 'LARGE', 'DOG'),
       ('56bb113b-aff0-40b9-9b49-5ee82740fc5c', 'blanco', 'ADULT', 'FEMALE', 'SMALL', 'CAT');

INSERT INTO location (id, name, address, number, latitude, longitude)
VALUES ('575c2a43-6f58-4b54-821a-95431913081d', 'Plaza', 'Mitre', 1, -32.41, -63.24);

-- Admin: LOST que llego a RESCUED (caso feliz)
INSERT INTO animal_post (id, name, description, image_url, animal_id, location_id, owner_id, created_at)
VALUES ('cd1c651f-9a6b-4660-b156-5a95c0cf7b98', 'Rescatado', 'Lo rescataron en la plaza', NULL,
        'eea710e1-232c-4717-bc6e-deeaba851bd0', '575c2a43-6f58-4b54-821a-95431913081d',
        '22222222-2222-2222-2222-222222222222', DATEADD('DAY', -10, CURRENT_TIMESTAMP));
INSERT INTO lost_post (id, has_owner, reward) VALUES ('cd1c651f-9a6b-4660-b156-5a95c0cf7b98', false, NULL);
INSERT INTO lost_post_status_history (id, lost_post_id, status, created_at, finished_at)
VALUES ('1a575a48-e067-4975-8e28-30dc3038ec16', 'cd1c651f-9a6b-4660-b156-5a95c0cf7b98', 'TO_RESCUE',
        DATEADD('DAY', -10, CURRENT_TIMESTAMP), DATEADD('DAY', -2, CURRENT_TIMESTAMP)),
       ('e7302768-8156-4103-973f-b2c23fbaaac7', 'cd1c651f-9a6b-4660-b156-5a95c0cf7b98', 'RESCUED',
        DATEADD('DAY', -2, CURRENT_TIMESTAMP), NULL);

-- Admin: ADOPTION que llego a ADOPTED (caso feliz)
INSERT INTO animal_post (id, name, description, image_url, animal_id, location_id, owner_id, created_at)
VALUES ('f3cce45a-4e27-4168-9822-dc89b9d20728', 'Adoptada', 'Encontro familia', NULL,
        'e25a154e-d1dd-4931-9810-25344b9cc751', '575c2a43-6f58-4b54-821a-95431913081d',
        '22222222-2222-2222-2222-222222222222', DATEADD('DAY', -20, CURRENT_TIMESTAMP));
INSERT INTO adoption_post (id) VALUES ('f3cce45a-4e27-4168-9822-dc89b9d20728');
INSERT INTO adoption_post_status_history (id, adoption_post_id, status, created_at, finished_at)
VALUES ('37987618-fac8-4cbd-b6ec-8e894892485f', 'f3cce45a-4e27-4168-9822-dc89b9d20728', 'SEARCHING_ADOPT',
        DATEADD('DAY', -20, CURRENT_TIMESTAMP), DATEADD('DAY', -1, CURRENT_TIMESTAMP)),
       ('9dbfddef-2924-4850-b2bd-41543d52c3e6', 'f3cce45a-4e27-4168-9822-dc89b9d20728', 'ADOPTED',
        DATEADD('DAY', -1, CURRENT_TIMESTAMP), NULL);

-- Admin: ADOPTION en SEARCHING_ADOPT (NO es caso feliz)
INSERT INTO animal_post (id, name, description, image_url, animal_id, location_id, owner_id, created_at)
VALUES ('0fd1bc12-fea1-4701-8e61-a0b41238e438', 'Busca hogar', 'Todavia sin familia', NULL,
        '917544d4-2c9e-4cd8-8a9e-46deac18723f', '575c2a43-6f58-4b54-821a-95431913081d',
        '22222222-2222-2222-2222-222222222222', DATEADD('DAY', -5, CURRENT_TIMESTAMP));
INSERT INTO adoption_post (id) VALUES ('0fd1bc12-fea1-4701-8e61-a0b41238e438');
INSERT INTO adoption_post_status_history (id, adoption_post_id, status, created_at, finished_at)
VALUES ('dbcb1d10-f668-4510-b9c0-f531f55e9a24', '0fd1bc12-fea1-4701-8e61-a0b41238e438', 'SEARCHING_ADOPT',
        DATEADD('DAY', -5, CURRENT_TIMESTAMP), NULL);

-- Otro usuario: LOST que llego a FOUND (caso feliz, no es del admin)
INSERT INTO animal_post (id, name, description, image_url, animal_id, location_id, owner_id, created_at)
VALUES ('3158ebbd-78bc-48a9-9771-aee7eb1cc75f', 'Volvio a casa', 'Aparecio solo', NULL,
        '56bb113b-aff0-40b9-9b49-5ee82740fc5c', '575c2a43-6f58-4b54-821a-95431913081d',
        '75e89eae-99f1-43e8-900a-c45826ec3153', DATEADD('DAY', -8, CURRENT_TIMESTAMP));
INSERT INTO lost_post (id, has_owner, reward) VALUES ('3158ebbd-78bc-48a9-9771-aee7eb1cc75f', true, NULL);
INSERT INTO lost_post_status_history (id, lost_post_id, status, created_at, finished_at)
VALUES ('73f753b6-4e3a-4c77-a442-0d2879fea07a', '3158ebbd-78bc-48a9-9771-aee7eb1cc75f', 'SEARCHING',
        DATEADD('DAY', -8, CURRENT_TIMESTAMP), DATEADD('DAY', -3, CURRENT_TIMESTAMP)),
       ('d3b47dff-b40d-4006-816f-b189b516a9e9', '3158ebbd-78bc-48a9-9771-aee7eb1cc75f', 'FOUND',
        DATEADD('DAY', -3, CURRENT_TIMESTAMP), NULL);
