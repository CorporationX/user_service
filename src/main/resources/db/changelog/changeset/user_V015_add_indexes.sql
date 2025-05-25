CREATE INDEX idx_event_promotion_event_id_active
    ON event_promotion (event_id, active);

CREATE INDEX idx_profile_promotion_profile_id_active
    ON profile_promotion (profile_id, active);