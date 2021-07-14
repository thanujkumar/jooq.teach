CREATE USER jooqdata identified BY jooqdata;

GRANT create session, create table, create view, create sequence, create procedure, create type, create trigger, create synonym, select catalog to jooqdata;

GRANT UNLIMITED TABLESPACE TO jooqdata;

-- https://docs.oracle.com/database/121/ARPLS/d_xplan.htm#ARPLS378
-- http://www.dba-oracle.com/t_dbms_xplan.htm

GRANT SELECT,READ ON V_$SESSION TO jooqdata;
GRANT SELECT,READ ON V_$SQL_PLAN TO jooqdata;
GRANT SELECT,READ ON V_$SQL_PLAN_STATISTICS_ALL  TO jooqdata;
GRANT SELECT,READ ON DBA_HIST_SQL_PLAN TO jooqdata;
GRANT SELECT,READ ON DBA_HIST_SQLTEXT TO jooqdata;
GRANT SELECT,READ ON V_$DATABASE TO jooqdata;
GRANT SELECT,READ ON ALL_SQLSET_STATEMENTS TO jooqdata;
GRANT SELECT,READ ON ALL_SQLSET_PLANS TO jooqdata;
GRANT SELECT,READ ON DBA_SQL_PLAN_BASELINES  TO jooqdata;