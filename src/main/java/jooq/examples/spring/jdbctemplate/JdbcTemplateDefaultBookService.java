package jooq.examples.spring.jdbctemplate;

import jooq.examples.generated.tables.Book;
import jooq.examples.generated.tables.records.BookRecord;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

public class JdbcTemplateDefaultBookService implements JdbcTemplateBookService {

    @Autowired
    DSLContext dslContext;

    @Override
    public void create(int id, int authorId, String title) {
        BookRecord bookRecord = new BookRecord();
        bookRecord.setId(id);
        bookRecord.setAuthorId(authorId);
        bookRecord.setTitle(title);
        bookRecord.setCreatedBy("Owner");
        bookRecord.setCreatedTs(LocalDateTime.now());
        bookRecord.setVersion(0L);
        dslContext.insertInto(Book.BOOK).set(bookRecord).execute();
    }

    @Override
    public Result<Record> get(int id) {
        return dslContext.select().from(Book.BOOK).where(Book.BOOK.ID.eq(id)).fetch();
    }
}
