/*
 * This file is generated by jOOQ.
 */
package jooq.examples.generated.tables.pojos;


import java.io.Serializable;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.5"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
@Entity
@Table(name = "LANGUAGE", schema = "JOOQDATA", indexes = {
    @Index(name = "LANGUAGE_PK", unique = true, columnList = "ID ASC")
})
public class Language implements Serializable {

    private static final long serialVersionUID = 1234682668;

    private Integer id;
    private String  cd;
    private String  description;

    public Language() {}

    public Language(Language value) {
        this.id = value.id;
        this.cd = value.cd;
        this.description = value.description;
    }

    public Language(
        Integer id,
        String  cd,
        String  description
    ) {
        this.id = id;
        this.cd = cd;
        this.description = description;
    }

    @Id
    @Column(name = "ID", unique = true, nullable = false, precision = 7)
    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "CD", nullable = false, length = 2)
    public String getCd() {
        return this.cd;
    }

    public void setCd(String cd) {
        this.cd = cd;
    }

    @Column(name = "DESCRIPTION", length = 50)
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Language other = (Language) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        }
        else if (!id.equals(other.id))
            return false;
        if (cd == null) {
            if (other.cd != null)
                return false;
        }
        else if (!cd.equals(other.cd))
            return false;
        if (description == null) {
            if (other.description != null)
                return false;
        }
        else if (!description.equals(other.description))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
        result = prime * result + ((this.cd == null) ? 0 : this.cd.hashCode());
        result = prime * result + ((this.description == null) ? 0 : this.description.hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Language (");

        sb.append(id);
        sb.append(", ").append(cd);
        sb.append(", ").append(description);

        sb.append(")");
        return sb.toString();
    }
}
