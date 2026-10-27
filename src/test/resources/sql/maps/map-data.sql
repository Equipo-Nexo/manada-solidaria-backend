-- Reloj congelado en el test: miercoles 2026-09-23 10:00 (America/Argentina/Buenos_Aires)

INSERT INTO animal (id, color, age, gender, size, type)
VALUES ('ee000000-0000-0000-0000-0000000000a1', 'negro', 'ADULT', 'MALE', 'MEDIUM', 'DOG'),
       ('ee000000-0000-0000-0000-0000000000a2', 'negro', 'ADULT', 'MALE', 'MEDIUM', 'DOG'),
       ('ee000000-0000-0000-0000-0000000000a3', 'negro', 'ADULT', 'MALE', 'MEDIUM', 'DOG'),
       ('ee000000-0000-0000-0000-0000000000a4', 'negro', 'ADULT', 'MALE', 'MEDIUM', 'DOG'),
       ('ee000000-0000-0000-0000-0000000000a5', 'negro', 'ADULT', 'MALE', 'MEDIUM', 'DOG'),
       ('ee000000-0000-0000-0000-0000000000a6', 'negro', 'ADULT', 'MALE', 'MEDIUM', 'DOG');

-- Ubicaciones: completa, solo nombre, sin datos
INSERT INTO location (id, name, address, number, latitude, longitude)
VALUES ('ee000000-0000-0000-0000-0000000000b1', 'San Justo', 'Constancio Vigil', 1821, -32.4102, -63.2411),
       ('ee000000-0000-0000-0000-0000000000b2', 'Centro', NULL, NULL, -32.4152, -63.2451),
       ('ee000000-0000-0000-0000-0000000000b3', NULL, NULL, NULL, -32.4180, -63.2510);

-- LOST activo, publicado hace 2 dias
INSERT INTO animal_post (id, name, description, image_url, animal_id, location_id, owner_id, created_at)
VALUES ('ee000000-0000-0000-0000-0000000000c1', 'Milo', 'Se perdio', '/fotoMilo',
        'ee000000-0000-0000-0000-0000000000a1', 'ee000000-0000-0000-0000-0000000000b1',
        '22222222-2222-2222-2222-222222222222', TIMESTAMP '2026-09-21 09:00:00');
INSERT INTO lost_post (id, has_owner, reward) VALUES ('ee000000-0000-0000-0000-0000000000c1', true, NULL);
INSERT INTO lost_post_status_history (id, lost_post_id, status, created_at, finished_at)
VALUES ('ee000000-0000-0000-0000-0000000000d1', 'ee000000-0000-0000-0000-0000000000c1', 'SEARCHING',
        TIMESTAMP '2026-09-21 09:00:00', NULL);

-- LOST ya encontrado: NO va al mapa
INSERT INTO animal_post (id, name, description, image_url, animal_id, location_id, owner_id, created_at)
VALUES ('ee000000-0000-0000-0000-0000000000c2', 'Rex', 'Aparecio', '/fotoRex',
        'ee000000-0000-0000-0000-0000000000a2', 'ee000000-0000-0000-0000-0000000000b1',
        '22222222-2222-2222-2222-222222222222', TIMESTAMP '2026-09-10 09:00:00');
INSERT INTO lost_post (id, has_owner, reward) VALUES ('ee000000-0000-0000-0000-0000000000c2', true, NULL);
INSERT INTO lost_post_status_history (id, lost_post_id, status, created_at, finished_at)
VALUES ('ee000000-0000-0000-0000-0000000000d2', 'ee000000-0000-0000-0000-0000000000c2', 'SEARCHING',
        TIMESTAMP '2026-09-10 09:00:00', TIMESTAMP '2026-09-15 09:00:00'),
       ('ee000000-0000-0000-0000-0000000000d3', 'ee000000-0000-0000-0000-0000000000c2', 'FOUND',
        TIMESTAMP '2026-09-15 09:00:00', NULL);

-- IN_STREET activo, publicado ayer
INSERT INTO animal_post (id, name, description, image_url, animal_id, location_id, owner_id, created_at)
VALUES ('ee000000-0000-0000-0000-0000000000c3', 'Luna', 'Anda por el centro', '/fotoLuna',
        'ee000000-0000-0000-0000-0000000000a3', 'ee000000-0000-0000-0000-0000000000b2',
        '22222222-2222-2222-2222-222222222222', TIMESTAMP '2026-09-22 18:00:00');
INSERT INTO lost_post (id, has_owner, reward) VALUES ('ee000000-0000-0000-0000-0000000000c3', false, NULL);
INSERT INTO lost_post_status_history (id, lost_post_id, status, created_at, finished_at)
VALUES ('ee000000-0000-0000-0000-0000000000d4', 'ee000000-0000-0000-0000-0000000000c3', 'TO_RESCUE',
        TIMESTAMP '2026-09-22 18:00:00', NULL);

-- IN_STREET activo, publicado hoy, sin datos de ubicacion
INSERT INTO animal_post (id, name, description, image_url, animal_id, location_id, owner_id, created_at)
VALUES ('ee000000-0000-0000-0000-0000000000c4', 'Toby', 'Recien visto', '/fotoToby',
        'ee000000-0000-0000-0000-0000000000a4', 'ee000000-0000-0000-0000-0000000000b3',
        '22222222-2222-2222-2222-222222222222', TIMESTAMP '2026-09-23 08:00:00');
INSERT INTO lost_post (id, has_owner, reward) VALUES ('ee000000-0000-0000-0000-0000000000c4', false, NULL);
INSERT INTO lost_post_status_history (id, lost_post_id, status, created_at, finished_at)
VALUES ('ee000000-0000-0000-0000-0000000000d5', 'ee000000-0000-0000-0000-0000000000c4', 'TO_RESCUE',
        TIMESTAMP '2026-09-23 08:00:00', NULL);

-- IN_STREET ya rescatado: NO va al mapa
INSERT INTO animal_post (id, name, description, image_url, animal_id, location_id, owner_id, created_at)
VALUES ('ee000000-0000-0000-0000-0000000000c5', 'Coco', 'Ya rescatado', '/fotoCoco',
        'ee000000-0000-0000-0000-0000000000a5', 'ee000000-0000-0000-0000-0000000000b2',
        '22222222-2222-2222-2222-222222222222', TIMESTAMP '2026-09-01 09:00:00');
INSERT INTO lost_post (id, has_owner, reward) VALUES ('ee000000-0000-0000-0000-0000000000c5', false, NULL);
INSERT INTO lost_post_status_history (id, lost_post_id, status, created_at, finished_at)
VALUES ('ee000000-0000-0000-0000-0000000000d6', 'ee000000-0000-0000-0000-0000000000c5', 'RESCUED',
        TIMESTAMP '2026-09-05 09:00:00', NULL);

-- ADOPTION: NO va al mapa
INSERT INTO animal_post (id, name, description, image_url, animal_id, location_id, owner_id, created_at)
VALUES ('ee000000-0000-0000-0000-0000000000c6', 'Michi', 'Busca hogar', '/fotoMichi',
        'ee000000-0000-0000-0000-0000000000a6', 'ee000000-0000-0000-0000-0000000000b1',
        '22222222-2222-2222-2222-222222222222', TIMESTAMP '2026-09-22 09:00:00');
INSERT INTO adoption_post (id) VALUES ('ee000000-0000-0000-0000-0000000000c6');
INSERT INTO adoption_post_status_history (id, adoption_post_id, status, created_at, finished_at)
VALUES ('ee000000-0000-0000-0000-0000000000d7', 'ee000000-0000-0000-0000-0000000000c6', 'SEARCHING_ADOPT',
        TIMESTAMP '2026-09-22 09:00:00', NULL);

-- Veterinarias: abierta (dos turnos el miercoles), cerrada a las 10, sin horario
INSERT INTO location (id, name, address, number, latitude, longitude)
VALUES ('ee000000-0000-0000-0000-0000000000b4', 'Villa Maria', 'San Martin', 540, -32.4180, -63.2510),
       ('ee000000-0000-0000-0000-0000000000b5', 'Centro', 'Mitre', 100, -32.4100, -63.2400),
       ('ee000000-0000-0000-0000-0000000000b6', 'Norte', 'Salta', 200, -32.4000, -63.2300);

INSERT INTO vet_information (id, location_id, description, email, name, area_code, phone_number, profile_picture_url, vet_page_url)
VALUES ('ee000000-0000-0000-0000-0000000000e1', 'ee000000-0000-0000-0000-0000000000b4', 'Clinica', 'seba@mail.com',
        'Dr. Seba Veterinaria', '353', '4010369', '/fotoVeterinaria', NULL),
       ('ee000000-0000-0000-0000-0000000000e2', 'ee000000-0000-0000-0000-0000000000b5', 'Clinica', 'tarde@mail.com',
        'Abre a la tarde', '353', '4111111', '/fotoTarde', NULL),
       ('ee000000-0000-0000-0000-0000000000e3', 'ee000000-0000-0000-0000-0000000000b6', 'Clinica', 'nunca@mail.com',
        'Sin horario', '353', '4222222', '/fotoSinHorario', NULL);

INSERT INTO schedule (id, vet_id, day_of_week, opening_time, closing_time)
VALUES ('ee000000-0000-0000-0000-0000000000f1', 'ee000000-0000-0000-0000-0000000000e1', 'WEDNESDAY', '08:00:00', '13:00:00'),
       ('ee000000-0000-0000-0000-0000000000f2', 'ee000000-0000-0000-0000-0000000000e1', 'WEDNESDAY', '16:00:00', '20:00:00'),
       ('ee000000-0000-0000-0000-0000000000f3', 'ee000000-0000-0000-0000-0000000000e2', 'WEDNESDAY', '16:00:00', '20:00:00'),
       ('ee000000-0000-0000-0000-0000000000f4', 'ee000000-0000-0000-0000-0000000000e2', 'TUESDAY', '08:00:00', '20:00:00');
