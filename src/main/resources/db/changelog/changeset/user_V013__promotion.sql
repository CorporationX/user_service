CREATE TABLE promotion_plan(
    id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    plan VARCHAR(25) NOT NULL check (plan in ('VIP', 'GOLD', 'PLUS')),
    num_promoted_views INTEGER NOT NULL,
    view_width VARCHAR(25) NOT NULL check (view_width in ('PUBLIC', 'FRIENDS')),
    price numeric(10,2) NOT NULL,
    currency CHAR(3) NOT NULL CHECK (currency IN ('USD','EUR'))
);

CREATE TABLE product(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    product_type VARCHAR(50) NOT NULL,
    price numeric(10,2) NOT NULL,
    currency CHAR(3) NOT NULL CHECK (currency IN ('USD','EUR'))
);

CREATE TABLE profile_promotion(
    id BIGINT PRIMARY KEY,
    client_id BIGINT NOT NULL,
    profile_id BIGINT NOT NULL,
    created_at timestamptz DEFAULT current_timestamp,
    updated_at timestamptz DEFAULT current_timestamp,
    active BOOLEAN NOT NULL DEFAULT false,
    current_views BIGINT NOT NULL DEFAULT 0,
    num_promoted_views INTEGER NOT NULL,
    plan VARCHAR(25) NOT NULL check (plan in ('VIP', 'GOLD', 'PLUS')),
    view_width VARCHAR(25) NOT NULL check (view_width in ('PUBLIC', 'FRIENDS')),
    name VARCHAR(100) NOT NULL,
    transaction_purpose VARCHAR(100) NOT NULL,

    CONSTRAINT fk_profile_promotion_to_product FOREIGN KEY (id) REFERENCES product(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_profile_promotion_client_to_users FOREIGN KEY (client_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_profile_promotion_profileId_to_users FOREIGN KEY (profile_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE event_promotion(
    id BIGINT PRIMARY KEY,
    client_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    created_at timestamptz DEFAULT current_timestamp,
    updated_at timestamptz DEFAULT current_timestamp,
    active BOOLEAN NOT NULL DEFAULT false,
    current_views BIGINT NOT NULL DEFAULT 0,
    num_promoted_views INTEGER NOT NULL,
    plan VARCHAR(25) NOT NULL check (plan in ('VIP', 'GOLD', 'PLUS')),
    view_width VARCHAR(25) NOT NULL check (view_width in ('PUBLIC', 'FRIENDS')),
    name VARCHAR(100) NOT NULL,
    transaction_purpose VARCHAR(100) NOT NULL,

    CONSTRAINT fk_event_promotion_to_product FOREIGN KEY (id) REFERENCES product(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_event_promotion_to_users FOREIGN KEY (client_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_event_promotion_to_event FOREIGN KEY (event_id) REFERENCES event(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE transaction_product(
    transaction_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    CONSTRAINT fk_transaction_product_to_product FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_transaction_product_to_transaction FOREIGN KEY (transaction_id) REFERENCES transactions(id) ON DELETE CASCADE ON UPDATE CASCADE
);