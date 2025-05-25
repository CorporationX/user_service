CREATE INDEX idx_event_promotion_event_id_active
    ON event_promotion (event_id, active);

CREATE INDEX idx_profile_promotion_profile_id_active
    ON profile_promotion (profile_id, active);

CREATE INDEX idx_event_promotion_active_plan_event_id
    ON event_promotion (active, plan, event_id);

CREATE INDEX idx_profile_promotion_active_plan_profile_id
    ON profile_promotion (active, plan, profile_id);