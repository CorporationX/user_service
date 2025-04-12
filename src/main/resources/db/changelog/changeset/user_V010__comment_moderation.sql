ALTER TABLE users
    ADD COLUMN IF NOT EXISTS unverified_comments int,
    ADD COLUMN IF NOT EXISTS banned boolean DEFAULT false;
