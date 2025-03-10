package com.ecommerce.project.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// for response from server to client, instead of directly modifying the category model class --- this is sent as response.
// category model class should be responsible for any changes that happen in the backend
// it shouldn't be modified based on changes from Frontend --- therefore decoupling needed


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponse {
    private List<CategoryDTO> content;
}
