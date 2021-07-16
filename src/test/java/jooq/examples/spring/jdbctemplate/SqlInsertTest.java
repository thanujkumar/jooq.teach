package jooq.examples.spring.jdbctemplate;

import jooq.examples.generated.tables.InsertTest;
import jooq.examples.generated.tables.records.InsertTestRecord;
import org.jooq.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.IntStream;

import static jooq.examples.generated.Tables.AUTHOR;
import static jooq.examples.generated.Tables.INSERT_TEST;
import static org.jooq.impl.DSL.begin;
import static org.jooq.impl.DSL.max;

////Note TestMe convention is used to capture test name by Oracle end to end metrics
@ExtendWith(SpringExtension.class)
@ContextConfiguration("classpath:jooq-spring-jdbc-template.xml")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SqlInsertTest {
    private static final Logger log = LoggerFactory.getLogger(SqlInsertTest.class);

    @Autowired
    DSLContext dsl;

    @Autowired
    ApplicationContext context;

    //https://www.jooq.org/doc/latest/manual/sql-building/sql-statements/insert-statement/insert-values/
    @Test
    @DisplayName("basic insert statements")
    @Order(1)
    public void simpleInsertTestMe() {

        SelectJoinStep<Record1<Integer>> result = dsl.select(max(INSERT_TEST.ID)).from(INSERT_TEST);
        Integer currentMaxId = Optional.ofNullable(result.fetch().get(0).value1()).orElse(0);


        dsl.transaction(cfx -> {
            dsl.insertInto(InsertTest.INSERT_TEST,
                    INSERT_TEST.ID, INSERT_TEST.NAME, INSERT_TEST.AGE, INSERT_TEST.TITLE, INSERT_TEST.CREATED_TS, INSERT_TEST.VERSION)
                    .values(currentMaxId + 1, "TK", BigInteger.valueOf(10), "STD", LocalDateTime.now(), 0L)
                    .execute();
        });

        dsl.transaction(cfx -> {
            InsertValuesStep6<InsertTestRecord, Integer, String, BigInteger, String, LocalDateTime, Long> step =
                    dsl.insertInto(INSERT_TEST, INSERT_TEST.ID, INSERT_TEST.NAME, INSERT_TEST.AGE, INSERT_TEST.TITLE, INSERT_TEST.CREATED_TS, INSERT_TEST.VERSION);
            step.values(currentMaxId + 2, "TK", BigInteger.valueOf(10), "STD", LocalDateTime.now(), 0L).execute();
        });


        dsl.transaction(cfx -> {
            InsertTestRecord insertTestRecord = new InsertTestRecord();
            insertTestRecord.setId(currentMaxId + 3);
            insertTestRecord.setAge(BigInteger.valueOf(10));
            insertTestRecord.setTitle("STDNT");
            insertTestRecord.setName("TK2").setCreatedTs(LocalDateTime.now()).setVersion(0L);

            dsl.insertInto(INSERT_TEST).set(insertTestRecord).execute();
        });
    }


    //https://www.jooq.org/doc/latest/manual/sql-building/sql-statements/insert-statement/insert-values/
    @Test
    @DisplayName("multi-record insert statements with values")
    @Order(2)
    public void multiInsertTestMe() {
        dsl.configuration().settings().withBatchSize(500); //for insert

        SelectJoinStep<Record1<Integer>> result = dsl.select(max(INSERT_TEST.ID)).from(INSERT_TEST);
        Integer currentMaxId = Optional.ofNullable(result.fetch().get(0).value1()).orElse(0);


        InsertValuesStep6 insertValuesStep = dsl.insertInto(InsertTest.INSERT_TEST, INSERT_TEST.ID, INSERT_TEST.NAME,
                INSERT_TEST.AGE, INSERT_TEST.TITLE, INSERT_TEST.CREATED_TS, INSERT_TEST.VERSION);
        IntStream.range(currentMaxId + 1, currentMaxId + 1500).forEach(x -> {
            insertValuesStep.values(x, "Name" + x, 5 + x, "Title" + x, LocalDateTime.now(), 0L);
        });

        dsl.transaction(cfg -> {
            insertValuesStep.execute();
        });
    }


    @Test
    @DisplayName("multi-record insert statements with bind values using batch")
    @Order(3)
    public void multiInsertWithBindBatchTestMe() {

        dsl.configuration().settings().withBatchSize(500); //for insert
        SelectJoinStep<Record1<Integer>> result = dsl.select(max(INSERT_TEST.ID)).from(INSERT_TEST);
        Integer currentMaxId = Optional.ofNullable(result.fetch().get(0).value1()).orElse(0);

        dsl.transaction(cfg -> {
            BatchBindStep bindStep = dsl.batch(dsl.insertInto(InsertTest.INSERT_TEST, INSERT_TEST.ID, INSERT_TEST.NAME,
                    INSERT_TEST.AGE, INSERT_TEST.TITLE, INSERT_TEST.CREATED_TS, INSERT_TEST.VERSION)
                    .values((Integer) null, null, (BigInteger) null, null, null, null));

            IntStream.range(currentMaxId + 1, currentMaxId + 3000).forEach(x -> {
                bindStep.bind(x, "Name" + x, 5 + x, "Title" + x, LocalDateTime.now(), 0L);
            });

            bindStep.execute();
        });
    }


    @Test
    @DisplayName("insert return keys")
    @Order(4)
    public void insertReturningTestMe() {

        dsl.configuration().settings().withBatchSize(500); //for insert
        SelectJoinStep<Record1<Integer>> resultMax = dsl.select(max(INSERT_TEST.ID)).from(INSERT_TEST);
        Integer currentMaxId = Optional.ofNullable(resultMax.fetch().get(0).value1()).orElse(0);

        InsertValuesStep6 step6 = dsl.insertInto(InsertTest.INSERT_TEST, INSERT_TEST.ID, INSERT_TEST.NAME,
                INSERT_TEST.AGE, INSERT_TEST.TITLE, INSERT_TEST.CREATED_TS, INSERT_TEST.VERSION);

        IntStream.range(currentMaxId + 1, currentMaxId + 3).forEach(x -> {
            step6.values(x, "Name" + x, 5 + x, "Title" + x, LocalDateTime.now(), 0L);
        });

        dsl.transaction(cfx -> {
            Result result = step6.returningResult(INSERT_TEST.ID).fetch();
            log.info(result.toString());
        });
    }

    //https://www.jooq.org/doc/latest/manual/sql-building/sql-statements/update-statement/
    //https://www.jooq.org/doc/latest/manual/sql-building/sql-statements/merge-statement/
    @Test
    @DisplayName("merge statement")
    @Order(5)
    public void mergeTestMe() {
        dsl.configuration().settings().withExecuteWithOptimisticLocking(true);
        dsl.transaction(cfx -> {
            dsl.mergeInto(AUTHOR).using(dsl.selectOne())
                    .on(AUTHOR.LAST_NAME.eq("Orwell"))
                    .whenMatchedThenUpdate()
                    .set(AUTHOR.FIRST_NAME, "Thanuj")
                    .whenNotMatchedThenInsert(AUTHOR.ID, AUTHOR.FIRST_NAME, AUTHOR.LAST_NAME, AUTHOR.DATE_OF_BIRTH, AUTHOR.CREATED_BY, AUTHOR.CREATED_TS, AUTHOR.VERSION)
                    .values(3, "THANUJ", "KUMAR", LocalDate.of(1977, 03, 26), "owner", LocalDateTime.now(), 0L)
                    .execute();
        });
    }

    @Test
    @DisplayName("Client Side Block Insert Statements")
    @Order(6)
    public void blockInsertTestMe() {
        dsl.configuration().settings().withExecuteWithOptimisticLocking(true).withExecuteLogging(true);

        SelectJoinStep<Record1<Integer>> resultMax = dsl.select(max(INSERT_TEST.ID)).from(INSERT_TEST);
        Integer currentMaxId = Optional.ofNullable(resultMax.fetch().get(0).value1()).orElse(0);
        int val = currentMaxId + 1;

        //https://www.jooq.org/doc/latest/manual/sql-building/procedural-statements/procedural-block/
        dsl.transaction(cfx -> {
            dsl.begin(
                    dsl.insertInto(INSERT_TEST, INSERT_TEST.ID, INSERT_TEST.AGE, INSERT_TEST.TITLE, INSERT_TEST.NAME, INSERT_TEST.CREATED_TS, INSERT_TEST.VERSION)
                            .values(val, BigInteger.valueOf(val + 5), "Title" + val, "Name" + val, LocalDateTime.now(), 0L),

                    dsl.insertInto(INSERT_TEST, INSERT_TEST.ID, INSERT_TEST.AGE, INSERT_TEST.TITLE, INSERT_TEST.NAME, INSERT_TEST.CREATED_TS, INSERT_TEST.VERSION)
                            .values(val + 1, BigInteger.valueOf(val + (1 + 5)), "Title" + (val + 1), "Name" + (val + 1), LocalDateTime.now(), 0L)

            ).execute();
        });

        int va = val + 2;

        dsl.transaction(cfx -> {
            dsl.begin(
                    begin(
                            dsl.insertInto(INSERT_TEST, INSERT_TEST.ID, INSERT_TEST.AGE, INSERT_TEST.TITLE, INSERT_TEST.NAME, INSERT_TEST.CREATED_TS, INSERT_TEST.VERSION)
                                    .values(va, BigInteger.valueOf(va + 5), "Title" + va, "Name" + (va), LocalDateTime.now(), 0L)
                    ),
                    begin(
                            dsl.insertInto(INSERT_TEST, INSERT_TEST.ID, INSERT_TEST.AGE, INSERT_TEST.TITLE, INSERT_TEST.NAME, INSERT_TEST.CREATED_TS, INSERT_TEST.VERSION)
                                    .values(va + 1, BigInteger.valueOf(va + (1 + 5)), "Title" + (va + 1), "Name" + (va + 1), LocalDateTime.now(), 0L)
                    )
            ).execute();
        });
    }
}
