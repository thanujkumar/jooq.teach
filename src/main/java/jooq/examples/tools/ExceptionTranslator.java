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
                ctx.exception(translator.translate("jOOQ", left(ctx.query().getSQL(), 2000), ctx.sqlException()));
            } else {
                ctx.exception(translator.translate("jOOQ", "SQL not found", ctx.sqlException()));
            }
        }
    }

    public static String left(String str, int len) {
        if (str == null) {
            return null;
        } else if (len < 0) {
            return "";
        } else {
            return str.length() <= len ? str : str.substring(0, len);
        }
    }
}