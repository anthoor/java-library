package in.anthoor.library.controller;

import in.anthoor.library.entity.Category;
import in.anthoor.library.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CategoryController {
    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping(path = "/category", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Category> findAll() {
        return categoryService.findAll();
    }

    @GetMapping(path = "/category/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Category findById(@PathVariable long id) {
        return categoryService.findById(id);
    }

    @GetMapping(path = "/category/name/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Category> findByCategoryName(@PathVariable String name) {
        return categoryService.findByCategoryName(name);
    }

    @PostMapping(path = "/category", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Category save(@RequestBody Category publisher) {
        return categoryService.save(publisher);
    }
}
