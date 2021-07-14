package jooq.examples.spring.jdbctemplate;


import jooq.examples.generated.tables.Author;
import jooq.examples.generated.tables.Book;
import jooq.examples.generated.tables.records.BookRecord;
import org.jooq.Record;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigInteger;

import static jooq.examples.generated.Tables.*;
import static org.jooq.impl.DSL.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration("classpath:jooq-spring-jdbc-template.xml")
public class SqlStatementDMLTest {

    private static final Logger log = LoggerFactory.getLogger(SqlStatementDMLTest.class);

    @Autowired
    DSLContext dsl;

    @Autowired
    ApplicationContext context;

    @Test
    @DisplayName("Basic SELECT")
    public void basicSelectTest() {

        Author a = AUTHOR.as("a");
        Book b = BOOK.as("b");

        Result<Record> result =
                dsl.select(a.asterisk())
                        .from(AUTHOR.as("a"))
                        .join(BOOK.as("b")).on(a.ID.eq(b.AUTHOR_ID))
                        .where(a.YEAR_OF_BIRTH.gt(1920)
                                .and(a.FIRST_NAME.eq("Paulo")))
                        .orderBy(b.TITLE)
                        .fetch();
        log.info(result.toString());
    }

    //https://www.jooq.org/doc/latest/manual/sql-building/sql-statements/dsl-and-non-dsl/
    @Test
    @DisplayName("Model API instead of DSL")
    public void usingModelAPITest() {

        SelectQuery<Record> query = dsl.selectQuery();
        query.addFrom(AUTHOR);
        query.addJoin(BOOK, BOOK.AUTHOR_ID.eq(AUTHOR.ID));

        Result<?> result = query.fetch();
        log.info(result.toString());
    }

    @Test
    @DisplayName("Explicit Common Table Expression")
    public void explicitCommonTableExpressionTest() {
        /*
        WITH a AS (
            SELECT * FROM  book
        )SELECT  * FROM a;
         */
        CommonTableExpression<Record> te = DSL.name("a").as(dsl.select().from(BOOK));

        Result<?> result = dsl.with(te).select(te.asterisk()).from(te).fetch();
        log.info(result.toString());

        result = dsl.with(te).select(te.fields(BOOK.ID, BOOK.TITLE)).from(te).fetch();
        log.info(result.toString());

        CommonTableExpression<Record2<Integer, String>> te2 = DSL.name("a").as(dsl.select(BOOK.ID, BOOK.TITLE).from(BOOK));
        result = dsl.with(te2).select(te2.fields(BOOK.ID, BOOK.TITLE)).from(te2).fetch();
        log.info(result.toString());
    }


    @Test
    @DisplayName("Inline Common Table Expression")
    public void inlineCommonTableExpressionTest() {
        /*
        WITH a AS (
            SELECT * FROM  book
        )SELECT  * FROM a;
         */
        Result<Record> result = dsl.with("a").as(
                dsl.select(BOOK.ID, BOOK.TITLE).from(BOOK))
                .select().from(DSL.table(DSL.name("a"))).fetch();
        log.info(result.toString());


        Result<Record> result2 = dsl.with("a").as(
                dsl.select(BOOK.ID, BOOK.TITLE).from(BOOK))
                .select().from(DSL.table(DSL.name("a"))).fetch();
        log.info(result2.toString());
    }

    @Test
    @DisplayName("With Recursive Common Table Expression")
    public void withRecursiveCommonTableExpressionTest() {
        /*
        WITH a (id, name) as (
            SELECT id, title FROM  book
        )SELECT  * FROM a;
         */
        CommonTableExpression<?> cte = DSL.name("a").fields("id", "name")
                .as(DSL.select(BOOK.ID, BOOK.TITLE).from(BOOK));

        Result<?> result = dsl.withRecursive(cte).selectFrom(cte).fetch();
        log.info(result.toString());
    }

    @Test
    @DisplayName("Projections With Select Statment")
    public void selectProjectionTest() {
        Result result = dsl.select(AUTHOR.FIRST_NAME, AUTHOR.LAST_NAME, count())
                .from(AUTHOR)
                .join(BOOK).on(BOOK.AUTHOR_ID.eq(AUTHOR.ID))
                .where(BOOK.LANGUAGE_ID.eq(1))
                .and(BOOK.PUBLISHED_IN.gt(1800))
                .groupBy(AUTHOR.FIRST_NAME, AUTHOR.LAST_NAME)
                .having(count().gt(1))
                .orderBy(AUTHOR.LAST_NAME.asc().nullsFirst())
                .limit(2)
                .offset(0)
                //.forUpdate()
                .fetch();

        log.info(result.toString());
    }

    //https://www.jooq.org/doc/latest/manual/sql-building/sql-statements/select-statement/select-clause/
    @Test
    @DisplayName("SELECT clause Examples")
    public void selectClauseExampleTest() {
        Result result = dsl.select(BOOK.ID, BOOK.TITLE).from(BOOK).fetch();
        log.info(result.toString());

        result = dsl.selectCount().from(BOOK).fetch();
        log.info(result.toString());

        Select select = dsl.select(val("1")).from(BOOK);
        select.bind("1", BOOK.ID);
        result = select.fetch();
        log.info(result.toString());

        Result<Record1<String>> select1 = dsl.selectDistinct(BOOK.TITLE).from(BOOK).fetch();
        log.info(select1.toString());

        result = dsl.select(asterisk()).from(BOOK.as("a"), AUTHOR.as("b")).fetch();
        log.info(result.toString());
    }

    @Test
    @DisplayName("Advanced Table Expressions")
    public void advancedTableExpressionTest() {
        Result result = dsl.select().from(
                table("book")
        ).fetch();
        log.info(result.toString());

        //Explain plan
        String planInfo = dsl.explain(dsl.select().from(BOOK)).plan();
        log.info(planInfo);

    }


    @Test
    @DisplayName("Order By")
    public void orderByTest() {
        Result result = dsl.select(BOOK.AUTHOR_ID, BOOK.TITLE)
                .from(BOOK)
                .orderBy(one().asc(), inline(2).desc())
                .fetch();

        log.info("\n" + result.toString());

        result = dsl.select(BOOK.AUTHOR_ID, BOOK.TITLE)
                .from(BOOK)
                .orderBy(inline(1).asc(), two().asc())
                .fetch();

        log.info("\n" + result.toString());

    }

    //https://www.jooq.org/doc/latest/manual/sql-building/sql-statements/select-statement/order-by-clause/
    @Test
    @DisplayName("Using CASE Expression")
    public void caseExpressionTest() {
        Result result = dsl.select()
                .from(BOOK)
                .orderBy(case_(BOOK.TITLE)
                        .when("1984", 0)
                        .when("Animal Farm", 1)
                        .else_(2).asc())
                .fetch();

        log.info("\n" + result.toString());

        result = dsl.select()
                .from(BOOK)
                .orderBy(BOOK.TITLE.sortAsc("1984", "Animal Farm"))
                .fetch();

        log.info("\n" + result.toString());
    }

    //https://www.jooq.org/doc/latest/manual/sql-building/sql-statements/select-statement/with-ties-clause/
    @Test
    @DisplayName("With TIES Clause")
    public void withTieClauseTest() {
        Result result = dsl.selectFrom(BOOK)
                .orderBy(BOOK.AUTHOR_ID)
                .limit(1).withTies()
                .fetch();

        log.info("\n" + result.toString());
    }

    //https://www.jooq.org/doc/latest/manual/sql-building/sql-statements/select-statement/seek-clause/
    @Test
    @DisplayName("using SEEK Clause")
    public void seekClauseTest() {
        Result result = dsl.select(BATCH_TEST.ID, BATCH_TEST.AGE)
                .from(BATCH_TEST)
                .orderBy(BATCH_TEST.ID, BATCH_TEST.AGE)
                .seek(5, BigInteger.valueOf(12))
                .limit(5)
                .fetch();

        log.info("\n" + result.toString());
    }

    //https://www.jooq.org/doc/latest/manual/sql-building/sql-statements/select-statement/for-clause/
    @Test
    @DisplayName("using FOR Clause to convert to XML or JSON")
    public void forClauseTest() {
        Result result = dsl.select(BOOK.ID, BOOK.TITLE)
                .from(BOOK)
                .orderBy(BOOK.ID)
                .forXML().path("book").root("books")
                .fetch();

        log.info("\n" + result.getValues(0));

        result = dsl.select(BOOK.ID, BOOK.TITLE)
                .from(BOOK)
                .orderBy(BOOK.ID)
                .forJSON().path().root("books")
                .fetch();

        log.info("\n" + result.getValues(0));
    }


    //https://www.jooq.org/doc/latest/manual/sql-building/sql-statements/select-statement/for-update-clause/
    @Test
    @DisplayName("for UPDATE clause")
    public void forUpdateTest() {
        //should be in transaction
        dsl.transaction(cf -> {
            dsl.configuration().settings().withQueryTimeout(1000);//millis
            Result<Record> result = dsl.select()
                    .from(BOOK)
                    .where(BOOK.ID.eq(3))
                    .forUpdate()
                    .fetch();

            BookRecord bookRecord = (BookRecord) result.get(0);
            bookRecord.setCreatedBy("tk");
            bookRecord.setModifiedBy("aj");
            bookRecord.store();
        });

        // ------------------- ORDER OF EXECUTION ------------------------------ //
        //https://www.jooq.org/doc/latest/manual/sql-building/sql-statements/select-statement/select-lexical-vs-logical-order/
    }

}
