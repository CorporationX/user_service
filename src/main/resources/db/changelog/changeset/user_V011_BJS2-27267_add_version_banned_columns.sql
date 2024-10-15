ALTER TABLE users
    ADD COLUMN if not exists version INT NOT NULL DEFAULT 0;

ALTER TABLE users
    ADD COLUMN if not exists banned BOOLEAN NOT NULL DEFAULT false;
