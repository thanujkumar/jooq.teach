package jooq.examples;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

public class JOOQConfigSettings {

	public static void main(String[] args) throws TransformerFactoryConfigurationError, TransformerException {
		Settings s = new Settings();
		//System.out.println(s);
		
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		StreamResult result = new StreamResult(new StringWriter());
		
		StreamSource source = new StreamSource(new StringReader("<root>"+s.toString()+"</root>"));
		transformer.transform(source, result);
		System.out.println(result.getWriter().toString());

		Arrays.stream(SQLDialect.families())
				.map(family -> String.format("%17s : ", family) + DSL.using(family).render(DSL.selectOne()))
				.forEach(System.out::println);
	}
}
