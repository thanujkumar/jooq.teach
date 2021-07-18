package jooq.examples.spring.jdbctemplate.dto;

import lombok.Data;
import lombok.ToString;


@Data
@ToString(callSuper = true)
public class BookStoreVo extends AuditData {
    private String name;
}
