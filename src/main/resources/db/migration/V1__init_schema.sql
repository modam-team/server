CREATE TABLE user (
                      id BIGINT NOT NULL AUTO_INCREMENT,
                      name VARCHAR(255) NOT NULL,
                      providerId VARCHAR(255) NOT NULL UNIQUE,
                      nickname VARCHAR(255) UNIQUE,
                      goalScore INT,
                      preferredCategories VARCHAR(500),
                      isOnboardingCompleted BOOLEAN NOT NULL DEFAULT FALSE,
                      PRIMARY KEY (id)
);

CREATE TABLE book (
                      id BIGINT NOT NULL AUTO_INCREMENT,
                      title VARCHAR(255) NOT NULL,
                      author VARCHAR(255) NOT NULL,
                      publisher VARCHAR(255) NOT NULL,
                      categoryName VARCHAR(255) NOT NULL,
                      cover VARCHAR(255),
                      itemId VARCHAR(255),
                      PRIMARY KEY (id)
);


CREATE TABLE bookcase (
                          id BIGINT NOT NULL AUTO_INCREMENT,
                          user_id BIGINT NOT NULL,
                          book_id BIGINT NOT NULL,
                          status VARCHAR(255),
                          enrollAt DATETIME,
                          startedAt DATETIME,
                          finishedAt DATETIME,
                          PRIMARY KEY (id),
                          CONSTRAINT fk_bookcase_user FOREIGN KEY (user_id) REFERENCES user(id),
                          CONSTRAINT fk_bookcase_book FOREIGN KEY (book_id) REFERENCES book(id)
);


CREATE TABLE review (
                        id BIGINT NOT NULL AUTO_INCREMENT,
                        bookcase_id BIGINT NOT NULL,
                        rating INT,
                        hashtag VARCHAR(255),
                        comment VARCHAR(255),
                        PRIMARY KEY (id),
                        CONSTRAINT fk_review_bookcase FOREIGN KEY (bookcase_id) REFERENCES bookcase(id),
                        CONSTRAINT uk_review_bookcase UNIQUE (bookcase_id)   -- one-to-one
);
