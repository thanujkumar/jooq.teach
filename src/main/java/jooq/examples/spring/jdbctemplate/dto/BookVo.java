package jooq.examples.spring.jdbctemplate.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class BookVo extends AuditData {
    private int id;
    private int authorId;
    private String title;
    private int publishedIn;
    private int languageId;
}
