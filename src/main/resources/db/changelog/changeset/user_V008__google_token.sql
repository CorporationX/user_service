ALTER TABLE users ADD COLUMN google_token_id BIGINT;

 CREATE TABLE google_token (
                               id SERIAL PRIMARY KEY,
                               uuid VARCHAR(255),
                               user_id BIGINT,
                               oauth_client_id VARCHAR(255),
                               access_token VARCHAR(255),
                               refresh_token VARCHAR(255),
                               expiration_time_milliseconds BIGINT,
                               updated_at TIMESTAMP,
                               CONSTRAINT fk_user_token_id FOREIGN KEY (user_id) REFERENCES users (id)
 );