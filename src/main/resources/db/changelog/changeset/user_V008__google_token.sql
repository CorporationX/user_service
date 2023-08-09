ALTER TABLE users ADD COLUMN google_token_id BIGINT;

CREATE TABLE google_token (
                              id SERIAL PRIMARY KEY,
                              uuid VARCHAR(64) NOT NULL UNIQUE,
                              user_id BIGINT NOT NULL,
                              oauth_client_id VARCHAR(32) NOT NULL,
                              access_token VARCHAR(255) NOT NULL,
                              refresh_token VARCHAR(255),
                              expiration_time_milliseconds BIGINT,
                              updated_at TIMESTAMP NOT NULL,
                              CONSTRAINT fk_user_token_id FOREIGN KEY (user_id) REFERENCES users (id)
);