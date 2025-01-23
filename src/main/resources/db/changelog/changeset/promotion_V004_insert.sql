INSERT INTO promotion (user_id, target, plan_id, impressions_limit, current_impressions, is_active, start_time)
VALUES
    (1, 'BASIC', 1, 1000, 100, true, NOW()),
    (2, 'PREMIUM', 2, 5000, 300, true, NOW()),
    (3, 'ULTIMATE', 3, 10000, 500, false, NOW());