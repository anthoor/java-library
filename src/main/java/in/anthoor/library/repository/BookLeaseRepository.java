package in.anthoor.library.repository;

import in.anthoor.library.entity.BookLease;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;

@Repository
public interface BookLeaseRepository extends CrudRepository<BookLease, Long> {

    @Modifying
    @Query("INSERT INTO BOOK_LEASE (BOOK_ID, LEASED_PERSON, LEASED_ON) VALUES (:bookId, :leasedPerson, :leasedOn)")
    int save(@Param("bookId") long bookId, @Param("leasedPerson") String leasedPerson, @Param("leasedOn") Date leasedOn);

    @Modifying
    @Query("UPDATE BOOK_LEASE SET RETURNED_ON = :date WHERE BOOK_LEASE_ID = :id")
    int returnBook(@Param("id") long id,@Param("date") Date date);
}
