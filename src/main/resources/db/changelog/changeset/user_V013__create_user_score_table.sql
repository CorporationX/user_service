--liquibase formatted sql

--changeset trytofixme:create_user_score_table
CREATE TABLE user_score (
    user_id BIGINT PRIMARY KEY,
    score INT NOT NULL DEFAULT 0,
    CONSTRAINT fk_user_score_user_id FOREIGN KEY (user_id) REFERENCES users(id)
);