package jooq.examples.spring.jdbctemplate;

import jooq.examples.generated.tables.Author;
import jooq.examples.spring.jdbctemplate.dto.AuthorVo;
import jooq.examples.spring.jdbctemplate.sfm.*;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.simpleflatmapper.jooq.JooqMapperFactory;
import org.simpleflatmapper.jooq.SelectQueryMapper;
import org.simpleflatmapper.jooq.SelectQueryMapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static jooq.examples.generated.Tables.AUTHOR;
import static jooq.examples.generated.Tables.BOOK;

@ExtendWith(SpringExtension.class)
@ContextConfiguration("classpath:jooq-spring-jdbc-template.xml")
public class UsingSimpleFlatMapperTest {

    private static final Logger log = LoggerFactory.getLogger(UsingSimpleFlatMapperTest.class);

    @Autowired
    DSLContext dsl;

    @Autowired
    ApplicationContext context;

    //https://simpleflatmapper.org/0106-getting-started-jooq.html
    @Test
    @DisplayName("Using SimpleFlatMapper libary - basic test")
    public void basicSFMTestMe() {
        //Create a copy so that original settings is not modified that is configured in xml
        Configuration tempConfig = dsl.configuration().derive();

        //Mapping which matches the fields
        List<AuthorVo> authorVoList = DSL.using(tempConfig
                .set(SQLDialect.ORACLE)
                .set(JooqMapperFactory.newInstance().ignorePropertyNotFound().newRecordMapperProvider())).select().from(Author.AUTHOR).fetchInto(AuthorVo.class);

        log.info("\n" + authorVoList);

        //Mapping which matches the fields, here maps based on type first available (check id and _id fields)
        List<SfmCustomAuthorVo> sfmCustomAuthorVos = DSL.using(tempConfig
                .set(SQLDialect.ORACLE)
                .set(JooqMapperFactory.newInstance().ignorePropertyNotFound().newRecordMapperProvider())).select().from(Author.AUTHOR).fetchInto(SfmCustomAuthorVo.class);

        log.info("\n" + sfmCustomAuthorVos);

        //Mapping which matches the fields, here maps based on type first available, however we have invalid data types though field name matches column (auto conversion?)
        // a_dateOfBirth is not mapped
        List<SfmInvalidCustomAuthorVo> sfmInvalidCustomAuthorVos = DSL.using(tempConfig
                .set(SQLDialect.ORACLE)
                .set(JooqMapperFactory.newInstance().ignorePropertyNotFound().newRecordMapperProvider())).select().from(Author.AUTHOR).fetchInto(SfmInvalidCustomAuthorVo.class);

        log.info("\n" + sfmInvalidCustomAuthorVos);

        //map select fields to partially mapping class
        List<SfmPartialCustomAuthorVo> sfmPartialCustomAuthorVos1 = DSL.using(tempConfig
                .set(SQLDialect.ORACLE)
                .set(JooqMapperFactory.newInstance().ignorePropertyNotFound().newRecordMapperProvider())).select().from(Author.AUTHOR).fetchInto(SfmPartialCustomAuthorVo.class);

        log.info("\n" + sfmPartialCustomAuthorVos1);

        //map subset of select fields
        List<SfmPartialCustomAuthorVo> sfmPartialCustomAuthorVos2 = DSL.using(tempConfig
                .set(SQLDialect.ORACLE)
                .set(JooqMapperFactory.newInstance().ignorePropertyNotFound().newRecordMapperProvider())).select(Author.AUTHOR.ID).from(Author.AUTHOR).fetchInto(SfmPartialCustomAuthorVo.class);

        log.info("\n" + sfmPartialCustomAuthorVos2);

        //Custom mapping
        List<SfmMapCustomAuthorVo> sfmMapCustomAuthorVos = DSL.using(
                tempConfig.set(SQLDialect.ORACLE)
                        .set(JooqMapperFactory.newInstance()
                                .ignorePropertyNotFound()
                                .addAlias("id", "a")
                                .addAlias(Author.AUTHOR.YEAR_OF_BIRTH.getName(), "b")
                                .addAlias(Author.AUTHOR.DISTINGUISHED.getName(), "f")
                                .addAlias(Author.AUTHOR.DATE_OF_BIRTH.getName(), "e")
                                .addAlias(Author.AUTHOR.FIRST_NAME.getName(), "c")
                                .addAlias(Author.AUTHOR.LAST_NAME.getName(), "d")
                                .newRecordMapperProvider()))
                .select().from(Author.AUTHOR)
                .fetchInto(SfmMapCustomAuthorVo.class);

        log.info("\n" + sfmMapCustomAuthorVos);

        List<SfmExtendedAuthorVo> sfmExtendedAuthorVos = DSL.using(tempConfig.set(SQLDialect.ORACLE)
                .set(JooqMapperFactory.newInstance().ignorePropertyNotFound()
                        .addAlias(BOOK.ID.getName(), "bookId")
                        .newRecordMapperProvider()))
                .select(AUTHOR.ID, AUTHOR.FIRST_NAME, AUTHOR.LAST_NAME, AUTHOR.DATE_OF_BIRTH,
                        BOOK.ID, BOOK.TITLE)
                .from(AUTHOR).leftJoin(BOOK).on(BOOK.AUTHOR_ID.eq(AUTHOR.ID))
                .orderBy(AUTHOR.ID).fetchInto(SfmExtendedAuthorVo.class);

        log.info("\n" + sfmExtendedAuthorVos);
    }

    @Test
    @DisplayName("Using SimpleFlatMapper libary - join test extended object")
    public void basicSFMJoinTestMe() {
        Configuration tempConfig = dsl.configuration().derive();
        //SelectQueryMapper<SfmExtendedAuthorVo> authorMapper = SelectQueryMapperFactory.newInstance().ignorePropertyNotFound().newMapper(SfmExtendedAuthorVo.class);
        SelectQueryMapper<SfmExtendedAuthorVo> authorMapper = SelectQueryMapperFactory
                .newInstance()
                .addAlias(BOOK.ID.getName(), "bookId")
                .newMapper(SfmExtendedAuthorVo.class);

        List<SfmExtendedAuthorVo> authors = authorMapper.asList(DSL.using(tempConfig)
                .select(AUTHOR.ID, AUTHOR.FIRST_NAME, AUTHOR.LAST_NAME, AUTHOR.DATE_OF_BIRTH,
                        BOOK.ID, BOOK.TITLE)
                .from(AUTHOR).leftJoin(BOOK).on(BOOK.AUTHOR_ID.eq(AUTHOR.ID))
                .orderBy(AUTHOR.ID));

        log.info("\n" + authors);
    }
}



