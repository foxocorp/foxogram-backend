ALTER TABLE users
    ADD banner_id BIGINT,
    ADD CONSTRAINT FK_USERS_ON_BANNER FOREIGN KEY (banner_id) REFERENCES attachments (id);
