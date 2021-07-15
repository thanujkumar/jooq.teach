package jooq.examples.tools;

import org.jooq.ExecuteContext;
import org.jooq.conf.ParamType;
import org.jooq.impl.DefaultExecuteListener;
import org.jooq.tools.JooqLogger;
import org.jooq.tools.StopWatch;

/*
 * Just hook a simple execute listener into your jOOQ Configuration
 */
public class QueryPerformanceListener extends DefaultExecuteListener {

    private static final JooqLogger log = JooqLogger.getLogger(QueryPerformanceListener.class);

    //https://www.jooq.org/doc/3.15/manual/getting-started/use-cases/jooq-for-pros/

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    StopWatch watch;

    @Override
    public void executeStart(ExecuteContext ctx) {
        super.executeStart(ctx);
        watch = new StopWatch();
    }

    @Override
    public void executeEnd(ExecuteContext ctx) {

        super.executeEnd(ctx);
//        System.out.println("Performance of SQL \n"
//                + ctx.query() + "\n took " + StopWatch.format(watch.split()));

        //       System.out.println("Execution time : " + StopWatch.format(watch.split()) + ". Query : " + ctx.sql());
        // log.info("Execution time : " + StopWatch.format(watch.split()) + ". Query : " + ctx.query().getSQL(ParamType.INLINED));
        log.info("Execution time : " + StopWatch.format(watch.split()) + ". Query : " + left(ctx.sql(), 1000));
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