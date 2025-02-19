ALTER TABLE goal
DROP CONSTRAINT fk_goal_id;

ALTER TABLE goal
ADD CONSTRAINT fk_goal_id FOREIGN KEY (parent_goal_id) REFERENCES goal (id) ON DELETE CASCADE;

ALTER TABLE goal
DROP CONSTRAINT fk_mentor_id;

ALTER TABLE goal
ADD CONSTRAINT fk_mentor_id FOREIGN KEY (mentor_id) REFERENCES users (id) ON DELETE CASCADE;

ALTER TABLE goal_skill
DROP CONSTRAINT fk_goal_skill_id;

ALTER TABLE goal_skill
ADD CONSTRAINT fk_goal_skill_id FOREIGN KEY (goal_id) REFERENCES goal (id) ON DELETE CASCADE;

ALTER TABLE goal_skill
DROP CONSTRAINT fk_skill_goal_id;

ALTER TABLE goal_skill
ADD CONSTRAINT fk_skill_goal_id FOREIGN KEY (skill_id) REFERENCES skill (id) ON DELETE CASCADE;

ALTER TABLE user_goal
DROP CONSTRAINT fk_user_goal_id;

ALTER TABLE user_goal
ADD CONSTRAINT fk_user_goal_id FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE;

ALTER TABLE user_goal
DROP CONSTRAINT fk_goal_user_id;

ALTER TABLE user_goal
ADD CONSTRAINT fk_goal_user_id FOREIGN KEY (goal_id) REFERENCES goal (id) ON DELETE CASCADE;