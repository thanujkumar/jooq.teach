package jooq.examples.tools;

import org.jooq.codegen.GenerationTool;

public class CodeGeneration {

	public static void main(String[] args) throws Exception {
		GenerationTool.main(new String[] {"src/main/resources/codegen-config.xml"});
	}
}
