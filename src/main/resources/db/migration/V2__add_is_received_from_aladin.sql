ALTER TABLE book
    ADD COLUMN is_received_from_aladin BOOLEAN;

UPDATE book
SET is_received_from_aladin = TRUE;
