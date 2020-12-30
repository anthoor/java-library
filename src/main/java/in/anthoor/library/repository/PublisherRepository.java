package in.anthoor.library.repository;

import in.anthoor.library.entity.Publisher;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PublisherRepository extends CrudRepository<Publisher, Long> {
    List<Publisher> findByPublisherName(String name);
}
