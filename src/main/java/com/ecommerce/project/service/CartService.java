package com.ecommerce.project.service;

import com.ecommerce.project.payload.CartDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import java.util.List;

public interface CartService {
    CartDTO addProductToCart(Long productId, Integer quantity);

    List<CartDTO> getAllCarts();


    String deleteProductFromCart(Long cartId, Long productId);

    CartDTO getUsersCart(String emailId, Long cartId);

    CartDTO updateProductQuantity(Long productId, Integer quantity);

    void updateProductInCarts(Long cartId, Long productId);
}
