-- Скрипт для создания таблицы user_promotion
CREATE TABLE user_promotion (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    user_id bigint NOT NULL,
    priority_level int NOT NULL,
    remaining_shows int NOT NULL,
    promotion_target varchar(32) NOT NULL,

    CONSTRAINT fk_user_promotion_id FOREIGN KEY (user_id) REFERENCES users (id)
    );

-- Скрипт для создания последовательности для промоушн-платежей
CREATE SEQUENCE promotion_payment_number_sequence
    INCREMENT BY 1
    START WITH 1;

-- Скрипт для создания последовательности для премиум-платежей
CREATE SEQUENCE premium_payment_number_sequence
    INCREMENT BY 1
    START WITH 1;