-- January 2024 Data
INSERT INTO qr_scan_events(visitor_id, restaurant_id, scanned_at)
VALUES ('3d90381d-80d2-48f8-80b3-d237d5f0a8ed_A', 1, '2024-01-03 10:00:00'),
       ('3d90381d-80d2-48f8-80b3-d237d5f0a8ed_B', 1, '2024-01-07 09:00:00'),
       ('3d90381d-80d2-48f8-80b3-d237d5f0a8ed_A', 1, '2024-01-15 12:00:00'),
       ('3d90381d-80d2-48f8-80b3-d237d5f0a8ed_C', 1, '2024-01-20 11:00:00'),
       ('3d90381d-80d2-48f8-80b3-d237d5f0a8ed_C', 1, '2024-01-21 15:00:00'),
       -- Extra events for January 15 (to test daily breakdown)
       ('3d90381d-80d2-48f8-80b3-d237d5f0a8ed_D', 1, '2024-01-15 10:00:00'),
       ('3d90381d-80d2-48f8-80b3-d237d5f0a8ed_D', 1, '2024-01-15 10:15:00'),
       ('3d90381d-80d2-48f8-80b3-d237d5f0a8ed_E', 1, '2024-01-15 15:00:00');

-- February 2024 Data
INSERT INTO qr_scan_events(visitor_id, restaurant_id, scanned_at)
VALUES ('3d90381d-80d2-48f8-80b3-d237d5f0a8ed_F', 1, '2024-02-10 14:00:00');

-- March 2024 Data
INSERT INTO qr_scan_events(visitor_id, restaurant_id, scanned_at)
VALUES ('3d90381d-80d2-48f8-80b3-d237d5f0a8ed_G', 1, '2024-03-05 09:00:00'),
       ('3d90381d-80d2-48f8-80b3-d237d5f0a8ed_G', 1, '2024-03-05 15:00:00');

-- April 2024 Data
INSERT INTO qr_scan_events(visitor_id, restaurant_id, scanned_at)
VALUES ('3d90381d-80d2-48f8-80b3-d237d5f0a8ed_H', 1, '2024-04-12 10:30:00'),
       ('3d90381d-80d2-48f8-80b3-d237d5f0a8ed_I', 1, '2024-04-15 16:45:00'),
       ('3d90381d-80d2-48f8-80b3-d237d5f0a8ed_H', 1, '2024-04-20 11:00:00');