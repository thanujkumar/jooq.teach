CREATE USER jooqdata identified BY jooqdata;

GRANT create session, create table, create view, create sequence, create procedure, create type, create trigger, create synonym to jooqdata;

GRANT UNLIMITED TABLESPACE TO jooqdata;