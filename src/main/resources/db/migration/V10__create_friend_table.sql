CREATE TABLE friend (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    request_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,

    status VARCHAR(255) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6),

    CONSTRAINT fk_friend_requester
        FOREIGN KEY (request_id)
        REFERENCES user (id),

    CONSTRAINT fk_friend_receiver
        FOREIGN KEY (receiver_id)
        REFERENCES user (id)
);