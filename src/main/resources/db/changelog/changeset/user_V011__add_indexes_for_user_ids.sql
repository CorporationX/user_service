CREATE INDEX idx_subscription_follower_followee_id ON subscription (follower_id, followee_id);
CREATE INDEX idx_users_active ON users (active);