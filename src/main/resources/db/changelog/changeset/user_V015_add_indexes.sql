CREATE INDEX idx_event_promotion_event_id
    ON event_promotion (event_id);

CREATE INDEX idx_profile_promotion_profile_id
    ON profile_promotion (profile_id);

CREATE INDEX idx_ep_plan_event_active_true
    ON event_promotion (plan, event_id)
    WHERE active = true;

CREATE INDEX idx_pp_plan_profile_active_true
    ON profile_promotion (plan, profile_id)
    WHERE active = true;

CREATE INDEX idx_pp_current_views_active_true
    ON profile_promotion (current_views)
    WHERE active = true;

CREATE INDEX idx_ep_current_views_active_true
    ON event_promotion (current_views)
    WHERE active = true;