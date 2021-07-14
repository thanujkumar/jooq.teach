package jooq.examples.spring.jdbctemplate;

import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static jooq.examples.generated.Tables.BATCH_TEST;

@ExtendWith(SpringExtension.class)
@ContextConfiguration("classpath:jooq-spring-jdbc-template.xml")
public class OracleExplainPlanTest {

    private static final Logger log = LoggerFactory.getLogger(OracleExplainPlanTest.class);

    @Autowired
    DSLContext dsl;

    @Autowired
    ApplicationContext context;

    @Test
    @DisplayName("select Explain Plan")
    @Tag("performance")
    public void selectExplainPlan() {

        //With derive make a copy of configuration and settings
        Configuration config = dsl.configuration().derive();
        Settings settings = config.settings().withInListPadding(true) //default to false
                .withInListPadBase(4) //default is 2
                .withFetchSize(500)
                .withExecuteLogging(false);

        String planInfo = dsl.explain(DSL.using(config).select().from(BATCH_TEST)).plan();
        log.info("\n" + planInfo);
    }

}
