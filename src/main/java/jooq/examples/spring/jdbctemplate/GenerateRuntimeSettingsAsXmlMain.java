package jooq.examples.spring.jdbctemplate;

import org.jooq.DSLContext;
import org.jooq.conf.Settings;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.StringWriter;

public class GenerateRuntimeSettingsAsXmlMain {
    private static final String preamble = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<settings xsi:schemaLocation=\"http://www.jooq.org/xsd/jooq-runtime-3.14.9.xsd jooq-runtime-3.14.9.xsd\"\n" +
            "          xmlns=\"http://www.jooq.org/xsd/jooq-runtime-3.14.9.xsd\"\n" +
            "          xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">";

    public static void main(String[] args) throws TransformerException {
        ApplicationContext context = new ClassPathXmlApplicationContext("jooq-spring-jdbc-template.xml");
        DSLContext dslContext = context.getBean("dslContext", DSLContext.class);
        dslContext.selectOne().fetch();

        Settings settings = dslContext.configuration().settings();

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        StreamResult result = new StreamResult(new StringWriter());

        StreamSource source = new StreamSource(new StringReader(preamble + "\n" + settings.toString() + "</settings>"));
        transformer.transform(source, result);
        System.out.println(result.getWriter().toString());

        ((ClassPathXmlApplicationContext)context).close();
    }
}
