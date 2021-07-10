package jooq.examples.spring.javaconfig;

import org.springframework.transaction.annotation.Transactional;

public interface Service {

	@Transactional
	int create(int id, int authorId, String title, int publishedDate, int langId, String createdBy);
}
