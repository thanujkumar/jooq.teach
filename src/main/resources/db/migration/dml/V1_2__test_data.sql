-- language
INSERT INTO language (id, cd, description, created_by, created_ts, version)
VALUES (1, 'en', 'English', 'owner', current_timestamp, 0);

INSERT INTO language (id, cd, description, created_by, created_ts, version)
VALUES (2, 'de', 'Deutsch', 'owner', current_timestamp, 0);

INSERT INTO language (id, cd, description, created_by, created_ts, version)
VALUES (3, 'fr', 'Français', 'owner', current_timestamp, 0);

INSERT INTO language (id, cd, description, created_by, created_ts, version)
VALUES (4, 'pt', 'Português', 'owner', current_timestamp, 0);

-- Author
INSERT INTO author (id, first_name, last_name, date_of_birth, year_of_birth, created_by, created_ts, version)
VALUES (1, 'George', 'Orwell', DATE '1903-06-26', 1903, 'owner', current_timestamp, 0);

INSERT INTO author (id, first_name, last_name, date_of_birth, year_of_birth, created_by, created_ts, version)
VALUES (2, 'Paulo', 'Coelho', DATE '1947-08-24', 1947, 'owner', current_timestamp, 0);

-- book
INSERT INTO book (id, author_id, title, published_in, language_id, created_by, created_ts, version)
VALUES (1, 1, '1984', 1948, 1, 'owner', current_timestamp, 0);

INSERT INTO book (id, author_id, title, published_in, language_id, created_by, created_ts, version)
VALUES (2, 1, 'Animal Farm', 1945, 1, 'owner', current_timestamp, 0);

INSERT INTO book (id, author_id, title, published_in, language_id, created_by, created_ts, version)
VALUES (3, 2, 'O Alquimista', 1988, 4, 'owner', current_timestamp, 0);

INSERT INTO book (id, author_id, title, published_in, language_id, created_by, created_ts, version)
VALUES (4, 2, 'Brida', 1990, 2, 'owner', current_timestamp, 0);

-- book_store
INSERT INTO book_store (name, created_by, created_ts, version)
VALUES ('Orell Füssli', 'owner', current_timestamp, 0);

INSERT INTO book_store (name, created_by, created_ts, version)
VALUES ('Ex Libris', 'owner', current_timestamp, 0);

INSERT INTO book_store (name, created_by, created_ts, version)
VALUES ('Buchhandlung im Volkshaus', 'owner', current_timestamp, 0);

-- book_to_book_store
INSERT INTO book_to_book_store (name, book_id, stock, created_by, created_ts, version)
VALUES ('Orell Füssli', 1, 10, 'owner', current_timestamp, 0);

INSERT INTO book_to_book_store (name, book_id, stock, created_by, created_ts, version)
VALUES ('Orell Füssli', 2, 10, 'owner', current_timestamp, 0);

INSERT INTO book_to_book_store (name, book_id, stock, created_by, created_ts, version)
VALUES ('Orell Füssli', 3, 10, 'owner', current_timestamp, 0);

INSERT INTO book_to_book_store (name, book_id, stock, created_by, created_ts, version)
VALUES ('Ex Libris', 1, 1, 'owner', current_timestamp, 0);

INSERT INTO book_to_book_store (name, book_id, stock, created_by, created_ts, version)
VALUES ('Ex Libris', 3, 2, 'owner', current_timestamp, 0);

INSERT INTO book_to_book_store (name, book_id, stock, created_by, created_ts, version)
VALUES ('Buchhandlung im Volkshaus', 3, 1, 'owner', current_timestamp, 0);