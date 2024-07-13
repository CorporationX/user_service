CREATE TABLE promotion (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    user_id bigint,
    event_id bigint,
    impressions integer NOT NULL,
    audience_reach VARCHAR(20) NOT NULL,

    CONSTRAINT fk_user_promotion_id FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_event_promotion_id FOREIGN KEY (event_id) REFERENCES event (id)
)