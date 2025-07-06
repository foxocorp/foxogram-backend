ALTER TABLE avatars
    ADD COLUMN tumbhash VARCHAR(255);

ALTER TABLE attachments
    ADD COLUMN tumbhash VARCHAR(255);