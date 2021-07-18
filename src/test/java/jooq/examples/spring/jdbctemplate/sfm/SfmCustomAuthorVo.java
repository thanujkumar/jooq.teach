package jooq.examples.spring.jdbctemplate.sfm;

import jooq.examples.spring.jdbctemplate.dto.AuditData;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;

//Class should be public for sfm
@Data
@ToString(callSuper = true)
public class SfmCustomAuthorVo extends AuditData {
    private int yearOfBirth;
    private int _id;
    private int id;
    private String _firstName;
    private String _lastName;
    private LocalDate _dateOfBirth;
    private boolean distinguished;
}

