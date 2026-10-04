-- =================================================
-- ADOPTION FORM + ANSWERS (DETAILS)
-- =================================================

INSERT INTO adoption_forms (id, description, area_code, phone_number, is_read, created_at, applicant_id, adoption_post_id)
VALUES
(
    'c1111111-1111-1111-1111-111111111111',
    'Quiero una gatita para que le haga compañía a mi gato de 2 años para que crezcan juntos.',
    '353',
    '4123456',
    false,
    CURRENT_TIMESTAMP,
    'a1111111-1111-1111-1111-111111111111',
    '99999999-9999-9999-9999-999999999999'
);

INSERT INTO adoption_form_details (id, answer, adoption_form_id, question_form_id)
VALUES
(
    'd1111111-1111-1111-1111-111111111111',
    'Alquilo y sí me permiten.',
    'c1111111-1111-1111-1111-111111111111',
    'f1111111-1111-1111-1111-111111111111'
),
(
    'd2222222-2222-2222-2222-222222222222',
    'Sí, totalmente cerrado.',
    'c1111111-1111-1111-1111-111111111111',
    'f2222222-2222-2222-2222-222222222222'
);