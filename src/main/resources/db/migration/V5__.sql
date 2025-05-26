ALTER TABLE message_attachments
    ADD CONSTRAINT uq_attachment_id UNIQUE (attachment_id);
