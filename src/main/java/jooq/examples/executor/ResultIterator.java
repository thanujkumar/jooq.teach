package jooq.examples.executor;

import static jooq.examples.generated.tables.Author.AUTHOR;

import javax.sql.XAConnection;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.conf.RenderNameStyle;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import jooq.examples.oracle.InitializeOracleSettings;

public class ResultIterator extends InitializeOracleSettings {

	public static void main(String[] args) throws Exception {
		createPool();
		Settings settings = new Settings();
		//settings.setRenderNameStyle(RenderNameStyle.AS_IS);
		XAConnection xacon = pool.getXAConnection();
		
		DSLContext create = DSL.using(xacon.getConnection(), SQLDialect.ORACLE12C, settings);
		
		Result<Record> result = create.select().from(AUTHOR).fetch();
		
		for (Record r : result) {
			Integer id = r.getValue(AUTHOR.ID);
			String firstName = r.getValue(AUTHOR.FIRST_NAME);
			String lastName = r.getValue(AUTHOR.LAST_NAME);
			System.out.println("ID: " + id + " first name: " + firstName + " last name: " + lastName);
		}
		
		xacon.close();
	}
}
