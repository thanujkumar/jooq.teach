package jooq.examples.spring.jdbctemplate;

import jooq.examples.generated.tables.Author;
import jooq.examples.tools.QueryPerformanceListener;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultExecuteListenerProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.xml.bind.JAXB;
import java.io.File;
import java.sql.SQLException;

//https://www.jooq.org/doc/3.15/manual/sql-building/dsl-context/custom-settings/
public class CustomJooqSettingsMain {
    public static void main(String[] args) throws SQLException {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("jooq-spring-jdbc-template.xml");
        DSLContext dslContext = context.getBean("dslContext", DSLContext.class);

        //Creating a new configuration and DSLContext
        //renderSchema=false, renderQuotedNames=NEVER
        Settings settings = JAXB.unmarshal(new File("./src/main/resources/my-settings.xml"), Settings.class);

        DSLContext dsl = DSL.using(dslContext.parsingDataSource().getConnection(), SQLDialect.ORACLE, settings);
        dsl.configuration().set( new DefaultExecuteListenerProvider(new QueryPerformanceListener()));
        dsl.select().from(Author.AUTHOR).fetch();

        context.registerShutdownHook();
        context.close();
    }
}
