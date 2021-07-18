package jooq.examples.spring.jdbctemplate.sfm;

import jooq.examples.spring.jdbctemplate.dto.AuditData;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;

//Class should be public for sfm
@Data
@ToString(callSuper = true)
public class SfmPartialCustomAuthorVo extends AuditData {
    private int id;
    private String _firstName;
    private boolean distinguished;
}

