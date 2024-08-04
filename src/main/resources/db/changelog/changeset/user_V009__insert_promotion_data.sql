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