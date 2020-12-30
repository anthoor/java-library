package in.anthoor.library.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("AUTHOR")
public class Author {
    @Id
    @Column("AUTHOR_ID")
    long authorId;

    @Column("AUTHOR_NAME")
    String authorName;

    public String toString() {
        StringBuilder builder = new StringBuilder("{");
        if (authorId > 0) {
            builder.append("\"authorId\":").append(authorId).append(",");
        }
        if (authorName != null) {
            builder.append("\"authorName\":").append(authorName).append(",");
        }
        String out = builder.reverse().substring(1);
        return new StringBuilder(out).reverse().append("}").toString();
    }
}
