package jooq.examples.spring.jdbctemplate;

import org.jooq.DSLContext;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration("classpath:jooq-spring-jdbc-template.xml")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TableExpressionTest {

    private static final Logger log = LoggerFactory.getLogger(TableExpressionTest.class);

    @Autowired
    DSLContext dsl;

    @Autowired
    ApplicationContext context;
}
