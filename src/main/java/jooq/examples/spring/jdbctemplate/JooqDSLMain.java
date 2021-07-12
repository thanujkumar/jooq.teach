package jooq.examples.spring.jdbctemplate;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.jooq.util.oracle.OracleDSL;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static jooq.examples.generated.Tables.BOOK;

//https://www.jooq.org/doc/3.15/manual/sql-building/dsl/dsl-subclasses/
public class JooqDSLMain {
    //If using Oracle then use ORACLEDSL instead of generic org.jooq.impl.DSL.*
    private static ApplicationContext context = null;

    public static void main(String[] args) {
        context = new ClassPathXmlApplicationContext("jooq-spring-jdbc-template.xml");
        DSLContext dslContext = context.getBean("dslContext", DSLContext.class);
        //https://www.jooq.org/doc/3.15/manual/sql-building/dsl-context/
        Result<?> result = dslContext.selectOne().fetch();

        result = OracleDSL.using(dslContext.configuration()).select()
                        .from(BOOK)
                        .where(BOOK.TITLE.like("Animal%"))
                        .fetch();

        System.out.println(result);
    }
}
