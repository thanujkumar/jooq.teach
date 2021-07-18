package jooq.examples.spring.jdbctemplate.dto;

import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@ToString
public abstract class AuditData {
    private String createdBy;
    private LocalDateTime createdTS;
    private String modifiedBy;
    private LocalDateTime modifiedTS;
    private long version;
}
