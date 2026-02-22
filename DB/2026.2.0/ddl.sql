CREATE TABLE comments
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    content     TEXT   NOT NULL,
    incident_id BIGINT NOT NULL,
    user_id     BIGINT NOT NULL,
    created_at  TIMESTAMP NULL,
    updated_at  TIMESTAMP NULL,
    CONSTRAINT fk_comments_incident FOREIGN KEY (incident_id) REFERENCES incidents (id),
    CONSTRAINT fk_comments_user FOREIGN KEY (user_id) REFERENCES users (id)
);

ALTER TABLE incidents
    ADD COLUMN video_url VARCHAR(1000) NULL AFTER status;