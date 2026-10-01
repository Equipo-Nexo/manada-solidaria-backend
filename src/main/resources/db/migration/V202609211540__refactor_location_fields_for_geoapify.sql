-- 1. Renombrar las columnas existentes para conservar la información
ALTER TABLE location RENAME COLUMN address TO formatted;
ALTER TABLE location RENAME COLUMN number TO house_number;

-- 2. Eliminar la columna name que ya no se utiliza
ALTER TABLE location DROP COLUMN name;

-- 3. Agregar las nuevas columnas para la integración con Geoapify
-- NOTA: country y city se agregan permitiendo NULL inicialmente por si existen filas previas en la DB.
ALTER TABLE location ADD COLUMN country VARCHAR(255);
ALTER TABLE location ADD COLUMN city VARCHAR(255);
ALTER TABLE location ADD COLUMN district VARCHAR(255);
ALTER TABLE location ADD COLUMN street VARCHAR(255);

-- ROLLBACK
-- ALTER TABLE location DROP COLUMN street;
-- ALTER TABLE location DROP COLUMN district;
-- ALTER TABLE location DROP COLUMN city;
-- ALTER TABLE location DROP COLUMN country;
-- ALTER TABLE location ADD COLUMN name VARCHAR(255);
-- ALTER TABLE location RENAME COLUMN house_number TO number;
-- ALTER TABLE location RENAME COLUMN formatted TO address;