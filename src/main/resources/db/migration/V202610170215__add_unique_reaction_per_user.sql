ALTER TABLE reaction
    ADD CONSTRAINT uk_reaction_post_owner UNIQUE (post_id, owner_id);

-- ROLLBACK
-- ALTER TABLE reaction DROP INDEX uk_reaction_post_owner;
