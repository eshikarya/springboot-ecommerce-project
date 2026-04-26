package com.ecommerce.project.service;

import com.ecommerce.project.exception.APIException;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.model.*;
import com.ecommerce.project.payload.OrderDTO;
import com.ecommerce.project.payload.OrderItemDTO;
import com.ecommerce.project.payload.OrderRequestDTO;
import com.ecommerce.project.repositories.*;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private AddressRepository addressesRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartService cartService;


    @Autowired
    private ModelMapper modelMapper;

    @Override
    @Transactional
    public OrderDTO placeOrder(String email, OrderRequestDTO orderRequestDTO, String paymentMethod) {

        Cart userCart = cartRepository.findCartByEmail(email);

        if (userCart == null) throw new ResourceNotFoundException("Cart", "email", email);

        Address address = addressesRepository.findById(orderRequestDTO.getAddressId()).orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", orderRequestDTO.getAddressId()));

        Order order = new Order();

        order.setOrderDate(LocalDate.now());
        order.setEmail(email);
        order.setOrderStatus("Order accepted!");
        order.setTotalAmount(userCart.getTotalPrice());
        order.setAddress(address);

        Payment payment = new Payment(paymentMethod, orderRequestDTO.getPgPaymentId(), orderRequestDTO.getPgStatus(), orderRequestDTO.getPgResponseMessage(), orderRequestDTO.getPgName());
        payment.setOrder(order);
        payment = paymentRepository.save(payment);
        order.setPayment(payment);

        Order savedOrder = orderRepository.save(order);

        List<CartItem> cartItemsList = userCart.getCartItems();

        if (cartItemsList.isEmpty()) {
            throw new APIException("Cart is empty!");
        }

        List<OrderItem> orderItemList = new ArrayList<>();

        for (CartItem cartItem : cartItemsList) {
            OrderItem orderItem = new OrderItem();

            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setDiscount(cartItem.getDiscount());
            orderItem.setOrderedProductPrice(cartItem.getProductPrice());
            orderItem.setOrder(savedOrder);
            orderItemList.add(orderItem);
        }

        orderItemList = orderItemRepository.saveAll(orderItemList);

        userCart.getCartItems().forEach(item -> {
            int cartQty = item.getQuantity();
            Product product = item.getProduct();
            int stockQty = product.getQuantity();

            if (cartQty > stockQty) {
                throw new APIException("Only " + stockQty + " units of " + product.getProductName() + " in stock!!!");
            }

            product.setQuantity(stockQty - cartQty);
            productRepository.save(product);

            cartService.deleteProductFromCart(userCart.getCartId(), item.getProduct().getProductId());
        });

        OrderDTO orderDTO = modelMapper.map(savedOrder, OrderDTO.class);

        orderItemList.forEach(item -> orderDTO.getOrderItemsList().add(modelMapper.map(item, OrderItemDTO.class)));
        orderDTO.setAddressId(address.getAddressId());

        return orderDTO;
    }
}
