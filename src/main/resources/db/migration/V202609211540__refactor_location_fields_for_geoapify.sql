-- 1. Renombrar las columnas existentes para conservar la información
ALTER TABLE location RENAME COLUMN address TO street;
ALTER TABLE location RENAME COLUMN number TO house_number;

-- 2. Agregar las nuevas columnas para la integración con Geoapify
-- NOTA: country y city se agregan permitiendo NULL inicialmente por si existen filas previas en la DB.
ALTER TABLE location ADD COLUMN country VARCHAR(255);
ALTER TABLE location ADD COLUMN city VARCHAR(255);
ALTER TABLE location ADD COLUMN district VARCHAR(255);

-- 3. Migrar los datos existentes
UPDATE location
SET
    city = name
WHERE name IS NOT NULL;

-- 4. Eliminar la columna name que ya no se utiliza
ALTER TABLE location DROP COLUMN name;

-- 5. Poner Argentina en 'country' para todas las localizaciones
UPDATE location
SET country = 'Argentina';
-- ROLLBACK
-- ALTER TABLE location DROP COLUMN street;
-- ALTER TABLE location DROP COLUMN district;
-- ALTER TABLE location DROP COLUMN city;
-- ALTER TABLE location DROP COLUMN country;
-- ALTER TABLE location ADD COLUMN name VARCHAR(255);
-- ALTER TABLE location RENAME COLUMN house_number TO number;
-- ALTER TABLE location RENAME COLUMN formatted TO address;