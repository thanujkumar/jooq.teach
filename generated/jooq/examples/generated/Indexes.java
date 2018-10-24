/*
 * This file is generated by jOOQ.
 */
package jooq.examples.generated;


import javax.annotation.Generated;

import jooq.examples.generated.tables.Author;
import jooq.examples.generated.tables.Book;
import jooq.examples.generated.tables.BookStore;
import jooq.examples.generated.tables.BookToBookStore;
import jooq.examples.generated.tables.Language;

import org.jooq.Index;
import org.jooq.OrderField;
import org.jooq.impl.Internal;


/**
 * A class modelling indexes of tables of the <code>JOOQDATA</code> schema.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.5"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Indexes {

    // -------------------------------------------------------------------------
    // INDEX definitions
    // -------------------------------------------------------------------------

    public static final Index AUTHOR_PK = Indexes0.AUTHOR_PK;
    public static final Index BOOK_PK = Indexes0.BOOK_PK;
    public static final Index BOOK_STORE_NAME_UN = Indexes0.BOOK_STORE_NAME_UN;
    public static final Index BOOK_TO_BOOK_STORE_PK = Indexes0.BOOK_TO_BOOK_STORE_PK;
    public static final Index LANGUAGE_PK = Indexes0.LANGUAGE_PK;

    // -------------------------------------------------------------------------
    // [#1459] distribute members to avoid static initialisers > 64kb
    // -------------------------------------------------------------------------

    private static class Indexes0 {
        public static Index AUTHOR_PK = Internal.createIndex("AUTHOR_PK", Author.AUTHOR, new OrderField[] { Author.AUTHOR.ID }, true);
        public static Index BOOK_PK = Internal.createIndex("BOOK_PK", Book.BOOK, new OrderField[] { Book.BOOK.ID }, true);
        public static Index BOOK_STORE_NAME_UN = Internal.createIndex("BOOK_STORE_NAME_UN", BookStore.BOOK_STORE, new OrderField[] { BookStore.BOOK_STORE.NAME }, true);
        public static Index BOOK_TO_BOOK_STORE_PK = Internal.createIndex("BOOK_TO_BOOK_STORE_PK", BookToBookStore.BOOK_TO_BOOK_STORE, new OrderField[] { BookToBookStore.BOOK_TO_BOOK_STORE.NAME, BookToBookStore.BOOK_TO_BOOK_STORE.BOOK_ID }, true);
        public static Index LANGUAGE_PK = Internal.createIndex("LANGUAGE_PK", Language.LANGUAGE, new OrderField[] { Language.LANGUAGE.ID }, true);
    }
}
