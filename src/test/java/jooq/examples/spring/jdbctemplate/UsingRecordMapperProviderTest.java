package jooq.examples.spring.jdbctemplate;

import jooq.examples.generated.tables.Book;
import jooq.examples.generated.tables.records.AuthorRecord;
import jooq.examples.spring.jdbctemplate.dto.AuditData;
import jooq.examples.spring.jdbctemplate.dto.AuthorVo;
import lombok.Data;
import lombok.ToString;
import org.jooq.Record;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultRecordMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static jooq.examples.generated.Tables.AUTHOR;

//https://www.jooq.org/doc/latest/manual/sql-execution/fetching/pojos-with-recordmapper-provider/
@ExtendWith(SpringExtension.class)
@ContextConfiguration("classpath:jooq-spring-jdbc-template.xml")
@TestMethodOrder(MethodOrderer.Random.class)
public class UsingRecordMapperProviderTest {
    private static final Logger log = LoggerFactory.getLogger(UsingRecordMapperProviderTest.class);

    @Autowired
    DSLContext dsl;

    @Autowired
    ApplicationContext context;

    @Test
    @DisplayName("pure jdbc mapping - AuthorVo ")
    @Order(4)
    public void pureJdbcMappingTestMe() throws SQLException {
        Connection connection = dsl.configuration().connectionProvider().acquire();
        PreparedStatement preparedStatement = connection.prepareStatement("select id, first_name, last_name, " +
                "date_of_birth, year_of_birth, distinguished, created_by, created_ts, modified_by, modified_ts, version from author");
        ResultSet resultSet = preparedStatement.executeQuery();
        List<AuthorVo> authorVoList = new ArrayList<>();
        while (resultSet.next()) {
            AuthorVo vo = new AuthorVo();
            vo.setId(resultSet.getInt(1));
            vo.setFirstName(resultSet.getString(2));
            vo.setLastName(resultSet.getString(3));
            vo.setDateOfBirth(resultSet.getDate(4).toLocalDate());
            vo.setYearOfBirth(resultSet.getInt(5));
            vo.setDistinguished(Optional.ofNullable(resultSet.getByte(6)).orElse((byte) 0) == 0 ? false : true);
            vo.setCreatedBy(resultSet.getString(7));
            vo.setCreatedTS(resultSet.getTimestamp(8).toLocalDateTime());
            vo.setModifiedBy(resultSet.getString(9));
            Timestamp timestamp = Optional.ofNullable(resultSet.getTimestamp(10)).orElse(null);
            LocalDateTime localDateTime = null;
            if (timestamp != null) {
                localDateTime = timestamp.toLocalDateTime();
            }
            vo.setModifiedTS(localDateTime);
            vo.setVersion(resultSet.getLong(11));
            authorVoList.add(vo);
        }
        dsl.configuration().connectionProvider().release(connection);
        log.info("\n" + authorVoList);
    }


    @Test
    @DisplayName("DefaultRecord Mapper - AuthorRecord ")
    @Order(1)
    public void nomralJooqRecordTestMe() {
        Result result = dsl.select().from(AUTHOR).fetch();
        log.info("\n" + result.toString());
    }

    @Test
    @DisplayName("DefaultRecord Mapper mapping to custom object (poor perf) that matches AuthorRecord fields (uses reflection)")
    @Order(2)
    public void nomralJooqFetchIntoTestMe() {
        //POOR performance as reflection is used to map to AuthorVO (only populates for matching columns)
        List<AuthorVo> result = dsl.select().from(AUTHOR).fetchInto(AuthorVo.class);
        log.info("\n" + result.toString());
    }


    @Test
    @DisplayName("POJOs with RecordMapperProvider - AuthorVo ")
    @Order(6)
    public void usingRecordHandlerTestMe() {
        //Create a copy so that original settings is not modified that is configured in xml
        Configuration tempConfig = dsl.configuration().derive();

        List<CustomAuthorVo> result = DSL.using(tempConfig
                .set(SQLDialect.ORACLE)
                .set(new RecordMapperProvider() {
                         @Override
                         public <R extends Record, E> RecordMapper<R, E> provide(RecordType<R> recordType, Class<? extends E> type) {
                             log.info("Type to convert +++++++++++++++++" + type);
                             log.info("RecordType to be converted ++++++" + recordType.getClass());
                             //Custom mapping of the record to CustomAuthorVo
                             if (type == CustomAuthorVo.class) {
                                 return record -> {
                                     //Record fetching is AuthorRecord
                                     log.info("Got Mapped Record ++++++" + record.getClass());
                                     AuthorRecord rec = (AuthorRecord) record;
                                     CustomAuthorVo author = new CustomAuthorVo();
                                     author.set_id(rec.getId());
                                     author.set_firstName(rec.getFirstName());
                                     author.set_lastName(rec.getLastName());
                                     author.set_dateOfBirth(rec.getDateOfBirth());
                                     author.set_distinguished(Optional.ofNullable(rec.getDistinguished()).orElse((byte) 0) == 0 ? false : true);
                                     author.setCreatedBy(rec.getCreatedBy());
                                     author.setCreatedTS(rec.getCreatedTs());
                                     author.setModifiedBy(rec.getModifiedBy());
                                     author.setModifiedTS(rec.getModifiedTs());
                                     return (E) author;
                                 };
                             }

                             // Books might be joined with their authors, create a 1:1 mapping
                             if (type == Book.class) {
                                 //TODO
                             }

                             // Fall back to jOOQ's DefaultRecordMapper, which maps records onto POJOs using reflection.
                             return new DefaultRecordMapper(recordType, type);
                         }
                     }
                ))
                .selectFrom(AUTHOR)
                .orderBy(AUTHOR.ID)
                .fetchInto(CustomAuthorVo.class);

        log.info("\n" + result.toString());
    }

    @Test
    @DisplayName("POJOs with RecordMapperProvider - CustomAuthorVo")
    @Order(5)
    public void usingRecordHandler2TestMe() {
        //Create a copy so that original settings is not modified that is configured in xml
        Configuration tempConfig = dsl.configuration().derive();

        List<CustomAuthorVo> result = DSL.using(tempConfig
                .set(SQLDialect.ORACLE)
                .set(new RecordMapperProvider() {
                         @Override
                         public <R extends Record, E> RecordMapper<R, E> provide(RecordType<R> recordType, Class<? extends E> type) {
                             log.info("Type to convert +++++++++++++++++" + type);
                             log.info("RecordType to be converted ++++++" + recordType.getClass());
                             //Custom mapping of the record to AuthorVo
                             if (type == CustomAuthorVo.class) {
                                 return new RecordMapper<R, E>() {
                                     @Override
                                     public E map(R record) {
                                         CustomAuthorVo _cusVo = new CustomAuthorVo();
                                         _cusVo.set_id(record.get(AUTHOR.ID));
                                         _cusVo.set_firstName(record.get(AUTHOR.FIRST_NAME));
                                         _cusVo.set_lastName(record.get(AUTHOR.LAST_NAME));
                                         _cusVo.set_distinguished(Optional.ofNullable(record.get(AUTHOR.DISTINGUISHED)).orElse((byte) 0) == 0 ? false : true);
                                         _cusVo.setCreatedBy(record.get(AUTHOR.MODIFIED_BY));
                                         _cusVo.setCreatedTS(record.get(AUTHOR.CREATED_TS));
                                         _cusVo.setModifiedBy(record.get(AUTHOR.MODIFIED_BY));
                                         _cusVo.setModifiedTS(record.get(AUTHOR.MODIFIED_TS));
                                         return (E) _cusVo;
                                     }
                                 };
                             }

                             // Books might be joined with their authors, create a 1:1 mapping
                             if (type == Book.class) {
                                 //TODO
                             }

                             // Fall back to jOOQ's DefaultRecordMapper, which maps records onto POJOs using reflection.
                             return new DefaultRecordMapper(recordType, type);
                         }
                     }
                ))
                .selectFrom(AUTHOR)
                .orderBy(AUTHOR.ID)
                .fetchInto(CustomAuthorVo.class);

        log.info("\n" + result.toString());

    }
}


@Data
@ToString(callSuper = true)
class CustomAuthorVo extends AuditData {
    private int _id;
    private String _firstName;
    private String _lastName;
    private LocalDate _dateOfBirth;
    private int _yearOfBirth;
    private boolean _distinguished;
}

