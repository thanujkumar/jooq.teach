package jooq.examples.spring.jdbctemplate;

import jooq.examples.generated.tables.InsertTest;
import jooq.examples.generated.tables.records.InsertTestRecord;
import org.jooq.BatchBindStep;
import org.jooq.DSLContext;
import org.jooq.InsertValuesStep6;
import org.jooq.Result;
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
import java.util.stream.IntStream;

import static jooq.examples.generated.Tables.AUTHOR;
import static jooq.examples.generated.Tables.INSERT_TEST;
import static org.jooq.impl.DSL.localDate;

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
    public void simpleInsertTest() {
        dsl.transaction(cfx -> {
            dsl.insertInto(InsertTest.INSERT_TEST,
                    INSERT_TEST.ID, INSERT_TEST.NAME, INSERT_TEST.AGE, INSERT_TEST.TITLE, INSERT_TEST.CREATED_TS, INSERT_TEST.VERSION)
                    .values(1, "TK", BigInteger.valueOf(10), "STD", LocalDateTime.now(), 0L)
                    .execute();
        });

        dsl.transaction(cfx -> {
            InsertValuesStep6<InsertTestRecord, Integer, String, BigInteger, String, LocalDateTime, Long> step =
                    dsl.insertInto(INSERT_TEST, INSERT_TEST.ID, INSERT_TEST.NAME, INSERT_TEST.AGE, INSERT_TEST.TITLE, INSERT_TEST.CREATED_TS, INSERT_TEST.VERSION);
            step.values(2, "TK", BigInteger.valueOf(10), "STD", LocalDateTime.now(), 0L).execute();
        });


        dsl.transaction(cfx -> {
            InsertTestRecord insertTestRecord = new InsertTestRecord();
            insertTestRecord.setId(3);
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
    public void multiInsertTest() {
        dsl.configuration().settings().withBatchSize(500); //for insert

        InsertValuesStep6 insertValuesStep = dsl.insertInto(InsertTest.INSERT_TEST, INSERT_TEST.ID, INSERT_TEST.NAME,
                INSERT_TEST.AGE, INSERT_TEST.TITLE, INSERT_TEST.CREATED_TS, INSERT_TEST.VERSION);
        IntStream.range(4, 1500).forEach(x -> {
            insertValuesStep.values(x, "Name" + x, 5 + x, "Title" + x, LocalDateTime.now(), 0L);
        });

        dsl.transaction(cfg -> {
            insertValuesStep.execute();
        });
    }


    @Test
    @DisplayName("multi-record insert statements with bind values using batch")
    @Order(3)
    public void multiInsertWithBindBatchTest() {

        dsl.configuration().settings().withBatchSize(500); //for insert
        dsl.transaction(cfg -> {
            BatchBindStep bindStep = dsl.batch(dsl.insertInto(InsertTest.INSERT_TEST, INSERT_TEST.ID, INSERT_TEST.NAME,
                    INSERT_TEST.AGE, INSERT_TEST.TITLE, INSERT_TEST.CREATED_TS, INSERT_TEST.VERSION)
                    .values((Integer) null, null, (BigInteger) null, null, null, null));

            IntStream.range(1501, 3000).forEach(x -> {
                bindStep.bind(x, "Name" + x, 5 + x, "Title" + x, LocalDateTime.now(), 0L);
            });

            bindStep.execute();
        });
    }


    @Test
    @DisplayName("insert return keys")
    @Order(4)
    public void insertReturningTest() {

        dsl.configuration().settings().withBatchSize(500); //for insert
        InsertValuesStep6 step6 = dsl.insertInto(InsertTest.INSERT_TEST, INSERT_TEST.ID, INSERT_TEST.NAME,
                INSERT_TEST.AGE, INSERT_TEST.TITLE, INSERT_TEST.CREATED_TS, INSERT_TEST.VERSION);

        IntStream.range(3001, 3003).forEach(x -> {
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
    public void mergeTest() {
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
}
