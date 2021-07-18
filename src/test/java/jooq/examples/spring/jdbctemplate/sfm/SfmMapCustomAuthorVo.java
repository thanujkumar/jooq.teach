package jooq.examples.spring.jdbctemplate.sfm;

import jooq.examples.spring.jdbctemplate.dto.AuditData;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;

//Class should be public for sfm
@Data
@ToString(callSuper = true)
public class SfmMapCustomAuthorVo extends AuditData {
    private int a;
    private int b;
    private String c;
    private String d;
    private LocalDate e;
    private boolean f;
}

