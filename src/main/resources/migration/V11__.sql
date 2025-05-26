DROP INDEX idx_user_contact;
CREATE UNIQUE INDEX idx_user_contact ON user_contacts (user_id, contact_id);
