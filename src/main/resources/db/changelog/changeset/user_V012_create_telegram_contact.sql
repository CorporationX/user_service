CREATE TABLE telegram_contact (
    id SERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    telegram_user_name VARCHAR(64) NOT NULL,
    telegram_user_id VARCHAR(32),
    CONSTRAINT fk_user
        FOREIGN KEY(user_id) 
        REFERENCES users(id)
        ON DELETE CASCADE,
    CONSTRAINT unique_telegram_user_name UNIQUE (telegram_user_name)
);
