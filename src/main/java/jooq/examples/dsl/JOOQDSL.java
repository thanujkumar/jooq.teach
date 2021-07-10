package jooq.examples.dsl;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;



public class JOOQDSL {
	
	public static void main(String[] args) {
		DSLContext create = DSL.using(SQLDialect.ORACLE20C);
		String sql = create.select(field("BOOK.TITLE"),
				                   field("AUTHOR.FIRST_NAME"),
				                   field("AUTHOR.LAST_NAME"))
				 .from(table("BOOK"))
				 .join(table("AUTHOR"))
				 .on(field("BOOK.AUTHOR_ID").eq(field("AUTHOR.ID")))
				 .where(field("BOOK.PUBLISHED_IN").eq(1948)).getSQL();
		
		System.out.println(sql);
		
		
				                  
	}

}
