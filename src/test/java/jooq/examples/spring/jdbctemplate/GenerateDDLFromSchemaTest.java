package jooq.examples.spring.jdbctemplate;


import org.jooq.DSLContext;
import org.jooq.Queries;
import org.jooq.Query;
import org.jooq.impl.SchemaImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.jooq.DDLFlag.SCHEMA;

@ExtendWith(SpringExtension.class)
@ContextConfiguration("classpath:jooq-spring-jdbc-template.xml")
public class GenerateDDLFromSchemaTest {
    private static final Logger log = LoggerFactory.getLogger(GenerateDDLFromSchemaTest.class);

    @Autowired
    DSLContext dsl;

    @Autowired
    ApplicationContext context;

    @Test
    @DisplayName("Generate DDL from Schema")
    public void generateDDLTestMe() {
        dsl.transaction(cfx -> {
            Queries ddl =
                    dsl.ddl(dsl.meta().getSchemas("JOOQDATA").get(0));

            for (Query query : ddl.queries()) {
                log.info(query.toString());
            }
        });
    }
}
