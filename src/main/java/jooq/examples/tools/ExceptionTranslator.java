package jooq.examples.tools;

import org.jooq.ExecuteContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.impl.DefaultExecuteListener;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.jdbc.support.SQLStateSQLExceptionTranslator;

//https://www.petrikainulainen.net/programming/jooq/using-jooq-with-spring-configuration/
public class ExceptionTranslator extends DefaultExecuteListener {

    /**
     * Generated UID
     */
    private static final long serialVersionUID = -2450323227461061152L;

    @Override
    public void exception(ExecuteContext ctx) {

        // [#4391] Translate only SQLExceptions
        if (ctx.sqlException() != null) {
            SQLDialect dialect = ctx.dialect();
            SQLExceptionTranslator translator = (dialect != null)
                    ? new SQLErrorCodeSQLExceptionTranslator(dialect.thirdParty().springDbName())
                    : new SQLStateSQLExceptionTranslator();

            //ctx.exception(translator.translate("jOOQ", ctx.sql(), ctx.sqlException()));
            //sql may be null
            if (ctx.query() != null) {
                ctx.exception(translator.translate("jOOQ", ctx.query().getSQL(), ctx.sqlException()));
            } else {
                ctx.exception(translator.translate("jOOQ", "SQL not found", ctx.sqlException()));
            }
        }
    }
}