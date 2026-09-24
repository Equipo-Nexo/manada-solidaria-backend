-- 1. Eliminar columnas deprecadas
ALTER TABLE location DROP COLUMN name;
ALTER TABLE location DROP COLUMN address;

-- 2. Renombrar columna
ALTER TABLE location RENAME COLUMN number TO house_number;

-- 3. Agregar las nuevas columnas con NOT NULL directo
ALTER TABLE location ADD COLUMN country VARCHAR(255) NOT NULL;
ALTER TABLE location ADD COLUMN city VARCHAR(255) NOT NULL;
ALTER TABLE location ADD COLUMN formatted VARCHAR(500) NOT NULL;

ALTER TABLE location ADD COLUMN district VARCHAR(255);
ALTER TABLE location ADD COLUMN street VARCHAR(255);

-- ROLLBACK
-- ALTER TABLE location ADD COLUMN address VARCHAR(255);
-- ALTER TABLE location ADD COLUMN name VARCHAR(255);
-- ALTER TABLE location RENAME COLUMN house_number TO number;
-- ALTER TABLE location DROP COLUMN street;
-- ALTER TABLE location DROP COLUMN district;
-- ALTER TABLE location DROP COLUMN formatted;
-- ALTER TABLE location DROP COLUMN city;
-- ALTER TABLE location DROP COLUMN country;