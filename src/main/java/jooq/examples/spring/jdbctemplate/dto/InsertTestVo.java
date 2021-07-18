package jooq.examples.spring.jdbctemplate.dto;

import lombok.Data;
import lombok.ToString;

import java.math.BigInteger;

@Data
@ToString(callSuper = true)
public class InsertTestVo extends AuditData {
    private int id;
    private String name;
    private BigInteger age;
    private String title;
}
