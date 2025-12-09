ALTER TABLE review
DROP
COLUMN hashtag;

CREATE TABLE hashtag
(
    id        BIGINT       NOT NULL AUTO_INCREMENT,
    tag       VARCHAR(255) NOT NULL,
    review_id BIGINT       NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_hashtag_review FOREIGN KEY (review_id) REFERENCES review (id)
);
