--liquibase formatted sql

--changeset trytofixme:create_score_rule_table
CREATE TABLE score_rule (
    id BIGSERIAL PRIMARY KEY,
    type VARCHAR(255) NOT NULL,
    role_id BIGINT UNIQUE,
    score INT NOT NULL DEFAULT 0,
    CONSTRAINT fk_score_rule_role FOREIGN KEY (role_id) REFERENCES role(id)
);