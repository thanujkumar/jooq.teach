CREATE TABLE language
(
    id          NUMBER(7)         NOT NULL PRIMARY KEY,
    cd          CHAR(2)           NOT NULL,
    description VARCHAR2(50),
    created_by  VARCHAR2(16 CHAR) NOT NULL,
    created_ts  TIMESTAMP(6)      NOT NULL,
    modified_by VARCHAR2(16 CHAR),
    modified_ts TIMESTAMP(6),
    version     NUMBER(18)        NOT NULL

);

CREATE TABLE author
(
    id            NUMBER(7)         NOT NULL PRIMARY KEY,
    first_name    VARCHAR2(50),
    last_name     VARCHAR2(50)      NOT NULL,
    date_of_birth DATE,
    year_of_birth NUMBER(7),
    distinguished NUMBER(1),
    created_by    VARCHAR2(16 CHAR) NOT NULL,
    created_ts    TIMESTAMP(6)      NOT NULL,
    modified_by   VARCHAR2(16 CHAR),
    modified_ts   TIMESTAMP(6),
    version       NUMBER(18)        NOT NULL
);

CREATE TABLE book
(
    id           NUMBER(7)         NOT NULL PRIMARY KEY,
    author_id    NUMBER(7)         NOT NULL,
    title        VARCHAR2(400)     NOT NULL,
    published_in NUMBER(7)         NOT NULL,
    language_id  NUMBER(7)         NOT NULL,
    created_by   VARCHAR2(16 CHAR) NOT NULL,
    created_ts   TIMESTAMP(6)      NOT NULL,
    modified_by  VARCHAR2(16 CHAR),
    modified_ts  TIMESTAMP(6),
    version      NUMBER(18)        NOT NULL,

    CONSTRAINT fk_book_author FOREIGN KEY (author_id) REFERENCES author (id),
    CONSTRAINT fk_book_language FOREIGN KEY (language_id) REFERENCES language (id)
);

CREATE TABLE book_store
(
    name        VARCHAR2(400)     NOT NULL UNIQUE,
    created_by  VARCHAR2(16 CHAR) NOT NULL,
    created_ts  TIMESTAMP(6)      NOT NULL,
    modified_by VARCHAR2(16 CHAR),
    modified_ts TIMESTAMP(6),
    version     NUMBER(18)        NOT NULL
);

CREATE TABLE book_to_book_store
(
    name        VARCHAR2(400)     NOT NULL,
    book_id     INTEGER           NOT NULL,
    stock       INTEGER,
    created_by  VARCHAR2(16 CHAR) NOT NULL,
    created_ts  TIMESTAMP(6)      NOT NULL,
    modified_by VARCHAR2(16 CHAR),
    modified_ts TIMESTAMP(6),
    version     NUMBER(18)        NOT NULL,

    PRIMARY KEY (name, book_id),
    CONSTRAINT fk_b2bs_book_store FOREIGN KEY (name) REFERENCES book_store (name) ON DELETE CASCADE,
    CONSTRAINT fk_b2bs_book FOREIGN KEY (book_id) REFERENCES book (id) ON DELETE CASCADE
);