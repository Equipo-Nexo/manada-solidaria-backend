INSERT INTO notification (
    id,
    created_at,
    title,
    message,
    type,
    icon,
    redirect_to
)
VALUES
(
    UUID_TO_BIN('cb3daadc-960e-46a4-aa6b-cb133ec477b7'),
    '2026-09-06 20:52:13.551601',
    'Nueva colecta solidaria',
    '{user} necesita tu ayuda, sumá tu aporte por una causa benéfica.',
    'NEW_FUNDRAISING_CAMPAIGN',
    NULL,
    '/colectas/{fundraisingId}'
);