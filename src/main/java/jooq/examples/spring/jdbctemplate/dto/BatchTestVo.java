package jooq.examples.spring.jdbctemplate.dto;

import lombok.Data;
import lombok.ToString;

import java.math.BigInteger;

@Data
@ToString(callSuper = true)
public class BatchTestVo extends AuditData {
    private int id;
    private String title;
    private BigInteger age;
}
