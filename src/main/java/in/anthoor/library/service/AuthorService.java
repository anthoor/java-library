package in.anthoor.library.service;

import in.anthoor.library.entity.Author;
import in.anthoor.library.repository.AuthorRepository;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorService {

    private final AuthorRepository authorRepository;

    @Autowired
    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public List<Author> findAll() {
        return IterableUtils.toList(authorRepository.findAll());
    }

    public Author findById(long id) {
        return authorRepository.findById(id).stream().findFirst().orElse(null);
    }

    public List<Author> findByAuthorName(String name) {
        return authorRepository.findByAuthorName(name);
    }

    public Author save(Author author) {
        return authorRepository.save(author);
    }
}
