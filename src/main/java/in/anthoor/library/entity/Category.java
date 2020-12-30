package in.anthoor.library.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("CATEGORY")
public class Category {

    @Id
    @Column("CATEGORY_ID")
    long categoryId;

    @Column("CATEGORY_NAME")
    String categoryName;
}
