package jooq.examples.spring.javaconfig;


import jooq.examples.generated.tables.Book;
import oracle.sql.TIMESTAMP;
import org.jooq.DSLContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.LocalDateTime;

import static jooq.examples.generated.Tables.BOOK;

public class OracleAllTablesMain {


    public static void main(String[] args) {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(PersistenceContext.class);
        DSLContext dslContext = ctx.getBean("dsl", org.jooq.impl.DefaultDSLContext.class);

        String sql = "select OWNER, TABLE_NAME, STATUS from all_tables where tablespace_name='USERS'";

        //http://www.java2s.com/Tutorials/Java/Java_Format/0090__Java_Format_General.htm
        dslContext.fetch(sql).stream().forEach(rs -> {
            System.out.printf("%s | %-20s | %s %n", rs.getValue("OWNER", String.class), rs.getValue("TABLE_NAME", String.class), rs.getValue("STATUS", String.class));
        });

        dslContext.select(BOOK.ID, BOOK.AUTHOR_ID, BOOK.TITLE, BOOK.CREATED_BY, BOOK.CREATED_TS)
                .from(BOOK).orderBy(BOOK.ID)
                .fetch().stream().forEach(
                rs -> {
                    System.out.printf("%s | %s | %-15s  | %s | %s %n",
                            rs.getValue("ID", int.class),
                            rs.getValue("AUTHOR_ID", int.class),
                            rs.getValue("TITLE", String.class),
                            rs.getValue("CREATED_BY", String.class),
                            rs.getValue("CREATED_TS", LocalDateTime.class));
                }
        );
    }
}
