-- Dueño con rol RESCUER
MERGE INTO PROFILE (ID, ROLES, PROFILE_IMAGEURL) KEY (ID)
VALUES ('aa000000-0000-0000-0000-000000000001', 'RESCUER', 'cf-perfil-rescatista');

MERGE INTO USERS (ID, USERNAME, PASSWORD, PROFILE_ID) KEY (ID)
VALUES ('aa000000-0000-0000-0000-000000000011', 'rescatista', 'x',
        'aa000000-0000-0000-0000-000000000001');

-- Dueño sin RESCUER
MERGE INTO PROFILE (ID, ROLES, PROFILE_IMAGEURL) KEY (ID)
VALUES ('aa000000-0000-0000-0000-000000000002', 'COMMUNITY', 'cf-perfil-vecino');

MERGE INTO USERS (ID, USERNAME, PASSWORD, PROFILE_ID) KEY (ID)
VALUES ('aa000000-0000-0000-0000-000000000022', 'vecino', 'x',
        'aa000000-0000-0000-0000-000000000002');

INSERT INTO animal (id, color, age, gender, size, type)
VALUES ('aa000000-0000-0000-0000-0000000000a1', 'marron', 'ADULT', 'MALE', 'MEDIUM', 'DOG'),
       ('aa000000-0000-0000-0000-0000000000a2', 'gris', 'PUPPY', 'FEMALE', 'SMALL', 'CAT'),
       ('aa000000-0000-0000-0000-0000000000a3', 'negro', 'ADULT', 'MALE', 'LARGE', 'DOG'),
       ('aa000000-0000-0000-0000-0000000000a4', 'blanco', 'ADULT', 'FEMALE', 'SMALL', 'CAT');

INSERT INTO location (id, name, address, number, latitude, longitude)
VALUES ('aa000000-0000-0000-0000-0000000000b1', 'Plaza', 'Mitre', 1, -34.6, -58.4);

-- 1) LOST que llegó a FOUND: caso feliz MÁS VIEJO (transicionó hace 5 días)
INSERT INTO animal_post (id, name, description, image_url, animal_id, location_id, owner_id, created_at)
VALUES ('aa000000-0000-0000-0000-0000000000c1', 'Firulais volvio', 'Aparecio en la plaza', 'cf-img-found',
        'aa000000-0000-0000-0000-0000000000a1', 'aa000000-0000-0000-0000-0000000000b1',
        'aa000000-0000-0000-0000-000000000011', DATEADD('DAY', -10, CURRENT_TIMESTAMP));
INSERT INTO lost_post (id, has_owner, reward) VALUES ('aa000000-0000-0000-0000-0000000000c1', true, NULL);
INSERT INTO lost_post_status_history (id, lost_post_id, status, created_at, finished_at)
VALUES ('aa000000-0000-0000-0000-0000000000d1', 'aa000000-0000-0000-0000-0000000000c1', 'SEARCHING',
        DATEADD('DAY', -10, CURRENT_TIMESTAMP), DATEADD('DAY', -5, CURRENT_TIMESTAMP)),
       ('aa000000-0000-0000-0000-0000000000d2', 'aa000000-0000-0000-0000-0000000000c1', 'FOUND',
        DATEADD('DAY', -5, CURRENT_TIMESTAMP), NULL);

-- 2) ADOPTION que llegó a ADOPTED: caso feliz MÁS NUEVO (transicionó ayer)
INSERT INTO animal_post (id, name, description, image_url, animal_id, location_id, owner_id, created_at)
VALUES ('aa000000-0000-0000-0000-0000000000c2', 'Michi adoptada', 'Encontro familia', 'cf-img-adopted',
        'aa000000-0000-0000-0000-0000000000a2', 'aa000000-0000-0000-0000-0000000000b1',
        'aa000000-0000-0000-0000-000000000022', DATEADD('DAY', -20, CURRENT_TIMESTAMP));
INSERT INTO adoption_post (id) VALUES ('aa000000-0000-0000-0000-0000000000c2');
INSERT INTO adoption_post_status_history (id, adoption_post_id, status, created_at, finished_at)
VALUES ('aa000000-0000-0000-0000-0000000000d3', 'aa000000-0000-0000-0000-0000000000c2', 'SEARCHING_ADOPT',
        DATEADD('DAY', -20, CURRENT_TIMESTAMP), DATEADD('DAY', -1, CURRENT_TIMESTAMP)),
       ('aa000000-0000-0000-0000-0000000000d4', 'aa000000-0000-0000-0000-0000000000c2', 'ADOPTED',
        DATEADD('DAY', -1, CURRENT_TIMESTAMP), NULL);

-- 3) LOST en SEARCHING: NO es caso feliz
INSERT INTO animal_post (id, name, description, image_url, animal_id, location_id, owner_id, created_at)
VALUES ('aa000000-0000-0000-0000-0000000000c3', 'Sigue perdido', 'Lo estamos buscando', 'cf-img-searching',
        'aa000000-0000-0000-0000-0000000000a3', 'aa000000-0000-0000-0000-0000000000b1',
        'aa000000-0000-0000-0000-000000000011', CURRENT_TIMESTAMP);
INSERT INTO lost_post (id, has_owner, reward) VALUES ('aa000000-0000-0000-0000-0000000000c3', true, NULL);
INSERT INTO lost_post_status_history (id, lost_post_id, status, created_at, finished_at)
VALUES ('aa000000-0000-0000-0000-0000000000d5', 'aa000000-0000-0000-0000-0000000000c3', 'SEARCHING',
        CURRENT_TIMESTAMP, NULL);

-- 4) ADOPTION en SEARCHING_ADOPT: NO es caso feliz
INSERT INTO animal_post (id, name, description, image_url, animal_id, location_id, owner_id, created_at)
VALUES ('aa000000-0000-0000-0000-0000000000c4', 'Busca hogar', 'Todavia sin familia', 'cf-img-searching-adopt',
        'aa000000-0000-0000-0000-0000000000a4', 'aa000000-0000-0000-0000-0000000000b1',
        'aa000000-0000-0000-0000-000000000022', CURRENT_TIMESTAMP);
INSERT INTO adoption_post (id) VALUES ('aa000000-0000-0000-0000-0000000000c4');
INSERT INTO adoption_post_status_history (id, adoption_post_id, status, created_at, finished_at)
VALUES ('aa000000-0000-0000-0000-0000000000d6', 'aa000000-0000-0000-0000-0000000000c4', 'SEARCHING_ADOPT',
        CURRENT_TIMESTAMP, NULL);

-- 5) LOST que llego a FOUND hace EXACTAMENTE 7 dias: caso feliz pero YA NO reciente (borde)
INSERT INTO animal (id, color, age, gender, size, type)
VALUES ('aa000000-0000-0000-0000-0000000000a5', 'atigrado', 'ADULT', 'MALE', 'MEDIUM', 'DOG');
INSERT INTO animal_post (id, name, description, image_url, animal_id, location_id, owner_id, created_at)
VALUES ('aa000000-0000-0000-0000-0000000000c5', 'Rex historico', 'Aparecio hace una semana', 'cf-img-old',
        'aa000000-0000-0000-0000-0000000000a5', 'aa000000-0000-0000-0000-0000000000b1',
        'aa000000-0000-0000-0000-000000000011', DATEADD('DAY', -30, CURRENT_TIMESTAMP));
INSERT INTO lost_post (id, has_owner, reward) VALUES ('aa000000-0000-0000-0000-0000000000c5', true, NULL);
INSERT INTO lost_post_status_history (id, lost_post_id, status, created_at, finished_at)
VALUES ('aa000000-0000-0000-0000-0000000000d7', 'aa000000-0000-0000-0000-0000000000c5', 'SEARCHING',
        DATEADD('DAY', -30, CURRENT_TIMESTAMP), DATEADD('DAY', -7, CURRENT_TIMESTAMP)),
       ('aa000000-0000-0000-0000-0000000000d8', 'aa000000-0000-0000-0000-0000000000c5', 'FOUND',
        DATEADD('DAY', -7, CURRENT_TIMESTAMP), NULL);
