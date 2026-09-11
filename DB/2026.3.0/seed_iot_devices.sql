-- ============================================================
-- Seed 10 fishing boat IoT devices
-- Location: Mohipur Fish Landing Center, Patuakhali, Bangladesh
-- ============================================================

INSERT INTO iot_devices (device_id, name, api_key_hash, hmac_secret_hash,
                         last_latitude, last_longitude, last_device_timestamp, last_seen,
                         created, updated)
VALUES
    ('BOAT-001', 'Padma Star',      '$2a$10$KEO/rWDMs95SI33WBRzrfuLbuHI8XGZYn4c6gh9SDW3kJG5mKzY5i', NULL, NULL, NULL, NULL, NULL, NOW(3), NOW(3)),
    ('BOAT-002', 'Meghna Queen',    '$2a$10$KEO/rWDMs95SI33WBRzrfuLbuHI8XGZYn4c6gh9SDW3kJG5mKzY5i', NULL, NULL, NULL, NULL, NULL, NOW(3), NOW(3)),
    ('BOAT-003', 'Jamuna Fisher',   '$2a$10$KEO/rWDMs95SI33WBRzrfuLbuHI8XGZYn4c6gh9SDW3kJG5mKzY5i', NULL, NULL, NULL, NULL, NULL, NOW(3), NOW(3)),
    ('BOAT-004', 'Surma Voyager',   '$2a$10$KEO/rWDMs95SI33WBRzrfuLbuHI8XGZYn4c6gh9SDW3kJG5mKzY5i', NULL, NULL, NULL, NULL, NULL, NOW(3), NOW(3)),
    ('BOAT-005', 'Karnaphuli Dawn', '$2a$10$KEO/rWDMs95SI33WBRzrfuLbuHI8XGZYn4c6gh9SDW3kJG5mKzY5i', NULL, NULL, NULL, NULL, NULL, NOW(3), NOW(3)),
    ('BOAT-006', 'Tetulia Wind',    '$2a$10$KEO/rWDMs95SI33WBRzrfuLbuHI8XGZYn4c6gh9SDW3kJG5mKzY5i', NULL, NULL, NULL, NULL, NULL, NOW(3), NOW(3)),
    ('BOAT-007', 'Halda Current',   '$2a$10$KEO/rWDMs95SI33WBRzrfuLbuHI8XGZYn4c6gh9SDW3kJG5mKzY5i', NULL, NULL, NULL, NULL, NULL, NOW(3), NOW(3)),
    ('BOAT-008', 'Sangu Tide',      '$2a$10$KEO/rWDMs95SI33WBRzrfuLbuHI8XGZYn4c6gh9SDW3kJG5mKzY5i', NULL, NULL, NULL, NULL, NULL, NOW(3), NOW(3)),
    ('BOAT-009', 'Bakkhali Breeze', '$2a$10$KEO/rWDMs95SI33WBRzrfuLbuHI8XGZYn4c6gh9SDW3kJG5mKzY5i', NULL, NULL, NULL, NULL, NULL, NOW(3), NOW(3)),
    ('BOAT-010', 'Mohipur Pride',   '$2a$10$KEO/rWDMs95SI33WBRzrfuLbuHI8XGZYn4c6gh9SDW3kJG5mKzY5i', NULL, NULL, NULL, NULL, NULL, NOW(3), NOW(3));
