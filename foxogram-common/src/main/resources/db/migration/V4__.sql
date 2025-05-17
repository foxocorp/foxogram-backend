ALTER TABLE users
    RENAME COLUMN avatar TO avatar_id;

ALTER TABLE channels
    RENAME COLUMN icon TO icon_id;

ALTER TABLE attachments
    ADD COLUMN message_id BIGINT;

CREATE TABLE message_attachments
(
    id            SERIAL PRIMARY KEY,
    message_id    BIGINT NOT NULL,
    attachment_id BIGINT NOT NULL,
    CONSTRAINT fk_message FOREIGN KEY (message_id) REFERENCES messages (id) ON DELETE CASCADE,
    CONSTRAINT fk_attachment FOREIGN KEY (attachment_id) REFERENCES attachments (id) ON DELETE CASCADE
);

CREATE INDEX idx_message_attachment ON message_attachments (message_id, attachment_id);
