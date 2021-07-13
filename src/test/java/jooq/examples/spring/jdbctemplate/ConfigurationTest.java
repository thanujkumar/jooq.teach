package jooq.examples.spring.jdbctemplate;

import jooq.examples.generated.tables.BatchTest;
import jooq.examples.generated.tables.Book;
import jooq.examples.generated.tables.records.BookRecord;
import org.jooq.BatchBindStep;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.conf.*;
import org.jooq.tools.StopWatch;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.suite.api.IncludeTags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.stream.IntStream;

import static jooq.examples.generated.Tables.BATCH_TEST;
import static jooq.examples.generated.Tables.BOOK;

@ExtendWith(SpringExtension.class)
@ContextConfiguration("classpath:jooq-spring-jdbc-template.xml")
@IncludeTags("performance")
public class ConfigurationTest {

    private static final Logger log = LoggerFactory.getLogger(ConfigurationTest.class);

    @Autowired
    DSLContext dsl;

    @Autowired
    ApplicationContext context;

    //https://www.jooq.org/doc/latest/manual/sql-building/dsl-context/custom-settings/settings-parameter-type/
    @Test
    @DisplayName("Indexed For Bind Variables")
    public void bindTest() {
        dsl.configuration().settings().withParamType(ParamType.INDEXED);
        Result<Record> result = dsl.select().from(Book.BOOK)
                .where(Book.BOOK.ID.in(IntStream.range(1, 50).boxed().toArray(Integer[]::new)))
                .fetch();
        Assertions.assertEquals(4, result.size());
    }

    //https://www.jooq.org/doc/latest/manual/sql-building/dsl-context/custom-settings/settings-statement-type/
    @Test
    @DisplayName("Statement Type - bind values? - inlined as Statement is used")
    public void statementTypeInlineTest() {
        dsl.configuration().settings().withParamType(ParamType.INDEXED).withStatementType(StatementType.STATIC_STATEMENT);
        Result<Record> result = dsl.select().from(Book.BOOK)
                .where(Book.BOOK.ID.in(IntStream.range(1, 50).boxed().toArray(Integer[]::new)))
                .fetch();
        Assertions.assertEquals(4, result.size());

    }

    @Test
    @DisplayName("Statement Type - bind values? - indexed as PreparedStatement is used")
    public void statementTypeIndexedTest() {
        dsl.configuration().settings().withParamType(ParamType.INDEXED).withStatementType(StatementType.PREPARED_STATEMENT);
        Result<Record> result = dsl.select().from(Book.BOOK)
                .where(Book.BOOK.ID.in(IntStream.range(1, 50).boxed().toArray(Integer[]::new)))
                .fetch();
        Assertions.assertEquals(4, result.size());
    }

    //https://www.jooq.org/doc/latest/manual/sql-building/dsl-context/custom-settings/settings-inline-threshold/
    @Test
    @DisplayName("Inline Threshold After Which Bind Values Are Used")
    public void inlineTest() {
        dsl.configuration().settings().withInlineThreshold(50);
        //Uses ? as it is withing inline threshold
        Result<Record> result = dsl.select().from(Book.BOOK)
                .where(Book.BOOK.ID.in(IntStream.range(1, 10).boxed().toArray(Integer[]::new)))
                .fetch();

        //Uses inline values as it is above inline threshold
        result = dsl.select().from(Book.BOOK)
                .where(Book.BOOK.ID.in(IntStream.range(1, 55).boxed().toArray(Integer[]::new)))
                .fetch();
        //org.jooq.impl.DefaultRenderContext - Re-render query          : Forcing bind variable inlining as ORACLE20C does not support 0 bind variables (or more) in a single query
        Assertions.assertEquals(4, result.size());
    }

    @Test
    @DisplayName("Disable Jooq Logging (LoggingListener)")
    public void jooqLoggingDisabledTest() {
        dsl.configuration().settings().withExecuteLogging(false);
        Result<Record> result = dsl.select().from(Book.BOOK)
                .where(Book.BOOK.ID.in(IntStream.range(1, 2).boxed().toArray(Integer[]::new)))
                .fetch();
    }

    @Test
    @DisplayName("Enable Jooq Logging (LoggingListener)")
    public void jooqLoggingEnabledTest() {
        dsl.configuration().settings().withExecuteLogging(true);
        Result<Record> result = dsl.select().from(Book.BOOK)
                .where(Book.BOOK.ID.in(IntStream.range(1, 2).boxed().toArray(Integer[]::new)))
                .fetch();
    }

    //https://www.jooq.org/doc/latest/manual/sql-building/dsl-context/custom-settings/settings-listener-invocation-order/
    @Test
    @DisplayName("Invocation Order Of Jooq Listeners")
    public void jooqListenerInovcationOrderTest() {
        dsl.configuration().settings().withExecuteLogging(true)
                .withTransactionListenerStartInvocationOrder(InvocationOrder.DEFAULT)
                .withTransactionListenerEndInvocationOrder(InvocationOrder.REVERSE);

        dsl.transaction(cfg -> {
            dsl.select().from(Book.BOOK)
                    .where(Book.BOOK.ID.in(IntStream.range(1, 2).boxed().toArray(Integer[]::new)))
                    .fetch();

        });
    }

    @Test
    @DisplayName("Using explicit transaction control")
    public void explicitTransactionControlTest() {
        dsl.configuration().settings().withExecuteLogging(true);
        TransactionStatus txStatus = context.getBean("transactionManager", PlatformTransactionManager.class).getTransaction(new DefaultTransactionDefinition());
        try {
            for (int i = 0; i < 2; i++) {
                dsl.insertInto(BOOK)
                        .set(BOOK.ID, 5)
                        .set(BOOK.AUTHOR_ID, 1)
                        .set(BOOK.TITLE, "Book 5")
                        .set(BOOK.PUBLISHED_IN, 2021)
                        .set(BOOK.LANGUAGE_ID, 1)
                        .set(BOOK.CREATED_BY, "owner")
                        .set(BOOK.CREATED_TS, LocalDateTime.now())
                        .set(BOOK.VERSION, 0L)
                        .execute();
            }
        } catch (Exception e) {
            context.getBean("transactionManager", PlatformTransactionManager.class).rollback(txStatus);
            System.err.println("Explicit Transaction Control -> " + e.getMessage());
        }
    }

    //https://www.jooq.org/doc/latest/manual/sql-building/dsl-context/custom-settings/settings-optimistic-locking/
    @Test
    @DisplayName("Using optimistic locking")
    public void usingOptimisticLocking() {
        /* pom.xml has info about version column and timestamp column for code generation
         *  <recordVersionFields>VERSION</recordVersionFields>
         * <recordTimestampFields>MODIFIED_TS</recordTimestampFields>
         */
        //By default this is attached record with current configuration
        dsl.configuration().settings().withExecuteLogging(true)
                .withAttachRecords(true)
                .withUpdateRecordVersion(true)
                .withUpdateRecordTimestamp(true)
                .withExecuteWithOptimisticLocking(true)
                .withExecuteWithOptimisticLockingExcludeUnversioned(false);

        BookRecord book4 = dsl.fetchOne(Book.BOOK, Book.BOOK.ID.eq(4));
        System.out.println(book4);
        book4.setTitle("Thanuj");
        book4.store();
    }

    //https://www.jooq.org/doc/latest/manual/sql-building/dsl-context/custom-settings/settings-attach-records/
    @Test
    @DisplayName("Using Auto-Attached Record (session)")
    public void usingAutoAttachRecord() {

        //By default this is attached record with current configuration
        dsl.configuration().settings().withExecuteLogging(true)
                .withUpdateRecordVersion(true)
                .withUpdateRecordTimestamp(true)
                .withExecuteWithOptimisticLocking(true)
                .withExecuteWithOptimisticLockingExcludeUnversioned(false)
                .withAttachRecords(false);

        Assertions.assertThrows(NullPointerException.class, () -> {
            try {
                BookRecord book4 = dsl.fetchOne(Book.BOOK, Book.BOOK.ID.eq(4));
                System.out.println(book4);
                book4.setTitle("Thanuj");
                book4.store(); //This would fail with NPE as withAttachRecords is set to false
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        });
    }

    //https://www.jooq.org/doc/latest/manual/sql-building/dsl-context/custom-settings/settings-reflection-caching/
    @Test
    @DisplayName("Using Reflection Caching")
    @Tag("performance")
    public void usingReflectionCaching() {

        //By default this is attached record with current configuration
        dsl.configuration().settings().withExecuteLogging(true)
                .withUpdateRecordVersion(true)
                .withUpdateRecordTimestamp(true)
                .withExecuteWithOptimisticLocking(true)
                .withExecuteWithOptimisticLockingExcludeUnversioned(false)
                .withReflectionCaching(false);
        //All operations of the DefaultRecordMapper are cached in the
        //Configuration by default for improved mapping and reflection speed

        BookRecord book4 = dsl.fetchOne(Book.BOOK, Book.BOOK.ID.eq(4));
        System.out.println(book4);
        book4.setTitle("Thanuj");
        book4.store();
    }

    //https://www.jooq.org/doc/latest/manual/sql-building/dsl-context/custom-settings/settings-fetch-warnings/
    @Test
    @DisplayName("Fetch Warning")
    public void withFetchWarningTest() throws Exception {
        dsl.configuration().settings().withExecuteLogging(true)
                .withInlineThreshold(5)
                .withFetchWarnings(true);

        dsl.transaction(cfg -> {
            dsl.select().from(Book.BOOK)
                    .where(Book.BOOK.ID.in(IntStream.range(1, 10).boxed().toArray(Integer[]::new)))
                    .fetch();

        });
    }

    //https://www.jooq.org/doc/latest/manual/sql-building/dsl-context/custom-settings/settings-return-identity-on-store/
    //https://www.jooq.org/doc/latest/manual/sql-building/dsl-context/custom-settings/settings-return-all-on-store/
    @Test
    @DisplayName("Return Identity Value On Store")
    public void identityValueOnStoreTest() {

        //By default this is attached record with current configuration
        dsl.configuration().settings().withExecuteLogging(true)
                .withUpdateRecordVersion(true)
                .withUpdateRecordTimestamp(true)
                .withExecuteWithOptimisticLocking(true)
                .withExecuteWithOptimisticLockingExcludeUnversioned(false)
                .withAttachRecords(true)
                .withReturnIdentityOnUpdatableRecord(false) //Default true
                .withReturnAllOnUpdatableRecord(true); // Defaults to false
        //All operations of the DefaultRecordMapper are cached in the
        //Configuration by default for improved mapping and reflection speed
        dsl.transaction(cfg -> {
            BookRecord book4 = dsl.fetchOne(Book.BOOK, Book.BOOK.ID.eq(4));
            System.out.println(book4);
            book4.setTitle("Thanuj");
            book4.setModifiedBy("tk");
            book4.store();
        });
    }

    //https://www.jooq.org/doc/latest/manual/sql-building/dsl-context/custom-settings/settings-map-jpa/
    @Test
    @DisplayName("Map JPA Annotations")
    @Tag("performance")
    public void jpaAnnotationTest() {

        JdbcTemplateBookService bookService = context.getBean("bookService", JdbcTemplateBookService.class);
        bookService.getDsl().configuration().settings().withExecuteLogging(true);
        bookService.get(4);

        //disabling JPA Annotation lookup will improve performance as JPA is not used
        bookService.getDsl().configuration().settings().withExecuteLogging(true).withMapJPAAnnotations(false);//Defaults to true
        bookService.get(4);
//
//        bookService.getDsl().configuration().settings().withExecuteLogging(true).withMapJPAAnnotations(true);//Defaults to true
//        bookService.get(4);
    }

    //https://www.jooq.org/doc/latest/manual/sql-building/dsl-context/custom-settings/settings-jdbc-flags/
    @Test
    @DisplayName("JDBC Flags")
    @Tag("performance")
    public void jdbcFlagsTest() {
        //These are set at UCP level, if required can be overridden per query
        JdbcTemplateBookService bookService = context.getBean("bookService", JdbcTemplateBookService.class);

        bookService.getDsl().configuration().settings()
                .withExecuteLogging(true)
                .withQueryTimeout(5)
                .withQueryPoolable(QueryPoolable.DEFAULT)
                .withMaxRows(1000)
                .withFetchSize(20);

        bookService.get(4);

    }

    //https://www.jooq.org/doc/latest/manual/sql-building/dsl-context/custom-settings/settings-batch-size/
    @Test
    @DisplayName("Batch size - several queries")
    @Tag("performance")
    public void batchSizeServeralQueriesTest() {
        dsl.configuration().settings().withBatchSize(20);
        dsl.transaction(cfg -> {
            dsl.batched(b -> {
                IntStream.range(20000, 20100).forEach(x -> dsl.insertInto(BatchTest.BATCH_TEST)
                        .set(BATCH_TEST.ID, x)
                        .set(BATCH_TEST.TITLE, "Title" + x)
                        .set(BATCH_TEST.AGE, BigInteger.valueOf(5 + x))
                        .set(BATCH_TEST.CREATED_BY, "tk")
                        .set(BATCH_TEST.CREATED_TS, LocalDateTime.now())
                        .set(BATCH_TEST.VERSION, 0L).execute());
            });
        });
    }

    //https://www.jooq.org/doc/latest/manual/sql-execution/batch-execution/
    @Test
    @DisplayName("Batch size - single query")
    @Tag("performance")
    @Order(1)
    public void batchSizeSingleQueryTest() {

        dsl.configuration().settings().withBatchSize(5000).withExecuteLogging(true);

        dsl.transaction(cfg -> {

//            dsl.batch(dsl.insertInto(BATCH_TEST,
//                    BATCH_TEST.ID, BATCH_TEST.TITLE, BATCH_TEST.AGE,
//                    BATCH_TEST.CREATED_BY, BATCH_TEST.CREATED_TS,
//                    BATCH_TEST.VERSION).values((Integer) null, null, (BigInteger) null, null, null, null))
//                    .bind(1, "Title1", BigInteger.valueOf(5 + 1), "tk", LocalDateTime.now(), 0L)
//                    .bind(2, "Title1", BigInteger.valueOf(5 + 1), "tk", LocalDateTime.now(), 0L)
//                    .bind(3, "Title1", BigInteger.valueOf(5 + 1), "tk", LocalDateTime.now(), 0L)
//                    .bind(4, "Title1", BigInteger.valueOf(5 + 1), "tk", LocalDateTime.now(), 0L)
//                    .bind(5, "Title1", BigInteger.valueOf(5 + 1), "tk", LocalDateTime.now(), 0L)
//                    .bind(6, "Title1", BigInteger.valueOf(5 + 1), "tk", LocalDateTime.now(), 0L)
//                    .bind(7, "Title1", BigInteger.valueOf(5 + 1), "tk", LocalDateTime.now(), 0L)
//                    .bind(8, "Title1", BigInteger.valueOf(5 + 1), "tk", LocalDateTime.now(), 0L)
//                    .bind(9, "Title1", BigInteger.valueOf(5 + 1), "tk", LocalDateTime.now(), 0L)
//                    .bind(10, "Title1", BigInteger.valueOf(5 + 1), "tk", LocalDateTime.now(), 0L)
//                    .bind(11, "Title1", BigInteger.valueOf(5 + 1), "tk", LocalDateTime.now(), 0L)
//                    .bind(12, "Title1", BigInteger.valueOf(5 + 1), "tk", LocalDateTime.now(), 0L)
//                    .bind(13, "Title1", BigInteger.valueOf(5 + 1), "tk", LocalDateTime.now(), 0L)
//                    .bind(14, "Title1", BigInteger.valueOf(5 + 1), "tk", LocalDateTime.now(), 0L)
//                    .bind(15, "Title1", BigInteger.valueOf(5 + 1), "tk", LocalDateTime.now(), 0L)
//                    .bind(16, "Title1", BigInteger.valueOf(5 + 1), "tk", LocalDateTime.now(), 0L)
//                    .bind(17, "Title1", BigInteger.valueOf(5 + 1), "tk", LocalDateTime.now(), 0L)
//                    .bind(18, "Title1", BigInteger.valueOf(5 + 1), "tk", LocalDateTime.now(), 0L)
//                    .bind(19, "Title1", BigInteger.valueOf(5 + 1), "tk", LocalDateTime.now(), 0L)
//                    .bind(20, "Title1", BigInteger.valueOf(5 + 1), "tk", LocalDateTime.now(), 0L)
//                    .bind(21, "Title1", BigInteger.valueOf(5 + 1), "tk", LocalDateTime.now(), 0L).execute();

            BatchBindStep bindStep = dsl.batch(dsl.insertInto(BATCH_TEST,
                    BATCH_TEST.ID, BATCH_TEST.TITLE, BATCH_TEST.AGE,
                    BATCH_TEST.CREATED_BY, BATCH_TEST.CREATED_TS,
                    BATCH_TEST.VERSION).values((Integer) null, null, (BigInteger) null, null, null, null));
            IntStream.range(1, 5001).forEach(x -> {
                bindStep.bind(x, "Title" + x, BigInteger.valueOf(5 + x), "tk", LocalDateTime.now(), 0L);
            });

            bindStep.execute();
        });
    }


    //https://www.jooq.org/doc/latest/manual/sql-building/dsl-context/custom-settings/settings-in-list-padding/
    @Test
    @DisplayName("IN-list Padding")
    @Tag("performance")
    @Order(2)
    public void inListPaddingTest() {

        Settings settings = dsl.configuration().settings().withInListPadding(false) //default to false
                .withInListPadBase(4) //default is 2
                .withFetchSize(500)
                .withExecuteLogging(false);

//        DSL.using(dsl.configuration()).select().from(BATCH_TEST)
//                .where(BATCH_TEST.ID.in(IntStream.range(1, 20001).boxed().toArray(Integer[]::new)))
//                .fetch();

        Result<Record> result = dsl.select().from(BATCH_TEST)
                .where(BATCH_TEST.ID.in(IntStream.range(1, 20001).boxed().toArray(Integer[]::new)))
                .fetch();
        //Time to read complete data
        StopWatch watch = watch = new StopWatch();
        for (Record rec : result) {
            rec.toString();
        }
        log.info("Total time to read {}", StopWatch.format(watch.split()));

    }
}
