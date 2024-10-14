CREATE TABLE user_promotion (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    user_id bigint NOT NULL,
    priority int NOT NULL,
    show_count int NOT NULL,
    target varchar NOT NULL,

    CONSTRAINT fk_user_promotion_id FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE SEQUENCE premium_payment_number_sequence
    INCREMENT BY 1
    START WITH 1;

CREATE SEQUENCE promotion_payment_number_sequence
    INCREMENT BY 1
    START WITH 1;