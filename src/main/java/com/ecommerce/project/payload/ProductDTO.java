package com.ecommerce.project.payload;

import com.ecommerce.project.model.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Long productId;

    @NotBlank(message = "Category name must not be blank")
    @Size(min = 3, message = "Product name must contain at least 3 characters")
    private String productName;

    @NotBlank(message = "Category name must not be blank")
    @Size(min = 6, message = "Product description must contain at least 6 characters")
    private String description;

    private String image;
    private Integer quantity;
    private double price;
    private double discount;
    private Double specialPrice;
    private Category category;
}
