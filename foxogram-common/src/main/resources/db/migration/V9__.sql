ALTER TABLE users
    ADD contact_id BIGINT;

CREATE TABLE user_contacts
(
    id         SERIAL PRIMARY KEY,
    user_id    BIGINT NOT NULL,
    contact_id BIGINT NOT NULL,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_contact FOREIGN KEY (contact_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_user_contact ON user_contacts (user_id, contact_id);
