package jooq.examples.spring.jdbctemplate;

import jooq.examples.generated.tables.Author;
import jooq.examples.generated.tables.Book;
import jooq.examples.generated.tables.BookStore;
import jooq.examples.generated.tables.BookToBookStore;
import oracle.ucp.admin.UniversalConnectionPoolManager;
import org.jooq.DSLContext;
import org.jooq.Record3;
import org.jooq.Result;
import org.jooq.ResultQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

import static jooq.examples.generated.Tables.*;
import static org.jooq.impl.DSL.countDistinct;

//https://github.com/jOOQ/jOOQ/blob/main/jOOQ-examples/jOOQ-spring-example/src/test/java/org/jooq/example/TransactionTest.java

//Using DSLContext for generating typesafe queries and using Spring jdbc template to execute sql
//and using create and get of JdbcTemplateBookService to directly use DSLContext
public class JdbcTemplateMain {

    private static final Logger log = LoggerFactory.getLogger(JdbcTemplateMain.class);

    private static ApplicationContext context = null;


    public static void main(String[] args) throws Exception {
        context = new ClassPathXmlApplicationContext("jooq-spring-jdbc-template.xml");
        ((ClassPathXmlApplicationContext)context).registerShutdownHook();
        UniversalConnectionPoolManager poolManager = context.getBean("ucpm", UniversalConnectionPoolManager.class);
        DSLContext dslContext = context.getBean("dslContext", DSLContext.class);
        JdbcTemplate jdbcTemplate = context.getBean("jdbcTemplate", JdbcTemplate.class);

        Book b = BOOK.as("b");
        Author a = AUTHOR.as("a");
        BookStore s = BOOK_STORE.as("s");
        BookToBookStore t = BOOK_TO_BOOK_STORE.as("t");

        //Only to generate sql
        final ResultQuery<Record3<String, String, Integer>> query =
                dslContext.select(a.FIRST_NAME, a.LAST_NAME, countDistinct(s.NAME))
                        .from(a)
                        .join(b).on(b.AUTHOR_ID.equal(a.ID))
                        .join(t).on(t.BOOK_ID.equal(b.ID.cast(BigInteger.class)))
                        .join(s).on(t.NAME.equal(s.NAME))
                        .groupBy(a.FIRST_NAME, a.LAST_NAME)
                        .orderBy(countDistinct(s.NAME).desc());

        //log.info(query.getSQL());
        //System.out.println(query.getBindValues());

        ///////////////////////// Using JDBC Template and using jooq to only for sql construction (no transaction as @Transaction is on service)///////////////////////
        /// Nothing related to Jooq is invoked - performance etc
        log.info("============START=====================Only SQL and Using JdbcTemplate for DB execute =======================");
        List<Pojo> result = jdbcTemplate.query(
                query.getSQL(), /*RowMapper*/
                (r, i) -> new Pojo(
                        r.getString(1),
                        r.getString(2),
                        r.getInt(3)
                ), query.getBindValues().toArray()
        );
        log.info("\n" + result.toString());
        log.info("============END=====================Only SQL and Using JdbcTemplate for DB execute =======================\n");

        ///////////////////////// Using JDBC Template and using jooq to only for sql construction (with TransactionTemplate)///////////////////////
        log.info("============START=====================Only SQL and Using JdbcTemplate and TransactionTemplate for DB execute =======================");
        TransactionTemplate txTemplate = new TransactionTemplate(context.getBean("transactionManager", PlatformTransactionManager.class));
        List<Pojo> resultTx = txTemplate.execute(transactionStatus -> {
            List<Pojo> output = jdbcTemplate.query(query.getSQL(), (r, i) -> new Pojo(r.getString(1), r.getString(2), r.getInt(3)), query.getBindValues().toArray());
            return output;
        });
        log.info("\n" + resultTx.toString());
        log.info("============END=====================Only SQL and Using JdbcTemplate and TransactionTemplate for DB execute =======================\n");

        ///////////////////////// Using Jooq DSLContext (perf logging will happen) ///////////////////////
        ///Jooq API is used for execution will see performance
        log.info("============START=====================Using Jooq DSLContext for execution (Perf printed) =======================");
        Result<Record3<String, String, Integer>> jooqResult = query.fetch();
        log.info("\n" + jooqResult.toString());
        log.info("============END=====================Using Jooq DSLContext for execution (Perf printed) =======================\n");

        /////////////////////// Using Spring beans with transaction support ///////////////////////////////
        log.info("============START=====================Using Spring serviceBean where Jooq and Transaction is autowired  (Perf printed) =======================");
        JdbcTemplateBookService bookService = context.getBean("bookService", JdbcTemplateBookService.class);
        log.info("\n" + bookService.get(1).toString());
        log.info("============END=====================Using Spring serviceBean where Jooq and Transaction is autowired  (Perf printed) =======================\n");

        ////////////////////////// Using DSLContext transaction for execution /////////////////////////////////
        log.info("============START=====================Using DSLContext.transaction which is wired with spring tx support  (Perf printed) =======================");
        log.info("=========START DSLContext.transaction=====================COUNT=" + dslContext.fetchCount(BOOK));
        try {
            dslContext.transaction(tx -> {
                for (int i = 0; i < 2; i++)
                    //trying to insert same data twice, so complete rollback, note try-catch is outside
                    dslContext.insertInto(BOOK)
                            .set(BOOK.ID, 5)
                            .set(BOOK.AUTHOR_ID, 1)
                            .set(BOOK.TITLE, "Book 5")
                            .set(BOOK.PUBLISHED_IN, 2021)
                            .set(BOOK.LANGUAGE_ID, 1)
                            .set(BOOK.CREATED_BY, "owner")
                            .set(BOOK.CREATED_TS, LocalDateTime.now())
                            .set(BOOK.VERSION, 0L)
                            .execute();
            });
        } catch (Exception e) {
            System.err.println("DSLContext.transaction-> " + e.getMessage());
        }
        log.info("=============END=================COUNT=" + dslContext.fetchCount(BOOK));
        log.info("============END DSLContext.transaction=====================Using DSLContext.transaction which is wired with spring tx support  (Perf printed) =======================\n");


        ////////////////////////// Using explicit control using transaction manager that is configured /////////////////////////////////
        log.info("============START=====================Using DSLContext with explicit transaction control from spring tx support (Perf printed) =======================");
        log.info("=============START Explicit Tx=================COUNT=" + dslContext.fetchCount(BOOK));
        TransactionStatus txStatus = context.getBean("transactionManager", PlatformTransactionManager.class).getTransaction(new DefaultTransactionDefinition());
        try {
            for (int i = 0; i < 2; i++) {
                dslContext.insertInto(BOOK)
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
        log.info("=============END Explicit Tx=================COUNT=" + dslContext.fetchCount(BOOK));
        log.info("============END=====================Using DSLContext with explicit transaction control from spring tx support  (Perf printed) =======================");

//        for (String poolName : poolManager.getConnectionPoolNames()) {
//            System.out.println(poolManager.getConnectionPool(poolName).getStatistics().toString());
//            poolManager.destroyConnectionPool(poolName);
//        }

        ((ClassPathXmlApplicationContext)context).close();
    }

    static class Pojo {
        final String firstName;
        final String lastName;
        final int books;

        Pojo(String firstName, String lastName, int books) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.books = books;
        }

        @Override
        public String toString() {
            return "Pojo{" +
                    "firstName='" + firstName + '\'' +
                    ", lastName='" + lastName + '\'' +
                    ", books=" + books +
                    '}';
        }
    }

}
