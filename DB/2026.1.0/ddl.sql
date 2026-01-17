CREATE TABLE users
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    version     BIGINT       NOT NULL DEFAULT 0,
    email       VARCHAR(255) NOT NULL UNIQUE,
    name        VARCHAR(255),
    picture     VARCHAR(500),
    provider    VARCHAR(50)  NOT NULL,
    provider_id VARCHAR(255) NOT NULL,
    created_at  TIMESTAMP             DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP             DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_login  TIMESTAMP,
    UNIQUE KEY uk_provider_id (provider, provider_id)
);

CREATE TABLE user_roles
(
    user_id BIGINT      NOT NULL,
    role    VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, role),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE incidents
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    version     BIGINT        NOT NULL DEFAULT 0,
    title       VARCHAR(200)  NOT NULL,
    description VARCHAR(2000) NOT NULL,
    status      VARCHAR(50)   NOT NULL,
    division    VARCHAR(50),
    district    VARCHAR(50),
    upazila     VARCHAR(50),
    lat DOUBLE,
    lng DOUBLE,
    user_id     BIGINT        NOT NULL,
    created_at  TIMESTAMP              DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP              DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    INDEX       idx_status (status),
    INDEX       idx_user_id (user_id),
    INDEX       idx_created_at (created_at)
);

CREATE TABLE incident_images
(
    incident_id BIGINT        NOT NULL,
    image_url   VARCHAR(1000) NOT NULL,
    FOREIGN KEY (incident_id) REFERENCES incidents (id) ON DELETE CASCADE,
    INDEX       idx_incident_id (incident_id)
);