CREATE TYPE promotion_target AS ENUM ('BASIC', 'PREMIUM', 'ULTIMATE');

CREATE TABLE promotion_plan (
                                id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                                name VARCHAR(32) NOT NULL,
                                impressions INT NOT NULL,
                                cost NUMERIC(10, 2) NOT NULL,
                                created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                                updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO promotion_plan (name, impressions, cost)
VALUES
    ('BASIC', 1000, 9.99),
    ('PREMIUM', 5000, 29.99),
    ('ULTIMATE', 10000, 49.99);
