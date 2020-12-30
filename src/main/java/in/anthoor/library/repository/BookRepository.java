package in.anthoor.library.repository;

import in.anthoor.library.entity.Book;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends CrudRepository<Book, Long> {
    List<Book> findByBookTitle(String title);

    @Modifying
    @Query("UPDATE BOOK SET BOOK_AVAILABLE = :isAvailable WHERE BOOK_ID = :id")
    int updateAvailability(@Param("id") long id, @Param("isAvailable") int isAvailable);
}
