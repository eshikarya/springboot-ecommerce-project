package com.ecommerce.project.controllers;

import com.ecommerce.project.service.OrderService;
import com.ecommerce.project.payload.OrderDTO;
import com.ecommerce.project.payload.OrderRequestDTO;
import com.ecommerce.project.utils.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private AuthUtil authUtil;

    @PostMapping("/order/users/payments/{paymentMethod}")
    public ResponseEntity<OrderDTO> placeOrder(@RequestBody OrderRequestDTO orderRequestDTO, @PathVariable String paymentMethod){
        String email = authUtil.loggedInEmail();
        return new ResponseEntity<>(orderService.placeOrder(email,orderRequestDTO,paymentMethod), HttpStatus.CREATED);
    }

}
