package in.anthoor.library.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("PUBLISHER")
public class Publisher {
    @Id
    @Column("PUBLISHER_ID")
    long publisherId;

    @Column("PUBLISHER_NAME")
    String publisherName;

    public String toString() {
        StringBuilder builder = new StringBuilder("{");
        if (publisherId > 0) {
            builder.append("\"authorId\":").append(publisherId).append(",");
        }
        if (publisherName != null) {
            builder.append("\"authorName\":").append(publisherName).append(",");
        }
        String out = builder.reverse().substring(1);
        return new StringBuilder(out).reverse().append("}").toString();
    }
}
