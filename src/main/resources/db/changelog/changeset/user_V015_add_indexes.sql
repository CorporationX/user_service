/*CREATE INDEX idx_event_promotion_event_id_active
    ON event_promotion (event_id, active);
*/
/*CREATE INDEX idx_profile_promotion_profile_id_active
    ON profile_promotion (profile_id, active);*/

CREATE INDEX idx_ep_plan_event_active_true
    ON event_promotion (plan, event_id)
    WHERE active = true;

CREATE INDEX idx_pp_plan_event_active_true
    ON profile_promotion (plan, event_id)
    WHERE active = true;

CREATE INDEX idx_active_profile_promotion
    ON profile_promotion (current_views)
    WHERE active = true;

CREATE INDEX idx_active_event_promotion
    ON event_promotion (current_views)
    WHERE active = true;