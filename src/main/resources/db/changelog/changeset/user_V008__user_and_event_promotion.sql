CREATE TABLE user_promotion
(
    id               bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    promotion_tariff varchar(32)      NOT NULL,
    cost             double precision NOT NULL,
    currency         varchar(32)      NOT NULL,
    coefficient      double precision NOT NULL,
    user_id          bigint           NOT NULL,
    number_of_views  bigint           NOT NULL,
    audience_reach   bigint           NOT NULL,
    creation_date    timestamptz      NOT NULL DEFAULT current_timestamp,
    CONSTRAINT fk_user_promotion_id FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE event_promotion
(
    id               bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    promotion_tariff varchar(32)      NOT NULL,
    cost             double precision NOT NULL,
    currency         varchar(32)      NOT NULL,
    coefficient      double precision NOT NULL,
    event_id         bigint           NOT NULL,
    number_of_views  bigint           NOT NULL,
    audience_reach   bigint           NOT NULL,
    creation_date    timestamptz      NOT NULL DEFAULT current_timestamp,
    CONSTRAINT fk_event_promotion_id FOREIGN KEY (event_id) REFERENCES event (id)
);