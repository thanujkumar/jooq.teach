package jooq.examples.spring.jdbctemplate.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class LanguageVo extends AuditData {
    private int id;
    private String code;
    private String description;
}
