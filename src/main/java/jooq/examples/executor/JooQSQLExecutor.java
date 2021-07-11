package jooq.examples.executor;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

import javax.sql.XAConnection;

import org.jooq.DSLContext;
import org.jooq.Record3;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import jooq.examples.oracle.InitializeOracleSettings;

public class JooQSQLExecutor extends InitializeOracleSettings {

	public static void main(String[] args) throws Exception {

		createPool();

		DSLContext create = DSL.using(pool.getConnection(), SQLDialect.ORACLE12C);
		Result<Record3<Object, Object, Object>> result =  create.select(field("BOOK.TITLE"),
                                                   field("AUTHOR.FIRST_NAME"),
                                                   field("AUTHOR.LAST_NAME"))
                                                   .from(table("BOOK"))
                                                   .join(table("AUTHOR"))
                                                   .on(field("BOOK.AUTHOR_ID").eq(field("AUTHOR.ID")))
                                                   .where(field("BOOK.PUBLISHED_IN").eq(1948)).fetch();
		result.forEach(x -> System.out.println(x));
		
		destroyPool();
	}

}
