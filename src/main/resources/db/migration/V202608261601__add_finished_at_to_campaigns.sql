ALTER TABLE donation_campaign
    ADD COLUMN finished_at DATETIME NULL;

ALTER TABLE fundraising_campaign
    ADD COLUMN finished_at DATETIME NULL;

ALTER TABLE news_campaign
    ADD COLUMN finished_at DATETIME NULL;


-- ROLLBACK
-- ALTER TABLE donation_campaign
--     DROP COLUMN finished_at;

-- ALTER TABLE fundraising_campaign
--     DROP COLUMN finished_at;

-- ALTER TABLE news_campaign
--     DROP COLUMN finished_at;