ALTER TABLE users
ADD COLUMN chat_id BIGINT,
ADD CONSTRAINT uk_users_chat_id UNIQUE (chat_id);