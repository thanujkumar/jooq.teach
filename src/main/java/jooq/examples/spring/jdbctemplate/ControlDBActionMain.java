package jooq.examples.spring.jdbctemplate;

import jooq.examples.generated.tables.Author;
import org.jooq.DSLContext;
import org.jooq.ExecuteContext;
import org.jooq.Insert;
import org.jooq.conf.ParamType;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DefaultExecuteListener;
import org.jooq.impl.DefaultExecuteListenerProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static jooq.examples.generated.Tables.AUTHOR;

public class ControlDBActionMain {
    private static ApplicationContext context = null;

    public static void main(String[] args) {
        context = new ClassPathXmlApplicationContext("jooq-spring-jdbc-template.xml");
        ((ClassPathXmlApplicationContext)context).registerShutdownHook();

        try {
            DSLContext dslContext = context.getBean("dslContext", DSLContext.class);

            dslContext.configuration().set(new DefaultExecuteListenerProvider(new NoInsertListener()));
            dslContext.configuration().data("inserts.barred", true);

            dslContext.insertInto(Author.AUTHOR, AUTHOR.ID, AUTHOR.LAST_NAME)
                    .values(1, "Thanuj").execute();
        } finally {
            ((ClassPathXmlApplicationContext) context).close();
        }
    }
}

class NoInsertListener extends DefaultExecuteListener {

    @Override
    public void start(ExecuteContext ctx) {
        if (Boolean.TRUE.equals(ctx.configuration().data("inserts.barred"))) {
            if (ctx.query() instanceof Insert) {
                throw new DataAccessException("inserts are barred now. \n" + ctx.query().getSQL(ParamType.INLINED));
            }
        }
        super.start(ctx);
    }
}