/*
 * This file is generated by jOOQ.
 */
package jooq.examples.generated.tables.daos;


import java.time.LocalDate;
import java.util.List;

import javax.annotation.Generated;

import jooq.examples.generated.tables.Author;
import jooq.examples.generated.tables.records.AuthorRecord;

import org.jooq.Configuration;
import org.jooq.impl.DAOImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.5"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
@Repository
public class AuthorDao extends DAOImpl<AuthorRecord, jooq.examples.generated.tables.pojos.Author, Integer> {

    /**
     * Create a new AuthorDao without any configuration
     */
    public AuthorDao() {
        super(Author.AUTHOR, jooq.examples.generated.tables.pojos.Author.class);
    }

    /**
     * Create a new AuthorDao with an attached configuration
     */
    @Autowired
    public AuthorDao(Configuration configuration) {
        super(Author.AUTHOR, jooq.examples.generated.tables.pojos.Author.class, configuration);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Integer getId(jooq.examples.generated.tables.pojos.Author object) {
        return object.getId();
    }

    /**
     * Fetch records that have <code>ID IN (values)</code>
     */
    public List<jooq.examples.generated.tables.pojos.Author> fetchById(Integer... values) {
        return fetch(Author.AUTHOR.ID, values);
    }

    /**
     * Fetch a unique record that has <code>ID = value</code>
     */
    public jooq.examples.generated.tables.pojos.Author fetchOneById(Integer value) {
        return fetchOne(Author.AUTHOR.ID, value);
    }

    /**
     * Fetch records that have <code>FIRST_NAME IN (values)</code>
     */
    public List<jooq.examples.generated.tables.pojos.Author> fetchByFirstName(String... values) {
        return fetch(Author.AUTHOR.FIRST_NAME, values);
    }

    /**
     * Fetch records that have <code>LAST_NAME IN (values)</code>
     */
    public List<jooq.examples.generated.tables.pojos.Author> fetchByLastName(String... values) {
        return fetch(Author.AUTHOR.LAST_NAME, values);
    }

    /**
     * Fetch records that have <code>DATE_OF_BIRTH IN (values)</code>
     */
    public List<jooq.examples.generated.tables.pojos.Author> fetchByDateOfBirth(LocalDate... values) {
        return fetch(Author.AUTHOR.DATE_OF_BIRTH, values);
    }

    /**
     * Fetch records that have <code>YEAR_OF_BIRTH IN (values)</code>
     */
    public List<jooq.examples.generated.tables.pojos.Author> fetchByYearOfBirth(Integer... values) {
        return fetch(Author.AUTHOR.YEAR_OF_BIRTH, values);
    }

    /**
     * Fetch records that have <code>DISTINGUISHED IN (values)</code>
     */
    public List<jooq.examples.generated.tables.pojos.Author> fetchByDistinguished(Byte... values) {
        return fetch(Author.AUTHOR.DISTINGUISHED, values);
    }
}