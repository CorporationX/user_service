CREATE TABLE user_premium (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    user_id bigint NOT NULL,
    start_date timestamptz NOT NULL DEFAULT current_timestamp,
    end_date timestamptz NOT NULL,

    CONSTRAINT fk_user_premium_id FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE user_activity (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    user_id bigint NOT NULL,
    rating bigint NOT NULL,

    CONSTRAINT fk_user_activity_id FOREIGN KEY (user_id) REFERENCES users (id)
);