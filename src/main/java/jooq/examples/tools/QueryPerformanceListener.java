package jooq.examples.tools;

import org.jooq.ExecuteContext;
import org.jooq.impl.DefaultExecuteListener;
import org.jooq.tools.StopWatch;

/*
 * Just hook a simple execute listener into your jOOQ Configuration
 */
class QueryPerformanceListener extends DefaultExecuteListener {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	StopWatch watch;

	class SQLPerformanceWarning  extends Exception {
	}

	@Override
	public void executeStart(ExecuteContext ctx) {
		super.executeStart(ctx);
		watch = new StopWatch();
	}

	@Override
	public void executeEnd(ExecuteContext ctx) {
		super.executeEnd(ctx);
			System.out.println("Slow SQL"+
					"jOOQ Meta executed a slow query" + "\n\n" + "Please report this bug here: "
							+ "https://github.com/jOOQ/jOOQ/issues/new\n\n" + ctx.query());
	}
}