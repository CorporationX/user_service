CREATE TABLE if not exists rating_types (
    id bigint PRIMARY key GENERATED ALWAYS AS IDENTITY UNIQUE,
    name varchar(255) NOT NULL UNIQUE,
    cost float NOT NULL,
    active boolean NOT NULL default true
);