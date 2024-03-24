ALTER TABLE users
    ADD COLUMN if not exists banned boolean DEFAULT false NOT NULL;