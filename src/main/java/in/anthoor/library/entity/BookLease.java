package in.anthoor.library.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.sql.Date;

@Data
@Table("BOOK_LEASE")
public class BookLease {

    @Id
    @Column("BOOK_LEASE_ID")
    long bookLeaseId;

    @MappedCollection(idColumn = "BOOK_ID")
    Book book;

    @Column("LEASED_PERSON")
    String leasedPerson;

    @Column("LEASED_ON")
    Date leasedOn;

    @Column("RETURNED_ON")
    Date returnedOn;
}
