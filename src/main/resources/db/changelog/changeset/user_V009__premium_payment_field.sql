ALTER TABLE user_premium
    ADD COLUMN IF NOT EXISTS period varchar(32) NOT NULL,
    ADD COLUMN IF NOT EXISTS amount numeric(19,2) NOT NULL,
    ADD COLUMN IF NOT EXISTS payment_number varchar(64),
    ADD COLUMN IF NOT EXISTS verification_code varchar(128),
    ADD COLUMN IF NOT EXISTS currency varchar(8) NOT NULL DEFAULT 'EUR',
    ADD COLUMN IF NOT EXISTS created_at timestamptz NOT NULL DEFAULT current_timestamp;