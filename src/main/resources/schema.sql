-- Ddareungi schema (alphabetical table order)

CREATE TABLE IF NOT EXISTS board (
    board_id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id             BIGINT      NOT NULL,
    board_type          VARCHAR(30) NOT NULL,
    title               VARCHAR(200) NOT NULL,
    content             TEXT        NOT NULL,
    created_date        DATETIME    DEFAULT CURRENT_TIMESTAMP,
    last_modified_date  DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS comment (
    comment_id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    board_id            BIGINT      NOT NULL,
    user_id             BIGINT      NOT NULL,
    content             TEXT        NOT NULL,
    created_date        DATETIME    DEFAULT CURRENT_TIMESTAMP,
    last_modified_date  DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS pass (
    pass_id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    pass_type           VARCHAR(30) NOT NULL,
    price               INT         NOT NULL,
    created_date        DATETIME    DEFAULT CURRENT_TIMESTAMP,
    last_modified_date  DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS rental (
    rental_id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    bike_number         VARCHAR(50) NOT NULL,
    user_id             BIGINT      NOT NULL,
    start_station_id    BIGINT      NULL,
    end_station_id      BIGINT      NULL,
    start_time          DATETIME    NOT NULL,
    end_time            DATETIME    NULL,
    created_date        DATETIME    DEFAULT CURRENT_TIMESTAMP,
    last_modified_date  DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS station (
    station_id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    station_name        VARCHAR(150) NOT NULL,
    latitude            DECIMAL(10,7) NOT NULL,
    longitude           DECIMAL(10,7) NOT NULL,
    address             VARCHAR(255) NOT NULL,
    capacity            INT          NOT NULL,
    installation_date   DATE         NOT NULL,
    closed_date         TIME         NULL,
    created_by_id       BIGINT       NULL,
    modified_by_id      BIGINT       NULL,
    created_date        DATETIME     DEFAULT CURRENT_TIMESTAMP,
    last_modified_date  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS usage_stats (
    usage_stats_id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    station_id          BIGINT NOT NULL,
    rental_id           BIGINT NOT NULL,
    created_date        DATETIME DEFAULT CURRENT_TIMESTAMP,
    last_modified_date  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS user_pass (
    user_pass_id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id             BIGINT      NOT NULL,
    pass_id             BIGINT      NOT NULL,
    activated_date      DATE        NULL,
    expired_date        DATE        NULL,
    user_pass_status    VARCHAR(30) NOT NULL,
    created_date        DATETIME    DEFAULT CURRENT_TIMESTAMP,
    last_modified_date  DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS users (
    user_id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    username            VARCHAR(100) NOT NULL UNIQUE,
    name                VARCHAR(100) NOT NULL,
    email               VARCHAR(255) NOT NULL UNIQUE,
    password            VARCHAR(255) NOT NULL,
    created_date        DATETIME     DEFAULT CURRENT_TIMESTAMP,
    last_modified_date  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Foreign key constraints
ALTER TABLE board
    ADD CONSTRAINT fk_board_user
        FOREIGN KEY (user_id) REFERENCES users (user_id);

ALTER TABLE comment
    ADD CONSTRAINT fk_comment_board
        FOREIGN KEY (board_id) REFERENCES board (board_id),
    ADD CONSTRAINT fk_comment_user
        FOREIGN KEY (user_id) REFERENCES users (user_id);

ALTER TABLE rental
    ADD CONSTRAINT fk_rental_user
        FOREIGN KEY (user_id) REFERENCES users (user_id),
    ADD CONSTRAINT fk_rental_start_station
        FOREIGN KEY (start_station_id) REFERENCES station (station_id),
    ADD CONSTRAINT fk_rental_end_station
        FOREIGN KEY (end_station_id) REFERENCES station (station_id);

ALTER TABLE station
    ADD CONSTRAINT fk_station_created_by
        FOREIGN KEY (created_by_id) REFERENCES users (user_id),
    ADD CONSTRAINT fk_station_modified_by
        FOREIGN KEY (modified_by_id) REFERENCES users (user_id);

ALTER TABLE usage_stats
    ADD CONSTRAINT fk_usage_stats_station
        FOREIGN KEY (station_id) REFERENCES station (station_id),
    ADD CONSTRAINT fk_usage_stats_rental
        FOREIGN KEY (rental_id) REFERENCES rental (rental_id);

ALTER TABLE user_pass
    ADD CONSTRAINT fk_user_pass_user
        FOREIGN KEY (user_id) REFERENCES users (user_id),
    ADD CONSTRAINT fk_user_pass_pass
        FOREIGN KEY (pass_id) REFERENCES pass (pass_id);

