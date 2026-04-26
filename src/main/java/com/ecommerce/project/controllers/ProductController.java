package com.ecommerce.project.controllers;

import com.ecommerce.project.config.AppConstants;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import com.ecommerce.project.service.ProductService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
@Tag(name = "Product APIs",description = "APIs for managing products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping("/public/categories/{categoryId}/products")
    public ResponseEntity<ProductResponse> getProductsByCategory(@PathVariable Long categoryId, @RequestParam(defaultValue = AppConstants.PAGE_NUMBER, required = false, name = "pageNumber") Integer pageNumber, @RequestParam(defaultValue = AppConstants.PAGE_SIZE, required = false, name = "pageSize") Integer pageSize, @RequestParam(defaultValue = AppConstants.SORT_PRODUCTS_BY, required = false, name = "sortBy") String sortBy, @RequestParam(defaultValue = AppConstants.SORT_ORDER, required = false, name = "sortOrder") String sortOrder) {
        return new ResponseEntity(productService.getProductsByCategory(categoryId, pageNumber, pageSize, sortBy, sortOrder), HttpStatus.OK);
    }

    @GetMapping("/public/products/keyword/{keyword}")
    public ResponseEntity<ProductResponse> getProductsByKeyword(@PathVariable String keyword, @RequestParam(defaultValue = AppConstants.PAGE_NUMBER, required = false, name = "pageNumber") Integer pageNumber, @RequestParam(defaultValue = AppConstants.PAGE_SIZE, required = false, name = "pageSize") Integer pageSize, @RequestParam(defaultValue = AppConstants.SORT_PRODUCTS_BY, required = false, name = "sortBy") String sortBy, @RequestParam(defaultValue = AppConstants.SORT_ORDER, required = false, name = "sortOrder") String sortOrder) {
        return new ResponseEntity(productService.getProductsByKeyword(keyword, pageNumber, pageSize, sortBy, sortOrder), HttpStatus.FOUND);
    }

    @GetMapping("/public/products")
    public ResponseEntity<ProductResponse> getAllProducts(@RequestParam(defaultValue = AppConstants.PAGE_NUMBER, required = false, name = "pageNumber") Integer pageNumber, @RequestParam(defaultValue = AppConstants.PAGE_SIZE, required = false, name = "pageSize") Integer pageSize, @RequestParam(defaultValue = AppConstants.SORT_PRODUCTS_BY, required = false, name = "sortBy") String sortBy, @RequestParam(defaultValue = AppConstants.SORT_ORDER, required = false, name = "sortOrder") String sortOrder) {
        return new ResponseEntity<>(productService.getAllProducts(pageNumber, pageSize, sortBy, sortOrder), HttpStatus.OK);
    }
//
//    @GetMapping("/seller/products")
//    public ResponseEntity<ProductResponse> getProductsBySeller(@RequestParam(defaultValue = AppConstants.PAGE_NUMBER, required = false, name = "pageNumber") Integer pageNumber, @RequestParam(defaultValue = AppConstants.PAGE_SIZE, required = false, name = "pageSize") Integer pageSize, @RequestParam(defaultValue = AppConstants.SORT_BY, required = false, name = "sortBy") String sortBy, @RequestParam(defaultValue = AppConstants.SORT_ORDER, required = false, name = "sortOrder") String sortOrder) {
//        return new ResponseEntity<>(productService.getProductsBySeller(), HttpStatus.OK);
//    }
//
//    @GetMapping("/admin/products/count")
//    public ResponseEntity<Long> getProductCount() {
//        return new ResponseEntity<>(productService.getProductCount(), HttpStatus.OK);
//    }

    @DeleteMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> deleteProduct(@PathVariable Long productId) {
        return new ResponseEntity<>(productService.deleteProduct(productId), HttpStatus.OK);
    }

    @PutMapping("/products/{productId}/image")
    public ResponseEntity<ProductDTO> updateProductImage(@PathVariable Long productId, @RequestParam("image") MultipartFile file) throws IOException {
        return new ResponseEntity<>(productService.updateProductImage(productId, file), HttpStatus.OK);
    }

    @PutMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long productId, @Valid @RequestBody ProductDTO productDTO) {
        return new ResponseEntity<>(productService.updateProduct(productId, productDTO), HttpStatus.OK);
    }

    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDTO> addProductToCategory(@PathVariable Long categoryId, @Valid @RequestBody ProductDTO productDTO) {
        return new ResponseEntity<>(productService.addProductToCategory(categoryId, productDTO), HttpStatus.CREATED);
    }
}
