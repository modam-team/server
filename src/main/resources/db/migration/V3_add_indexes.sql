ALTER TABLE bookcase
    ADD INDEX idx_bookcase_user_status (user_id, status);

ALTER TABLE book
    ADD FULLTEXT INDEX ft_title (title) WITH PARSER ngram;