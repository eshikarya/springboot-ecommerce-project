package com.ecommerce.project.controllers;

import com.ecommerce.project.model.Category;
import com.ecommerce.project.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CategoryController {

//    Field Injection
    @Autowired
    private CategoryService categoryService;

//    Constructor Injection
    /*
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }
     */



    @GetMapping("/public/categories")
    public ResponseEntity<List<Category>> getAllCategories(){
        return new ResponseEntity<>(categoryService.getAllCategories(), HttpStatus.OK);
    }

    @PostMapping("/admin/categories")
    public ResponseEntity<String> createNewCategory(@Valid @RequestBody Category category){
        return new ResponseEntity<>(categoryService.createNewCategory(category), HttpStatus.CREATED);
    }

    @PutMapping("/admin/categories/{categoryId}")
    public ResponseEntity<String> updateCategory(@RequestBody Category category,@PathVariable Long categoryId){
            return new ResponseEntity<>(categoryService.updateCategory(category,categoryId), HttpStatus.OK);
    }

    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long categoryId){
            return new ResponseEntity<>(categoryService.deleteCategory(categoryId), HttpStatus.OK);
    }


}
