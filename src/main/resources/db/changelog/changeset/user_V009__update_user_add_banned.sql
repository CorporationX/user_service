ALTER TABLE users
    ADD COLUMN if not exists banned boolean;
UPDATE users
SET banned = false
WHERE users.banned IS NULL;