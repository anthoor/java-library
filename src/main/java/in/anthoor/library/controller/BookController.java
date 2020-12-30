package in.anthoor.library.controller;

import com.fasterxml.jackson.databind.JsonNode;
import in.anthoor.library.entity.Book;
import in.anthoor.library.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BookController {
    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping(path = "/book", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Book> findAll() {
        return bookService.findAll();
    }

    @GetMapping(path = "/book/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public JsonNode getAllDetails() {
        return bookService.getAllDetails();
    }

    @GetMapping(path = "/book/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Book findById(@PathVariable long id) {
        return bookService.findById(id);
    }

    @GetMapping(path = "/book/title/{title}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Book> findByBookTitle(@PathVariable String title) {
        return bookService.findByBookTitle(title);
    }

    @PostMapping(path = "/book", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Book save(@RequestBody Book book) {
        return bookService.save(book);
    }
}
