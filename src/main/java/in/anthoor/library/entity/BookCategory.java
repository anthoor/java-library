package in.anthoor.library.entity;

import lombok.Data;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("BOOK_CATEGORY")
public class BookCategory {

    @Column("CATEGORY_ID")
    long categoryId;
}
