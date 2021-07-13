package jooq.examples.spring.jdbctemplate;

import jooq.examples.generated.tables.Book;
import jooq.examples.generated.tables.records.BookRecord;
import org.jooq.DSLContext;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class OptimisticLockingMain {
    //new Settings().withExecuteWithOptimisticLocking(true) set in spring xml
    // and pom.xml has info about version column and timestamp column for code generation
    // <recordVersionFields>VERSION</recordVersionFields>
    //<recordTimestampFields>MODIFIED_TS</recordTimestampFields>
    private static ApplicationContext context = null;

    public static void main(String[] args) {
        context = new ClassPathXmlApplicationContext("jooq-spring-jdbc-template.xml");
        DSLContext dslContext = context.getBean("dslContext", DSLContext.class);


        BookRecord book4 = dslContext.fetchOne(Book.BOOK, Book.BOOK.ID.eq(4));
        System.out.println(book4);
        book4.setTitle("Thanuj");
        book4.store(); // check version and created_ts, both will be updated

    }
}
