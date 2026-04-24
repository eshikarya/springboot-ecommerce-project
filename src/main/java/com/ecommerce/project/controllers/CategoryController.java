package com.ecommerce.project.controllers;

import com.ecommerce.project.config.AppConstants;
import com.ecommerce.project.payload.CategoryDTO;
import com.ecommerce.project.payload.CategoryResponse;
import com.ecommerce.project.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Tag(name = "Category APIs", description = "APIs for managing categories")
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
    @Operation(summary = "Get all categories", description = "API to get all categories existing on the ecommerce website")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "Category response received successfully"),
            @ApiResponse(responseCode = "400",description = "Invalid Input",content = @Content),
            @ApiResponse(responseCode = "500",description = "Internal Server Error",content = @Content),
    })
    public ResponseEntity<CategoryResponse> getAllCategories(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_CATEGORIES_BY, required = false) String sortBy,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.SORT_ORDER, required = false) String sortOrder) {
        return new ResponseEntity<>(categoryService.getAllCategories(pageNumber, pageSize, sortBy, sortOrder), HttpStatus.OK);
    }

    @PostMapping("/admin/categories")
    @Operation(summary = "Create a new Category", description = "API to create new category on the ecommerce website")
    @ApiResponses({
            @ApiResponse(responseCode = "201",description = "Category is created successfully"),
            @ApiResponse(responseCode = "400",description = "Invalid Input",content = @Content),
            @ApiResponse(responseCode = "500",description = "Internal Server Error",content = @Content),
    })
    public ResponseEntity<CategoryDTO> createNewCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        return new ResponseEntity<>(categoryService.createNewCategory(categoryDTO), HttpStatus.CREATED);
    }

    @PutMapping("/admin/categories/{categoryId}")
    @Operation(summary = "Update a categories", description = "API to update a category on the ecommerce website")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "Category is updated successfully"),
            @ApiResponse(responseCode = "400",description = "Invalid Input",content = @Content),
            @ApiResponse(responseCode = "500",description = "Internal Server Error",content = @Content),
    })
    public ResponseEntity<CategoryDTO> updateCategory(@RequestBody CategoryDTO categoryDTO, @Parameter(description = "ID of the category to be updated") @PathVariable Long categoryId) {
        return new ResponseEntity<>(categoryService.updateCategory(categoryDTO, categoryId), HttpStatus.OK);
    }

    @DeleteMapping("/admin/categories/{categoryId}")
    @Operation(summary = "Delete categories", description = "API to delete a category on the ecommerce website")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "Category is deleted successfully"),
            @ApiResponse(responseCode = "400",description = "Invalid Input",content = @Content),
            @ApiResponse(responseCode = "500",description = "Internal Server Error",content = @Content),
    })
    public ResponseEntity<CategoryDTO> deleteCategory(@Parameter(description = "ID of the Category to be deleted") @PathVariable Long categoryId) {
        return new ResponseEntity<>(categoryService.deleteCategory(categoryId), HttpStatus.OK);
    }


}
