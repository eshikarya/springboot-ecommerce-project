package com.ecommerce.project.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDTO {

    @Schema(description = "Category Id for a particular category",example = "10")
    private Long categoryId;

    @NotBlank(message = "Category name must not be blank")
    @Size(min = 3, message = "Category name should contain at least 3 characters")
    @Schema(description = "Category name for the category to be created",example = "Fitness")
    private String categoryName;
}
