package com.ecommerce.project.service;

import com.ecommerce.project.model.Category;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService{

    private List<Category> categories = new ArrayList<>();
    private long idValue = 1L;

    @Override
    public List<Category> getAllCategories() {
        return categories;
    }

    @Override
    public String createNewCategory(Category category) {
        category.setCategoryId(idValue++);
        categories.add(category);
        return "Category Details added successfully!";
    }

    @Override
    public String updateCategory(Category category, Long categoryId) {
        Optional<Category> toBeUpdatedCategory = categories.stream()
                .filter(c -> c.getCategoryId().equals(categoryId))
                .findFirst();
        if(toBeUpdatedCategory.isPresent()){
            Category existingCategory = toBeUpdatedCategory.get();
            existingCategory.setCategoryName(category.getCategoryName());
            return "Category updated successfully!";
        }else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Resource not found!");
        }
    }

    @Override
    public String deleteCategory(Long categoryId) {
        Category toBeDeletedCategory = categories.stream()
                .filter(c -> c.getCategoryId().equals(categoryId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Resource not found!"));

            categories.remove(toBeDeletedCategory);
            return "Category with categoryId:"+categoryId+" deleted successfully!";
    }

}
