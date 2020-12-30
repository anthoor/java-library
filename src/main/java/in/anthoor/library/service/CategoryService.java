package in.anthoor.library.service;

import in.anthoor.library.entity.Category;
import in.anthoor.library.repository.CategoryRepository;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> findAll() {
        return IterableUtils.toList(categoryRepository.findAll());
    }

    public Category findById(long id) {
        return categoryRepository.findById(id).stream().findFirst().orElse(null);
    }

    public List<Category> findByCategoryName(String name) {
        return categoryRepository.findByCategoryName(name);
    }

    public Category save(Category category) {
        return categoryRepository.save(category);
    }
}
