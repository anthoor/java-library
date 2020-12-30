package in.anthoor.library.service;

import in.anthoor.library.entity.Publisher;
import in.anthoor.library.repository.PublisherRepository;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PublisherService {

    private final PublisherRepository publisherRepository;

    @Autowired
    public PublisherService(PublisherRepository publisherRepository) {
        this.publisherRepository = publisherRepository;
    }

    public List<Publisher> findAll() {
        return IterableUtils.toList(publisherRepository.findAll());
    }

    public Publisher findById(long id) {
        return publisherRepository.findById(id).stream().findFirst().orElse(null);
    }

    public List<Publisher> findByPublisherName(String name) {
        return publisherRepository.findByPublisherName(name);
    }

    public Publisher save(Publisher publisher) {
        return publisherRepository.save(publisher);
    }
}
