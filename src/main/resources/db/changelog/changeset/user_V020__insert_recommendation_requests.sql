INSERT INTO recommendation_request (message,requester_id,receiver_id,status,rejection_reason,recommendation_id,created_at,updated_at) VALUES
	 ('recommendation request from 2 to 1',2,1,1,'',1,'2024-05-11 00:35:12.572671+03','2024-05-11 00:35:12.572671+03'),
	 ('recommendation request from 3 to 2',3,2,1,'',2,'2024-05-11 00:35:12.572671+03','2024-05-11 00:35:12.572671+03'),
	 ('recommendation request from 4 to 3',4,3,1,'',3,'2024-05-11 00:35:12.572671+03','2024-05-11 00:35:12.572671+03'),
	 ('recommendation request from 5 to 4',5,4,1,'',4,'2024-05-11 00:35:12.572671+03','2024-05-11 00:35:12.572671+03'),
	 ('recommendation request from 6 to 5',6,5,1,'',5,'2024-05-11 00:35:12.572671+03','2024-05-11 00:35:12.572671+03'),
	 ('recommendation request from 7 to 6',7,8,1,'',6,'2024-05-11 00:35:12.572671+03','2024-05-11 00:35:12.572671+03'),
	 ('recommendation request from 8 to 7',8,7,1,'',7,'2024-05-11 00:35:12.572671+03','2024-05-11 00:35:12.572671+03'),
	 ('recommendation request from 9 to 8',9,8,1,'',8,'2024-05-11 00:35:12.572671+03','2024-05-11 00:35:12.572671+03'),
	 ('recommendation request from 10 to 9',10,9,1,'',9,'2024-05-11 00:35:12.572671+03','2024-05-11 00:35:12.572671+03');
INSERT INTO recommendation_request (message,requester_id,receiver_id,status,rejection_reason,recommendation_id,created_at,updated_at) VALUES
	 ('recommendation request from 3 to 1',3,1,0,'',NULL,'2024-05-11 00:35:12.572671+03','2024-05-11 00:35:12.572671+03'),
	 ('recommendation request from 5 to 3',5,3,0,'',NULL,'2024-05-11 00:35:12.572671+03','2024-05-11 00:35:12.572671+03'),
	 ('recommendation request from 7 to 5',7,5,0,'',NULL,'2024-05-11 00:35:12.572671+03','2024-05-11 00:35:12.572671+03'),
	 ('recommendation request from 9 to 7',9,7,0,'',NULL,'2024-05-11 00:35:12.572671+03','2024-05-11 00:35:12.572671+03'),
	 ('recommendation request from 10 to 8',10,8,2,'rejection reason from 10 to 8',NULL,'2024-05-11 00:35:12.572671+03','2024-05-11 00:35:12.572671+03'),
	 ('recommendation request from 4 to 2',4,2,2,'rejection reason from 4 to 2',NULL,'2024-05-11 00:35:12.572671+03','2024-05-11 00:35:12.572671+03'),
	 ('recommendation request from 6 to 4',6,4,2,'rejection reason from 6 to 4',NULL,'2024-05-11 00:35:12.572671+03','2024-05-11 00:35:12.572671+03'),
	 ('recommendation request from 8 to 6',8,6,2,'rejection reason from 8 to 6',NULL,'2024-05-11 00:35:12.572671+03','2024-05-11 00:35:12.572671+03');

