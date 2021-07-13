package jooq.examples.spring.jdbctemplate;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.jooq.util.oracle.OracleDSL;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static jooq.examples.generated.Tables.*;
import static org.jooq.impl.DSL.count;

//https://www.jooq.org/doc/3.15/manual/sql-building/dsl/dsl-subclasses/
public class JooqDSLMain {
    //If using Oracle then use ORACLEDSL instead of generic org.jooq.impl.DSL.*
    private static ApplicationContext context = null;

    public static void main(String[] args) {
        context = new ClassPathXmlApplicationContext("jooq-spring-jdbc-template.xml");
        ((ClassPathXmlApplicationContext)context).registerShutdownHook();

        DSLContext dslContext = context.getBean("dslContext", DSLContext.class);
        //https://www.jooq.org/doc/3.15/manual/sql-building/dsl-context/
        Result<?> result = dslContext.selectOne().fetch();

        result = OracleDSL.using(dslContext.configuration()).select()
                        .from(BOOK)
                        .where(BOOK.TITLE.like("Animal%"))
                        .fetch();

        System.out.println(result);


       result =  OracleDSL.using(dslContext.configuration()).select(AUTHOR.FIRST_NAME, AUTHOR.LAST_NAME, count())
                .from(AUTHOR)
                .join(BOOK).on(BOOK.AUTHOR_ID.eq(AUTHOR.ID))
                .join(LANGUAGE).on(BOOK.LANGUAGE_ID.eq(LANGUAGE.ID))
                .where(LANGUAGE.CD.eq("de"))
                .and(BOOK.PUBLISHED_IN.gt(1850))
                .groupBy(AUTHOR.FIRST_NAME, AUTHOR.LAST_NAME)
                .having(count().gt(0))
                .orderBy(AUTHOR.LAST_NAME.asc().nullsFirst())
                .limit(1)
                .offset(0)
                //.forUpdate()
                .fetch();

        System.out.println(result);
        ((ClassPathXmlApplicationContext)context).close();
    }
}
