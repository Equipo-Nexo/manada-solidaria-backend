UPDATE animal_post
SET updated_at = created_at
WHERE updated_at IS NULL;

-- ROLLBACK
-- No es reversible: despues del backfill no se distinguen las filas que tenian updated_at en NULL
-- de las publicaciones que nunca se editaron. No se pierde ningun dato.
