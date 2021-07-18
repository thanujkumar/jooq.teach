package jooq.examples.spring.jdbctemplate;

import jooq.examples.generated.tables.Author;
import jooq.examples.generated.tables.records.AuthorRecord;
import org.jooq.DSLContext;
import org.jooq.RecordHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

//https://www.jooq.org/doc/latest/manual/sql-execution/fetching/recordhandler/
@ExtendWith(SpringExtension.class)
@ContextConfiguration("classpath:jooq-spring-jdbc-template.xml")
public class UsingRecordHandlerTest {
    private static final Logger log = LoggerFactory.getLogger(UsingRecordHandlerTest.class);

    @Autowired
    DSLContext dsl;

    @Autowired
    ApplicationContext context;

    @Test
    @DisplayName("Using RecordHandler for custom mapping (similar to JdbcTemplate)")
    public void usingRecordHandlerTestMe() {
        dsl.selectFrom(Author.AUTHOR).orderBy(2).fetch().into(new RecordHandler<AuthorRecord>() {

            @Override
            public void next(AuthorRecord record) {
                log.info("\n" + record.toString());
            }
        });

        //As RecordHandler is deprecated, recommendation is to use iterator

        dsl.selectFrom(Author.AUTHOR).orderBy(2).fetch().forEach(authorRecord -> {
            log.info("\n" + authorRecord.toString());
        });

    }
}
