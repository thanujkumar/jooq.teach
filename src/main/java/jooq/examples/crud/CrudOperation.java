package jooq.examples.crud;

import static jooq.examples.generated.tables.Author.AUTHOR;

import javax.sql.XAConnection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.RenderNameStyle;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import jooq.examples.generated.tables.records.AuthorRecord;
import jooq.examples.oracle.InitializeOracleSettings;

public class CrudOperation extends InitializeOracleSettings {

	public static void main(String[] args) throws Exception {
		Settings settings = new Settings();
		settings.setRenderNameStyle(RenderNameStyle.AS_IS);
		
		createPool();
		
		XAConnection xacon = pool.getXAConnection();
		
		DSLContext create = DSL.using(xacon.getConnection(), SQLDialect.ORACLE12C, settings);
		
		AuthorRecord author = create.fetchOne(AUTHOR, AUTHOR.ID.eq(4));
		
		//Create a new author, if it doesn't exists yet
		if (author == null) {
			author = create.newRecord(AUTHOR);
			author.setId(4);
			author.setFirstName("Thanuj");
			author.setLastName("Kumar");
		}
		
		author.setDistinguished((byte) 1);
		
		author.store();
		
		xacon.close();
				
	}
}
