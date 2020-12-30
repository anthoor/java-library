package in.anthoor.library.controller;

import in.anthoor.library.entity.Author;
import in.anthoor.library.service.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AuthorController {

    private final AuthorService authorService;

    @Autowired
    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping(path = "/author", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Author> findAll() {
        return authorService.findAll();
    }

    @GetMapping(path = "/author/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Author findById(@PathVariable long id) {
        return authorService.findById(id);
    }

    @GetMapping(path = "/author/name/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Author> findByAuthorName(@PathVariable String name) {
        return authorService.findByAuthorName(name);
    }

    @PostMapping(path = "/author", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Author create(@RequestBody Author author) {
        return authorService.save(author);
    }
}
