package jooq.examples.tools;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public aspect ProfilingAspect {
	
	static DecimalFormat df = null;
	
	static {
		df = new DecimalFormat("#.####");
		df.setRoundingMode(RoundingMode.CEILING);
	}
	
	pointcut publicOperation() : execution(public * *.*(..));
	
	Object around() : publicOperation() {
		long start = System.nanoTime();
		Object ret = proceed();
		long end = System.nanoTime();
		//System.out.println(Arrays.toString(thisJoinPoint.getArgs()));
		System.out.println(thisJoinPointStaticPart.getSignature()
				+ " took " + (end-start) + " nanoseconds , "+df.format((end-start)/1000000.0D)+" milliseconds");
				return ret;
	}
}
