ALTER TABLE users
    DROP COLUMN key;

ALTER TABLE users
    RENAME COLUMN deletion TO deleted_at;
