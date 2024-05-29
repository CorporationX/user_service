ALTER TABLE user_event
DROP CONSTRAINT fk_event_user_id;

ALTER TABLE user_event
ADD CONSTRAINT fk_event_user_id FOREIGN KEY (event_id) REFERENCES event (id) on delete cascade;



