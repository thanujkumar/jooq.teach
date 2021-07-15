package jooq.examples.spring.jdbctemplate;

import jooq.examples.generated.tables.BatchTest;
import jooq.examples.generated.tables.Book;
import jooq.examples.generated.tables.records.BookRecord;
import org.jooq.Record;
import org.jooq.*;
import org.jooq.conf.*;
import org.jooq.impl.DSL;
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
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.IntStream;

import static jooq.examples.generated.Tables.BATCH_TEST;
import static jooq.examples.generated.Tables.BOOK;
import static org.jooq.impl.DSL.*;

//Note TestMe convention is used to capture test name by Oracle end to end metrics
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
    public void bindTestMe() {
        dsl.configuration().settings().withParamType(ParamType.INDEXED);
        Result<Record> result = dsl.select().from(Book.BOOK)
                .where(Book.BOOK.ID.in(IntStream.range(1, 50).boxed().toArray(Integer[]::new)))
                .fetch();
        Assertions.assertEquals(4, result.size());
    }

    //https://www.jooq.org/doc/latest/manual/sql-building/dsl-context/custom-settings/settings-statement-type/
    @Test
    @DisplayName("Statement Type - bind values? - inlined as Statement is used")
    public void statementTypeInlineTestMe() {
        dsl.configuration().settings().withParamType(ParamType.INDEXED).withStatementType(StatementType.STATIC_STATEMENT);
        Result<Record> result = dsl.select().from(Book.BOOK)
                .where(Book.BOOK.ID.in(IntStream.range(1, 50).boxed().toArray(Integer[]::new)))
                .fetch();
        Assertions.assertEquals(4, result.size());

    }

    @Test
    @DisplayName("Statement Type - bind values? - indexed as PreparedStatement is used")
    public void statementTypeIndexedTestMe() {
        dsl.configuration().settings().withParamType(ParamType.INDEXED).withStatementType(StatementType.PREPARED_STATEMENT);
        Result<Record> result = dsl.select().from(Book.BOOK)
                .where(Book.BOOK.ID.in(IntStream.range(1, 50).boxed().toArray(Integer[]::new)))
                .fetch();
        Assertions.assertEquals(4, result.size());
    }

    //https://www.jooq.org/doc/latest/manual/sql-building/dsl-context/custom-settings/settings-inline-threshold/
    @Test
    @DisplayName("Inline Threshold After Which Bind Values Are Used")
    public void inlineTestMe() {
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
    public void jooqLoggingDisabledTestMe() {
        dsl.configuration().settings().withExecuteLogging(false);
        Result<Record> result = dsl.select().from(Book.BOOK)
                .where(Book.BOOK.ID.in(IntStream.range(1, 2).boxed().toArray(Integer[]::new)))
                .fetch();
    }

    @Test
    @DisplayName("Enable Jooq Logging (LoggingListener)")
    public void jooqLoggingEnabledTestMe() {
        dsl.configuration().settings().withExecuteLogging(true);
        Result<Record> result = dsl.select().from(Book.BOOK)
                .where(Book.BOOK.ID.in(IntStream.range(1, 2).boxed().toArray(Integer[]::new)))
                .fetch();
    }

    //https://www.jooq.org/doc/latest/manual/sql-building/dsl-context/custom-settings/settings-listener-invocation-order/
    @Test
    @DisplayName("Invocation Order Of Jooq Listeners")
    public void jooqListenerInovcationOrderTestMe() {
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
    public void explicitTransactionControlTestMe() {
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
    public void usingOptimisticLockingMe() {
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
    public void usingAutoAttachRecordMe() {

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
    public void usingReflectionCachingMe() {

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
    public void withFetchWarningTestMe() throws Exception {
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
    public void identityValueOnStoreTestMe() {

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
    public void jpaAnnotationTestMe() {

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
    public void jdbcFlagsTestMe() {
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
    @DisplayName("Batch size - several queries insert")
    @Tag("performance")
    public void batchSizeServeralQueriesTestMe() {
        dsl.configuration().settings().withBatchSize(20);
        SelectJoinStep<Record1<Integer>> result = dsl.select(max(BATCH_TEST.ID)).from(BATCH_TEST);
        Integer currentMaxId =  Optional.ofNullable(result.fetch().get(0).value1()).orElse(0);

        dsl.transaction(cfg -> {
            dsl.batched(b -> {
                IntStream.range(currentMaxId+1, currentMaxId+1000).forEach(x -> dsl.insertInto(BatchTest.BATCH_TEST)
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
    @DisplayName("Batch size - single query insert")
    @Tag("performance")
    @Order(1)
    public void batchSizeSingleQueryTestMe() {

        dsl.configuration().settings().withBatchSize(5000).withExecuteLogging(true);
        SelectJoinStep<Record1<Integer>> result = dsl.select(max(BATCH_TEST.ID)).from(BATCH_TEST);
        Integer currentMaxId =  Optional.ofNullable(result.fetch().get(0).value1()).orElse(0);


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
            IntStream.range(currentMaxId+1, currentMaxId+5000).forEach(x -> {
                bindStep.bind(x, "Title" + x, BigInteger.valueOf(5 + x), "tk", LocalDateTime.now(), 0L);
            });

            bindStep.execute();
        });
    }


    //https://www.jooq.org/doc/latest/manual/sql-building/dsl-context/custom-settings/settings-in-list-padding/
    //https://blog.jooq.org/2017/05/30/when-to-use-bind-values-and-when-to-use-inline-values-in-sql/
    @Test
    @DisplayName("IN-list Padding Reading Data")
    @Tag("performance")
    @Order(2)
    public void inListPaddingTestMe() {

        //With derive make a copy of configuration and settings
        Configuration config = dsl.configuration().derive();
        Settings settings = config.settings().withInListPadding(false) //default to false
                .withInListPadBase(4) //default is 2
                .withFetchSize(500)
                .withExecuteLogging(false);

//        DSL.using(dsl.configuration()).select().from(BATCH_TEST)
//                .where(BATCH_TEST.ID.in(IntStream.range(1, 20001).boxed().toArray(Integer[]::new)))
//                .fetch();

        Result<Record> result = DSL.using(config).select().from(BATCH_TEST)
                .where(BATCH_TEST.ID.in(IntStream.range(1, 1001).boxed().toArray(Integer[]::new)))
                .fetch();
        //Time to read complete data
        StopWatch watch = watch = new StopWatch();
        for (Record rec : result) {
            rec.getValue(0);
        }
        log.info("Total time to read {}", StopWatch.format(watch.split()));

    }

    @Test
    @DisplayName("IN-list With Array Reading Data")
    @Tag("performance")
    @Order(3)
    public void inListArrayTestMe() {

        //With derive make a copy of configuration and settings
        Configuration config = dsl.configuration().derive();
        Settings settings = config.settings().withInListPadding(false) //default to false
                .withInListPadBase(4) //default is 2
                .withFetchSize(500)
                .withExecuteLogging(false);

        Result<Record> result = DSL.using(config).select().from(BATCH_TEST)
                .where(BATCH_TEST.ID.eq(any(IntStream.range(1, 20001).boxed().toArray(Integer[]::new))))
                .fetch();
        //Time to read complete data
        StopWatch watch = watch = new StopWatch();
        for (Record rec : result) {
            rec.toString();
        }
        log.info("Total time to read {}", StopWatch.format(watch.split()));

    }

    //https://www.jooq.org/doc/latest/manual/sql-building/dsl-context/custom-settings/settings-scalar-subqueries/

    //https://www.jooq.org/doc/latest/manual/sql-building/dsl-context/custom-settings/settings-implicit-join-type/
    @Test
    @DisplayName("Implicit join type")
    public void implicitJoinTestMe() {
        Settings settings = dsl.configuration().settings()
                .withExecuteLogging(false).withRenderImplicitJoinType(RenderImplicitJoinType.INNER_JOIN);

        dsl.select(BOOK.author().FIRST_NAME,
                BOOK.author().LAST_NAME,
                BOOK.TITLE,
                BOOK.language().CD.as("language"))
                .from(BOOK)
                .fetch();
    }
}
