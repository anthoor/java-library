package in.anthoor.library.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.util.HashSet;
import java.util.Set;

@Data
@Table("BOOK")
public class Book {

    @Id
    @Column("BOOK_ID")
    long bookId;

    @Column("BOOK_TITLE")
    String bookTitle;

    @Column("BOOK_YEAR")
    int bookYear;

    @Column("BOOK_SHELF")
    int bookShelf;

    @Column("BOOK_ROW")
    int bookRow;

    @Column("BOOK_AVAILABLE")
    boolean bookAvailable;

    @MappedCollection(idColumn = "BOOK_ID")
    Set<BookAuthor> authors = new HashSet<>();

    @MappedCollection(idColumn = "BOOK_ID")
    Set<BookPublisher> publishers = new HashSet<>();

    @MappedCollection(idColumn = "BOOK_ID")
    Set<BookCategory> categories = new HashSet<>();
}
