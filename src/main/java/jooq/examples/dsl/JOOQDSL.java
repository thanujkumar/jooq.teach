package jooq.examples.dsl;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

import jooq.examples.generated.tables.Author;
import jooq.examples.generated.tables.Book;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.tools.Convert;


public class JOOQDSL {

    public static void main(String[] args) {
        Settings settings = new Settings();
        settings.setRenderSchema(false);

        DSLContext create = DSL.using(SQLDialect.ORACLE20C, settings);
        String sql = create.select(field("BOOK.TITLE"),
                field("AUTHOR.FIRST_NAME"),
                field("AUTHOR.LAST_NAME"))
                .from(table("BOOK"))
                .join(table("AUTHOR"))
                .on(field("BOOK.AUTHOR_ID").eq(field("AUTHOR.ID")))
                .where(field("BOOK.PUBLISHED_IN").eq(1948)).getSQL();

        System.out.println(sql);

        //To avoid schema to be appended set Render Schema to false
        String sql2 = create.select(Book.BOOK.TITLE, Author.AUTHOR.FIRST_NAME, Author.AUTHOR.LAST_NAME)
                .from(Book.BOOK)
                .join(Author.AUTHOR)
                .on(Book.BOOK.AUTHOR_ID.eq(Author.AUTHOR.ID))
                .where(Book.BOOK.PUBLISHED_IN.eq(1948))
                .getSQL();

        System.out.println(sql2);

    }

}
