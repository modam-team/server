ALTER TABLE book
    ADD FULLTEXT INDEX idx_book_title_fts (title)
  WITH PARSER ngram;
