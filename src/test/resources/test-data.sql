DELETE FROM books;
ALTER TABLE books ALTER COLUMN id RESTART WITH 1;

INSERT INTO books (title, author, publication_year) VALUES
('Effective Java', 'Joshua Bloch', 2018),
('Clean Code', 'Robert Martin', 2008);