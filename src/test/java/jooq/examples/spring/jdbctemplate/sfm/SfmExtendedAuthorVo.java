package jooq.examples.spring.jdbctemplate.sfm;

import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;

@Data
@ToString(callSuper = true)
public class SfmExtendedAuthorVo {
    private int id; //author id
    private String firstName; //author first_name
    private String lastName; //author last_name
    private LocalDate dateOfBirth; // author date_of_birth

    //BOOK
    private String title; //book title
    private int bookId; //book id
}
