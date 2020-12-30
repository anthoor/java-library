package in.anthoor.library.controller;

import com.fasterxml.jackson.databind.JsonNode;
import in.anthoor.library.entity.BookLease;
import in.anthoor.library.service.BookLeaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BookLeaseController {

    private final BookLeaseService bookLeaseService;

    @Autowired
    public BookLeaseController(BookLeaseService bookLeaseService) {
        this.bookLeaseService = bookLeaseService;
    }

    @GetMapping(path = "/lease", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<BookLease> findAll() {
        return bookLeaseService.findAll();
    }

    @GetMapping(path = "/lease/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public BookLease findById(@PathVariable long id) {
        return bookLeaseService.findById(id);
    }

    @PostMapping(path = "/lease", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public int save(@RequestBody BookLease bookLease) {
        return bookLeaseService.save(bookLease);
    }

    @PutMapping(path = "/lease/{id}/book/{bookId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public int returnBook(@PathVariable long id, @PathVariable long bookId) {
        return bookLeaseService.update(id, bookId);
    }

    @GetMapping(path = "/lease/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public JsonNode getAllDetails() {
        return bookLeaseService.getAllDetails();
    }
}
