CREATE TABLE promotion (
                           id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                           user_id BIGINT NOT NULL,
                           target promotion_target NOT NULL,
                           plan_id BIGINT NOT NULL,
                           impressions_limit INT NOT NULL,
                           current_impressions INT DEFAULT 0,
                           is_active BOOLEAN DEFAULT FALSE,
                           start_time TIMESTAMPTZ DEFAULT NULL,
                           created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,

                           CONSTRAINT fk_plan_id FOREIGN KEY (plan_id) REFERENCES promotion_plan (id)
);
