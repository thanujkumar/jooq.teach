--- Below is for batch queries for Jooq
CREATE TABLE batch_test
(
    id           NUMBER(7)         NOT NULL PRIMARY KEY,
    title        VARCHAR2(400)     NOT NULL,
    age          INTEGER          NOT NULL,
    created_by   VARCHAR2(16 CHAR) NOT NULL,
    created_ts   TIMESTAMP(6)      NOT NULL,
    modified_by  VARCHAR2(16 CHAR),
    modified_ts  TIMESTAMP(6),
    version      NUMBER(18)        NOT NULL
);

CREATE TABLE insert_test
(
    id           NUMBER(7)         NOT NULL PRIMARY KEY,
    title        VARCHAR2(400)     NOT NULL,
    name         VARCHAR2(400)     NOT NULL,
    age          INTEGER          NOT NULL,
    created_by   VARCHAR2(16 CHAR) DEFAULT 'UNKNOWN' NOT NULL ,
    created_ts   TIMESTAMP(6)      NOT NULL,
    modified_by  VARCHAR2(16 CHAR),
    modified_ts  TIMESTAMP(6),
    version      NUMBER(18)        NOT NULL
);