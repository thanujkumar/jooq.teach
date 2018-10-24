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
@Table(name = "BOOK", schema = "JOOQDATA", indexes = {
    @Index(name = "BOOK_PK", unique = true, columnList = "ID ASC")
})
public class Book implements Serializable {

    private static final long serialVersionUID = 1476132172;

    private Integer id;
    private Integer authorId;
    private String  title;
    private Integer publishedIn;
    private Integer languageId;

    public Book() {}

    public Book(Book value) {
        this.id = value.id;
        this.authorId = value.authorId;
        this.title = value.title;
        this.publishedIn = value.publishedIn;
        this.languageId = value.languageId;
    }

    public Book(
        Integer id,
        Integer authorId,
        String  title,
        Integer publishedIn,
        Integer languageId
    ) {
        this.id = id;
        this.authorId = authorId;
        this.title = title;
        this.publishedIn = publishedIn;
        this.languageId = languageId;
    }

    @Id
    @Column(name = "ID", unique = true, nullable = false, precision = 7)
    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "AUTHOR_ID", nullable = false, precision = 7)
    public Integer getAuthorId() {
        return this.authorId;
    }

    public void setAuthorId(Integer authorId) {
        this.authorId = authorId;
    }

    @Column(name = "TITLE", nullable = false, length = 400)
    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Column(name = "PUBLISHED_IN", nullable = false, precision = 7)
    public Integer getPublishedIn() {
        return this.publishedIn;
    }

    public void setPublishedIn(Integer publishedIn) {
        this.publishedIn = publishedIn;
    }

    @Column(name = "LANGUAGE_ID", nullable = false, precision = 7)
    public Integer getLanguageId() {
        return this.languageId;
    }

    public void setLanguageId(Integer languageId) {
        this.languageId = languageId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Book other = (Book) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        }
        else if (!id.equals(other.id))
            return false;
        if (authorId == null) {
            if (other.authorId != null)
                return false;
        }
        else if (!authorId.equals(other.authorId))
            return false;
        if (title == null) {
            if (other.title != null)
                return false;
        }
        else if (!title.equals(other.title))
            return false;
        if (publishedIn == null) {
            if (other.publishedIn != null)
                return false;
        }
        else if (!publishedIn.equals(other.publishedIn))
            return false;
        if (languageId == null) {
            if (other.languageId != null)
                return false;
        }
        else if (!languageId.equals(other.languageId))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
        result = prime * result + ((this.authorId == null) ? 0 : this.authorId.hashCode());
        result = prime * result + ((this.title == null) ? 0 : this.title.hashCode());
        result = prime * result + ((this.publishedIn == null) ? 0 : this.publishedIn.hashCode());
        result = prime * result + ((this.languageId == null) ? 0 : this.languageId.hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Book (");

        sb.append(id);
        sb.append(", ").append(authorId);
        sb.append(", ").append(title);
        sb.append(", ").append(publishedIn);
        sb.append(", ").append(languageId);

        sb.append(")");
        return sb.toString();
    }
}
