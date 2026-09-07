INSERT INTO profile_roles (profile_id, role)
SELECT
    p.id,
    jt.role
FROM profile p
JOIN JSON_TABLE(
    p.roles,
    '$[*]' COLUMNS (
        role VARCHAR(255) PATH '$'
    )
) jt
WHERE p.roles IS NOT NULL;
