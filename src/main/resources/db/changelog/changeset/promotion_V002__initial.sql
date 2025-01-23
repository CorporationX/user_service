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

INSERT INTO promotion (user_id, target, plan_id, impressions_limit, current_impressions, is_active, start_time)
VALUES
    (1, 'BASIC', 1, 1000, 100, true, NOW()),
    (2, 'PREMIUM', 2, 5000, 300, true, NOW()),
    (3, 'ULTIMATE', 3, 10000, 500, false, NOW());