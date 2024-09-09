-- добавил goal чтобы можно было вызвать метод GoalInvitationService.createInvitation()
INSERT INTO goal (title, description, parent_goal_id, deadline, mentor_id)
VALUES ('some goal', 'some goal description', null, null, 3);