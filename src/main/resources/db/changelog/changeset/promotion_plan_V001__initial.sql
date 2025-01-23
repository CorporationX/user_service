CREATE TYPE promotion_target AS ENUM ('BASIC', 'PREMIUM', 'ULTIMATE');

CREATE TABLE promotion_plan (
                                id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                                name VARCHAR(32) NOT NULL,
                                impressions INT NOT NULL,
                                cost NUMERIC(10, 2) NOT NULL,
                                created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                                updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

