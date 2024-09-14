CREATE TABLE user_promotion (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    user_id bigint NOT NULL,
    priority_level int NOT NULL,
    remaining_shows int NOT NULL,
    promotion_target varchar(32) NOT NULL,

    CONSTRAINT fk_user_promotion_id FOREIGN KEY (user_id) REFERENCES users (id)
    );