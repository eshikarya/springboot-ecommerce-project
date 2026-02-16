package com.ecommerce.project.service;

import com.ecommerce.project.exception.APIException;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService{

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<Category> getAllCategories() {
        if (categoryRepository.findAll().isEmpty())
            throw new APIException("No categories available !!!!");
        return categoryRepository.findAll();
    }

    @Override
    public String createNewCategory(Category category) {
        Category preExistingCategory = categoryRepository.findByCategoryName(category.getCategoryName());
        if (preExistingCategory!=null)
            throw new APIException("Category with the name "+category.getCategoryName()+" already exists !!!");
        categoryRepository.save(category);
        return "Category Details added successfully!";
    }

    @Override
    public String updateCategory(Category category, Long categoryId) {
        Optional<Category> categories = categoryRepository.findById(categoryId);

        Category savedCategory = categories
                .orElseThrow(()-> new ResourceNotFoundException("Category","categoryId",categoryId));

        category.setCategoryId(categoryId);
        savedCategory = categoryRepository.save(category);
        return "Category updated successfully!";
    }

    @Override
    public String deleteCategory(Long categoryId) {
        Category toBeDeletedCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category","categoryId",categoryId));

            categoryRepository.delete(toBeDeletedCategory);
            return "Category with categoryId:"+categoryId+" deleted successfully!";
    }

}
