CREATE TABLE promotion_payment_request (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    status varchar(10) not null,
    entity_type varchar(10) not null,
    entity_id bigint not null,
    tariff_id bigserial not null references promotion_tariff(id)
);

CREATE TABLE premium_payment_request (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    status varchar(10) not null,
    user_id bigint NOT NULL references users(id),
    days int not null
);