CREATE TABLE iot_devices
(
    id                    BIGINT AUTO_INCREMENT PRIMARY KEY,
    device_id             VARCHAR(100) NOT NULL,
    name                  VARCHAR(255) NOT NULL,
    api_key_hash          VARCHAR(255) NULL,
    hmac_secret_hash      VARCHAR(255) NULL,
    last_latitude         DOUBLE NULL,
    last_longitude        DOUBLE NULL,
    last_device_timestamp TIMESTAMP(3) NULL,
    last_seen             TIMESTAMP(3) NULL,
    created               TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated               TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    CONSTRAINT uk_iot_devices_device_id UNIQUE (device_id)
);

CREATE TABLE iot_device_locations
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    device_pk        BIGINT NOT NULL,
    latitude         DOUBLE NOT NULL,
    longitude        DOUBLE NOT NULL,
    device_timestamp TIMESTAMP(3) NOT NULL,
    received_at      TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    event_id         VARCHAR(150) NULL,
    created          TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated          TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    CONSTRAINT fk_iot_device_locations_device FOREIGN KEY (device_pk) REFERENCES iot_devices (id),
    CONSTRAINT uk_iot_device_locations_event UNIQUE (device_pk, event_id)
);

CREATE INDEX idx_iot_device_locations_device_received
    ON iot_device_locations (device_pk, received_at DESC);

CREATE INDEX idx_iot_device_locations_device_device_time
    ON iot_device_locations (device_pk, device_timestamp DESC);
