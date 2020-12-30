package in.anthoor.library.entity;

import lombok.Data;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("BOOK_AUTHOR")
public class BookAuthor {
    @Column("AUTHOR_ID")
    long authorId;
}
