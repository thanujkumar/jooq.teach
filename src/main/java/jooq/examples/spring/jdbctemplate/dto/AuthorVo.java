package jooq.examples.spring.jdbctemplate.dto;


import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;

@Data
@ToString (callSuper = true)
public class AuthorVo extends AuditData {
    private int id;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private int yearOfBirth;
    private boolean distinguished;
}
