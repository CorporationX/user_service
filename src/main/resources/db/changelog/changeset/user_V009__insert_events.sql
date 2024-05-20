INSERT INTO event (title, description, start_date, end_date, "location", user_id, "type", status)
VALUES
    ('Webinar', 'Java webinar', '2024-05-01 10:00:00+03', '2024-05-01 14:00:00+03', 'Zoom', 1, 0, 0),
    ('Retro', 'Retro spring', '2024-05-09 10:00:00+03', '2024-05-09 17:00:00+03', 'Discord', 1, 0, 0),
    ('QA evening', 'QA multithreading', '2024-05-17 10:00:00+03', '2024-05-17 15:00:00+03', 'Moscow', 1, 0, 0),
    ('Sprint', 'Scram sprint', '2024-05-10 10:00:00+03', '2024-05-10 15:00:00+03', 'Moscow', 2, 0, 0),
    ('Presentation', 'Docker presentation', '2024-05-01 10:00:00+03', '2024-05-01 14:00:00+03', 'Zoom', 1, 0, 0);

INSERT INTO user_event (user_id, event_id)
VALUES
    (1, 1),
    (1, 2),
    (1, 3),
    (1, 4),
    (2, 4);
