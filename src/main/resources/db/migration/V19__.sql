ALTER TABLE users
    DROP COLUMN avatar_id,
    ADD COLUMN avatar_id BIGINT NOT NULL,
    DROP COLUMN banner_id,
    ADD COLUMN banner_id BIGINT NOT NULL;

ALTER TABLE channels
    DROP COLUMN avatar_id,
    ADD COLUMN avatar_id BIGINT NOT NULL;