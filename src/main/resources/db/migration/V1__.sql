CREATE TABLE users
(
    id                BIGINT PRIMARY KEY,
    display_name      VARCHAR(255),
    username          VARCHAR(255),
    bio               TEXT,
    avatar_id         BIGINT,
    banner_id         BIGINT,
    flags             BIGINT,
    type              INTEGER,
    email             VARCHAR(255),
    status            INTEGER,
    status_updated_at BIGINT,
    password          VARCHAR(255),
    token_version     INTEGER,
    created_at        BIGINT,
    deleted_at        BIGINT
);

CREATE TABLE user_contacts
(
    id         BIGINT PRIMARY KEY,
    user_id    BIGINT REFERENCES users (id),
    contact_id BIGINT REFERENCES users (id)
);

CREATE TABLE otps
(
    user_id    BIGINT PRIMARY KEY REFERENCES users (id),
    type       VARCHAR(50),
    value      VARCHAR(255),
    issued_at  BIGINT,
    expires_at BIGINT
);

CREATE TABLE channels
(
    id         BIGINT PRIMARY KEY,
    display_name VARCHAR(255),
    name         VARCHAR(255),
    avatar_id  BIGINT,
    banner_id  BIGINT,
    type         INTEGER,
    flags        BIGINT,
    created_at BIGINT
);

CREATE TABLE members
(
    id         BIGINT PRIMARY KEY,
    permissions BIGINT,
    joined_at   BIGINT,
    user_id    BIGINT REFERENCES users (id),
    channel_id BIGINT REFERENCES channels (id)
);

CREATE TABLE messages
(
    id      BIGINT PRIMARY KEY,
    content   TEXT,
    author  BIGINT REFERENCES members (id),
    timestamp BIGINT,
    channel BIGINT REFERENCES channels (id)
);

CREATE TABLE attachments
(
    id           BIGINT PRIMARY KEY,
    user_id      BIGINT REFERENCES users (id),
    uuid         VARCHAR(255),
    filename     VARCHAR(255),
    content_type VARCHAR(255),
    flags        BIGINT,
    tumbhash     VARCHAR(255)
);

CREATE TABLE message_attachments
(
    id            BIGINT PRIMARY KEY,
    message_id    BIGINT REFERENCES messages (id),
    attachment_id BIGINT REFERENCES attachments (id)
);

CREATE TABLE avatars
(
    id         BIGINT PRIMARY KEY,
    user_id    BIGINT,
    channel_id BIGINT,
    uuid       VARCHAR(255),
    filename   VARCHAR(255),
    tumbhash   VARCHAR(255)
);


ALTER TABLE avatars
    ADD CONSTRAINT fk_avatar_user FOREIGN KEY (user_id) REFERENCES users (id);
ALTER TABLE avatars
    ADD CONSTRAINT fk_avatar_channel FOREIGN KEY (channel_id) REFERENCES channels (id);