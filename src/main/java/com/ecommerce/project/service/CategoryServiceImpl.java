package com.ecommerce.project.service;

import com.ecommerce.project.model.Category;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class CategoryServiceImpl implements CategoryService{

    private List<Category> categoryList = new ArrayList<>();

    private long nextId = 1L;

    @Override
    public List<Category> getAllCategories() {
        return categoryList;
    }

    @Override
    public void createCategory(Category category) {
        category.setCategoryId(nextId++);
        categoryList.add(category);
    }

    @Override
    public String deleteCategory(Long categoryId) {
        Category category = categoryList.stream()
                .filter(c -> c.getCategoryId() == (categoryId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Resource not found"));

        categoryList.remove(category);
        return "Category with categoryId "+categoryId + " deleted successfully";
    }

    @Override
    public Category updateCategory(Category category, Long categoryId) {
        Category categoryElement = categoryList.stream()
                .filter(c -> c.getCategoryId() == categoryId)
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,"Resource not found"));

        categoryElement.setCategoryName(category.getCategoryName());
        return categoryElement;
    }
}
