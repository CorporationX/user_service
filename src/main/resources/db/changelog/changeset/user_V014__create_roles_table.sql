--liquibase formatted sql

--changeset trytofixme:create_roles_table
CREATE TABLE role (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);