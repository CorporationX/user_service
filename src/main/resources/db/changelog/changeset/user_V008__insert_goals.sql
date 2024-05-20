INSERT INTO goal (title, description, status, mentor_id)
VALUES
    ('Create migration', 'Create migration for user_service', 0, 1),
    ('Recommendation request', 'Feature recommendation request', 0, 1),
    ('Swagger for project service', 'Connect and set Swagger for project service', 0, 1),
    ('Jacoco for project service', 'Connect and set Jacoco', 0, 1),
    ('Vacancies', 'Create feature for vacancies', 0, 1);

INSERT INTO user_goal (user_id, goal_id)
VALUES
    (2, 1),
    (2, 2),
    (3, 3),
    (4, 4),
    (5, 5);

INSERT INTO mentorship (mentor_id, mentee_id)
VALUES
    (1, 2),
    (1, 3),
    (1, 4);