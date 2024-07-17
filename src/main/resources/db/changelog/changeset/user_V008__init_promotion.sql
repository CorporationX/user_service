CREATE TABLE promotion_type (
    id bigserial primary key,
    name varchar(30) not null unique,
    description varchar(100)
);

CREATE TABLE promotion_tariff (
    id bigserial primary key,
    name varchar(30) not null unique,
    price_usd numeric(10, 2) not null,
    create_date timestamptz NOT NULL DEFAULT current_timestamp,
    description varchar(100),
    promotion_type bigint not null,
    available_action_count bigint not null,
    active bool default false,

    foreign key (promotion_type) references promotion_type(id)
);

CREATE TABLE link_user_promotion (
    id bigserial primary key,
    user_id bigint not null,
    tariff_id bigint not null,
    used_action_count bigint not null default 0,
    active bool default false,

    foreign key (user_id) references users(id) on update cascade on delete cascade,
    foreign key (tariff_id) references promotion_tariff(id) on update cascade on delete cascade,
    CONSTRAINT unique_userId_tariffId UNIQUE (user_id, tariff_id),
    CONSTRAINT check_used_action_count CHECK (used_action_count >= 0)
);

CREATE TABLE link_event_promotion (
    id bigserial primary key,
    event_id bigint not null,
    tariff_id bigint not null,
    used_action_count bigint not null default 0,
    active bool default false,

    foreign key (event_id) references event(id) on update cascade on delete cascade,
    foreign key (tariff_id) references promotion_tariff(id) on update cascade on delete cascade,
    CONSTRAINT unique_eventId_tariffId UNIQUE (event_id, tariff_id),
    CONSTRAINT check_used_action_count CHECK (used_action_count >= 0)
);

CREATE TABLE currency (
    code varchar(3) primary key NOT NULL,
    exchange_rate numeric(10, 2) NOT NULL,
    date_updated timestamptz NOT NULL DEFAULT current_timestamp
);

INSERT INTO promotion_type (name, description) VALUES ('shows', 'показы');

INSERT INTO promotion_tariff (name, price_usd, description, promotion_type, available_action_count, active)
SELECT 'shows_100', 9.9, '100 показов', id, 100, true
FROM promotion_type
WHERE name = 'shows';

INSERT INTO promotion_tariff (name, price_usd, description, promotion_type, available_action_count, active)
SELECT 'shows_500', 199.99, '500 показов', id, 500, true
FROM promotion_type
WHERE name = 'shows';

INSERT INTO promotion_tariff (name, price_usd, description, promotion_type, available_action_count, active)
SELECT 'shows_1000', 249.99, '1000 показов', id, 1000, true
FROM promotion_type
WHERE name = 'shows';

INSERT INTO currency (code, exchange_rate)
VALUES ('USD', 1.0), ('RUB', 88.60), ('EUR', 0.91);
