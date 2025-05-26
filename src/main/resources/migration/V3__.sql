ALTER TABLE members
    RENAME COLUMN channel TO channel_id;

ALTER TABLE messages
    RENAME COLUMN channel TO channel_id;