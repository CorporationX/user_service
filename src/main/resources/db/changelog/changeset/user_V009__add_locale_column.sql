ALTER TABLE users
    ADD COLUMN locale varchar(32) DEFAULT 'UK';

UPDATE users SET locale = 'UK' WHERE locale IS NULL;