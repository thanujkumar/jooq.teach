package jooq.examples.spring.jdbctemplate.sfm;

import jooq.examples.spring.jdbctemplate.dto.AuditData;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;

//Class should be public for sfm, here datatypes are invalid
@Data
@ToString(callSuper = true)
public class SfmInvalidCustomAuthorVo extends AuditData {
    private String yearOfBirth;
    private String _id;
    private String _firstName;
    private String _lastName;
    private String a_dateOfBirth;
    private String distinguished;
}
