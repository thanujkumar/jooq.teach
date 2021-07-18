package jooq.examples.spring.jdbctemplate.dto;

import lombok.Data;
import lombok.ToString;

import java.math.BigInteger;

@Data
@ToString(callSuper = true)
public class BookToBookStoreVo extends AuditData {
    private String name;
    private BigInteger bookId;
    private BigInteger stock;
}
