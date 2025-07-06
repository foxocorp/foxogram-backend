ALTER TABLE users
    DROP COLUMN avatar_id,
    ADD COLUMN avatar_id BIGINT,
    DROP COLUMN banner_id,
    ADD COLUMN banner_id BIGINT;

ALTER TABLE channels
    DROP COLUMN avatar_id,
    ADD COLUMN avatar_id BIGINT;