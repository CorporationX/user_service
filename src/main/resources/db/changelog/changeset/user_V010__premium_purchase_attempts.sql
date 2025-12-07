CREATE TABLE IF NOT EXISTS premium_purchase_attempts (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_id BIGINT NOT NULL,
    payment_number VARCHAR(64) NOT NULL UNIQUE,
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT current_timestamp,
    updated_at TIMESTAMPTZ,
    completed_at TIMESTAMPTZ,
    failure_reason TEXT,

    CONSTRAINT fk_premium_attempts_user
    FOREIGN KEY (user_id)
    REFERENCES users(id)
    ON DELETE CASCADE
);

COMMENT ON TABLE premium_purchase_attempts IS 'Tracks all premium purchase attempts for idempotency and debugging';
COMMENT ON COLUMN premium_purchase_attempts.payment_number IS 'Unique payment identifier for idempotency, format: PREM-{userId}-{uuid}';
COMMENT ON COLUMN premium_purchase_attempts.status IS 'Possible values: PAYMENT_PENDING, PAYMENT_SUCCESS, COMPLETED, FAILED';