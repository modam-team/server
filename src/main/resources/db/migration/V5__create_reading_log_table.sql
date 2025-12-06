CREATE TABLE reading
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,

    bookcase_id   BIGINT NOT NULL,

    read_at       DATETIME,
    reading_place VARCHAR(255),

    CONSTRAINT fk_reading_bookcase
        FOREIGN KEY (bookcase_id)
            REFERENCES bookcase (id)
            ON DELETE CASCADE
);
