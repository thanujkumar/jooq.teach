package jooq.examples.dsl;

import jooq.examples.generated.tables.Author;
import jooq.examples.generated.tables.Book;
import org.jooq.*;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import javax.sql.XAConnection;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

import jooq.examples.oracle.InitializeOracleSettings;

public class ExecuteJOOQDSL extends  InitializeOracleSettings{

    //https://www.jooq.org/doc/3.15/manual/getting-started/use-cases/jooq-as-a-sql-builder-without-codegeneration/

    public static void main(String[] args) throws Exception {
        Settings settings = new Settings();
        settings.setRenderSchema(false);
        settings.setExecuteLogging(true);

        createPool();



        /////////// WITHOUT USING JOOQ CODE GENERATORS //////////////
        DSLContext create = DSL.using(pool.getConnection(), SQLDialect.ORACLE20C, settings);
        Query query = create.select(field("BOOK.TITLE"),
                field("AUTHOR.FIRST_NAME"),
                field("AUTHOR.LAST_NAME"))
                .from(table("BOOK"))
                .join(table("AUTHOR"))
                .on(field("BOOK.AUTHOR_ID").eq(field("AUTHOR.ID")))
                .where(field("BOOK.PUBLISHED_IN").eq(1948));

        System.out.println(query.getSQL());
        System.out.println(query.getSQL(ParamType.INLINED));
        System.out.println(query.getBindValues());

        Result<Record3<Object, Object, Object>> result = create.select(field("BOOK.TITLE"),
                field("AUTHOR.FIRST_NAME"),
                field("AUTHOR.LAST_NAME"))
                .from(table("BOOK"))
                .join(table("AUTHOR"))
                .on(field("BOOK.AUTHOR_ID").eq(field("AUTHOR.ID")))
                .where(field("BOOK.PUBLISHED_IN").eq(1948)).fetch();
        System.out.println(result);

        //----------------------------------------------------------------------------------------- //

        ///////////  USING JOOQ CODE GENERATORS //////////////
        //To avoid schema to be appended set Render Schema to false
        String sql2 = create.select(Book.BOOK.TITLE, Author.AUTHOR.FIRST_NAME, Author.AUTHOR.LAST_NAME)
                .from(Book.BOOK)
                .join(Author.AUTHOR)
                .on(Book.BOOK.AUTHOR_ID.eq(Author.AUTHOR.ID))
                .where(Book.BOOK.PUBLISHED_IN.eq(1948))
                .getSQL();

        System.out.println(sql2);
        System.out.println(create.select(Book.BOOK.TITLE, Author.AUTHOR.FIRST_NAME, Author.AUTHOR.LAST_NAME)
                .from(Book.BOOK)
                .join(Author.AUTHOR)
                .on(Book.BOOK.AUTHOR_ID.eq(Author.AUTHOR.ID))
                .where(Book.BOOK.PUBLISHED_IN.eq(1948)).fetch());

        destroyPool();
    }

}
