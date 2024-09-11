CREATE TABLE user_promotion
(
    id               bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    promotion_tariff smallint             DEFAULT 0 NOT NULL,
    coefficient      bigint      NOT NULL,
    user_id          bigint      NOT NULL,
    number_of_views  bigint      NOT NULL,
    audience_reach   bigint      NOT NULL,
    creation_date    timestamptz NOT NULL DEFAULT current_timestamp,
    CONSTRAINT fk_user_promotion_id FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE event_promotion
(
    id               bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    promotion_tariff smallint             DEFAULT 0 NOT NULL,
    coefficient      bigint      NOT NULL,
    event_id         bigint      NOT NULL,
    number_of_views  bigint      NOT NULL,
    audience_reach   bigint      NOT NULL,
    creation_date    timestamptz NOT NULL DEFAULT current_timestamp,
    CONSTRAINT fk_event_promotion_id FOREIGN KEY (event_id) REFERENCES event (id)
);