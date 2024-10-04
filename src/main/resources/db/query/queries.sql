-- UPDATE_USER_PROMOTION
UPDATE user_promotion
SET number_of_views = GREATEST(number_of_views - ?, 0)
WHERE id = ?