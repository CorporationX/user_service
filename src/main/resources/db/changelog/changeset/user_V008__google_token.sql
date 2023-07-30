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

INSERT INTO event (title, description, start_date, end_date, location, max_attendees, user_id, type, status)
VALUES ('Заголовок события', 'Описание события', '2023-07-26 10:00:00', '2023-07-26 15:00:00', 'Местоположение события', 100, 1, 1, 1);