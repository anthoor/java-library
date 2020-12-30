package in.anthoor.library.controller;

import in.anthoor.library.entity.Publisher;
import in.anthoor.library.service.PublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PublisherController {

    private final PublisherService publisherService;

    @Autowired
    public PublisherController(PublisherService publisherService) {
        this.publisherService = publisherService;
    }

    @GetMapping(path = "/publisher", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Publisher> findAll() {
        return publisherService.findAll();
    }

    @GetMapping(path = "/publisher/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Publisher findById(@PathVariable long id) {
        return publisherService.findById(id);
    }

    @GetMapping(path = "/publisher/name/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Publisher> findByPublisherName(@PathVariable String name) {
        return publisherService.findByPublisherName(name);
    }

    @PostMapping(path = "/publisher", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Publisher save(@RequestBody Publisher publisher) {
        return publisherService.save(publisher);
    }
}
