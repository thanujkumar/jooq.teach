package jooq.examples.spring.jdbctemplate;

import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


public interface BookService {

	/**
	 * Create a new book.
	 * <p>
	 * The implementation of this method has a bug, which causes this method to fail
	 * and roll back the transaction.
	 */
	@Transactional
	void create(int id, int authorId, String title, String createdBy);

	DSLContext getDsl();
}
