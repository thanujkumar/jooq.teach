package jooq.examples.springxml1;

import static jooq.examples.generated.tables.Book.BOOK;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


public class DefaultBookService implements BookService {


    DSLContext dsl;

    @Override
    @Transactional  //@Transactional is already defined in interface, to know whether 1 record gets inserted or not comment both Annotations here and interface
    public void create(int id, int authorId, String title, String createdBy) {

        // This method has a "bug". It creates the same book twice. The second insert
        // should lead to a constraint violation, which should roll back the whole transaction
        for (int i = 0; i < 2; i++)
            dsl.insertInto(BOOK)
                    .set(BOOK.ID, id)
                    .set(BOOK.AUTHOR_ID, authorId)
                    .set(BOOK.TITLE, title)
                    .set(BOOK.PUBLISHED_IN, 2020)
                    .set(BOOK.LANGUAGE_ID, 1)
                    .set(BOOK.CREATED_BY, createdBy)
                    .set(BOOK.CREATED_TS, LocalDateTime.now())
                    .set(BOOK.VERSION, 0L)
                    .execute();
    }

    public DSLContext getDsl() {
        return dsl;
    }

    public void setDsl(DSLContext dsl) {
        this.dsl = dsl;
    }
}
