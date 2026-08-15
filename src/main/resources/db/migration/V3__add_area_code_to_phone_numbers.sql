ALTER TABLE animal_post ADD COLUMN area_code VARCHAR(255);
ALTER TABLE profile ADD COLUMN area_code VARCHAR(255);
ALTER TABLE news_campaign ADD COLUMN area_code VARCHAR(255);
ALTER TABLE donation_campaign ADD COLUMN area_code VARCHAR(255);
ALTER TABLE fundraising_campaign ADD COLUMN area_code VARCHAR(255);
ALTER TABLE vet_information ADD COLUMN area_code VARCHAR(255);

UPDATE animal_post
SET area_code = LEFT(phone_number, 4),
    phone_number = SUBSTRING(phone_number, 5)
WHERE CHAR_LENGTH(phone_number) = 10;

UPDATE profile
SET area_code = LEFT(phone_number, 4),
    phone_number = SUBSTRING(phone_number, 5)
WHERE CHAR_LENGTH(phone_number) = 10;

UPDATE news_campaign
SET area_code = LEFT(phone_number, 4),
    phone_number = SUBSTRING(phone_number, 5)
WHERE CHAR_LENGTH(phone_number) = 10;

UPDATE donation_campaign
SET area_code = LEFT(phone_number, 4),
    phone_number = SUBSTRING(phone_number, 5)
WHERE CHAR_LENGTH(phone_number) = 10;

UPDATE fundraising_campaign
SET area_code = LEFT(phone_number, 4),
    phone_number = SUBSTRING(phone_number, 5)
WHERE CHAR_LENGTH(phone_number) = 10;

UPDATE vet_information
SET area_code = LEFT(phone, 4),
    phone = SUBSTRING(phone, 5)
WHERE CHAR_LENGTH(phone) = 10;
