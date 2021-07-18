package jooq.examples.spring.jdbctemplate;

import jooq.examples.generated.tables.Author;
import jooq.examples.generated.tables.records.AuthorRecord;
import jooq.examples.spring.jdbctemplate.dto.AuditData;
import jooq.examples.spring.jdbctemplate.dto.AuthorVo;
import lombok.Data;
import lombok.ToString;
import org.jooq.DSLContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

//https://www.jooq.org/doc/latest/manual/sql-execution/fetching/recordmapper/
@ExtendWith(SpringExtension.class)
@ContextConfiguration("classpath:jooq-spring-jdbc-template.xml")
public class UsingRecordMapperTest {
    private static final Logger log = LoggerFactory.getLogger(UsingRecordMapperTest.class);

    @Autowired
    DSLContext dsl;

    @Autowired
    ApplicationContext context;

    @Test
    @DisplayName("Using RecordMapper for custom mapping (similar to JdbcTemplate)")
    public void usingRecordHandlerTestMe() {

        List<AuthorVo> authorVoList = dsl.selectFrom(Author.AUTHOR).orderBy(2).fetch().map(record -> {
            //Already AuthorRecord created, isn't it too many object conversion here?
            AuthorVo author = new AuthorVo();
            author.setId(record.getId());
            author.setFirstName(record.getFirstName());
            author.setLastName(record.getLastName());
            author.setDateOfBirth(record.getDateOfBirth());
            author.setDistinguished(Optional.ofNullable(record.getDistinguished()).orElse((byte) 0) == 0 ? false : true);
            author.setCreatedBy(record.getCreatedBy());
            author.setCreatedTS(record.getCreatedTs());
            author.setModifiedBy(record.getModifiedBy());
            author.setModifiedTS(record.getModifiedTs());
            return author;
        });

        log.info("\n" + authorVoList.toString());

        List<String> names = dsl.selectFrom(Author.AUTHOR).orderBy(2).fetch(AuthorRecord::getFirstName);
        log.info("\n" + names.toString());

    }
}


