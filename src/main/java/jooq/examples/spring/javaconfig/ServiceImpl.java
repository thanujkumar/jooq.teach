package jooq.examples.spring.javaconfig;

import static jooq.examples.generated.tables.Book.BOOK;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@org.springframework.stereotype.Service
public class ServiceImpl implements Service {

	@Autowired
	DSLContext dsl;

	@Override
	@Transactional
	public int create(int id, int authorId, String title, int publishedDate, int langId) {
		return dsl.insertInto(BOOK).set(BOOK.ID, id).set(BOOK.AUTHOR_ID, authorId).set(BOOK.TITLE, title)
				.set(BOOK.PUBLISHED_IN, publishedDate).set(BOOK.LANGUAGE_ID, langId).execute();
	}

}
