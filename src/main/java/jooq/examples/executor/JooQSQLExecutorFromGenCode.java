package jooq.examples.executor;

import javax.sql.XAConnection;

import static jooq.examples.generated.tables.Book.*;
import static jooq.examples.generated.tables.Author.*;

import org.jooq.DSLContext;
import org.jooq.Record3;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import jooq.examples.oracle.InitializeOracleSettings;

public class JooQSQLExecutorFromGenCode extends InitializeOracleSettings {

	public static void main(String[] args) throws Exception {
		createPool();

		XAConnection xacon = pool.getXAConnection();

		DSLContext create = DSL.using(xacon.getConnection(), SQLDialect.ORACLE12C);

		Result<Record3<String, String, String>> result = 
				     create.select(BOOK.TITLE, AUTHOR.FIRST_NAME, AUTHOR.LAST_NAME)
				           .from(BOOK)
				           .join(AUTHOR)
				           .on(BOOK.AUTHOR_ID.eq(AUTHOR.ID))
				           .where(BOOK.PUBLISHED_IN.eq(1948)).fetch();
		
		result.forEach(System.out::println);
		
		xacon.close();
	}
}
