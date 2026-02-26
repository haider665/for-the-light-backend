CREATE TABLE programs
(
    id                    BIGINT AUTO_INCREMENT PRIMARY KEY,
    title                 VARCHAR(255) NOT NULL,
    description           TEXT,
    display_image         VARCHAR(255),
    program_schedule      VARCHAR(255),
    program_start_date    TIMESTAMP,
    registration_deadline TIMESTAMP,
    status                VARCHAR(50),
    created_by_user_id    BIGINT,
    version               BIGINT,
    created_at            TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at            TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_program_created_by FOREIGN KEY (created_by_user_id) REFERENCES users (id)
);

CREATE TABLE program_enrollments
(
    program_id BIGINT NOT NULL,
    user_id    BIGINT NOT NULL,
    PRIMARY KEY (program_id, user_id),
    CONSTRAINT fk_enrollment_program FOREIGN KEY (program_id) REFERENCES programs (id),
    CONSTRAINT fk_enrollment_user FOREIGN KEY (user_id) REFERENCES users (id)
);
