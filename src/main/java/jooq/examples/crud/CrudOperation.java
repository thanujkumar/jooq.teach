package jooq.examples.crud;

import static jooq.examples.generated.tables.Author.AUTHOR;

import javax.sql.XAConnection;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.conf.RenderNameStyle;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import jooq.examples.generated.tables.records.AuthorRecord;
import jooq.examples.oracle.InitializeOracleSettings;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.Date;

public class CrudOperation extends InitializeOracleSettings {

	public static void main(String[] args) throws Exception {
		Settings settings = new Settings();
		//settings.setRenderNameStyle(RenderNameStyle.AS_IS);
		
		createPool();

		DSLContext create = DSL.using(pool.getConnection(), SQLDialect.ORACLE20C, settings);

		//select 1 from dual
		Result r =  create.selectOne().fetch();
		System.out.println(r.get(0));
		
		AuthorRecord author = create.fetchOne(AUTHOR, AUTHOR.ID.eq(4));
		
		//Create a new author, if it doesn't exists yet
		if (author == null) {
			author = create.newRecord(AUTHOR);
			author.setId(4);
			author.setFirstName("Thanuj");
			author.setLastName("Kumar");
			author.setCreatedBy("thanuj");
			author.setCreatedTs(LocalDateTime.now());
		}
		
		author.setDistinguished((byte) 1);
		
		author.store();
		
		destroyPool();
				
	}
}
