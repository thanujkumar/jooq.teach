package jooq.examples.spring.jdbctemplate;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.transaction.annotation.Transactional;

public interface JdbcTemplateBookService {

    @Transactional
    void create(int id, int authorId, String title);

    @Transactional(readOnly = true)
    Result<Record> get(int id);

    DSLContext getDsl();
}
