ALTER TABLE book
    ADD COLUMN link VARCHAR(255);

UPDATE book
SET link = 'https://www.aladin.co.kr/home/welcome.aspx'
WHERE link IS NULL;