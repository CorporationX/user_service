CREATE TABLE refresh_tokens (
    id          BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    user_id     BIGINT NOT NULL,
    token       TEXT    NOT NULL UNIQUE,
    is_revoked  BOOLEAN NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expired_at  TIMESTAMP NOT NULL,

    CONSTRAINT fk_refresh_tokens_user_id
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE INDEX idx_refresh_tokens_token
    ON refresh_tokens(token);