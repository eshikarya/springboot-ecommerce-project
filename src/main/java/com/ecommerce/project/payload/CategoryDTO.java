package com.ecommerce.project.payload;


//DTO is an important concept ---- Read on it before interviews
//data transfer object is used to carry data bw different software systems w/o
//modifying the entity classes

// used to encapsulate and transfer data related to categories from client to server (Request)
// representing Category at the presentation layer

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
    private Long categoryId;
    private String categoryName;
}
