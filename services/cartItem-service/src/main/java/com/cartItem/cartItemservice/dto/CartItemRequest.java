package com.cartItem.cartItemservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemRequest {
    private Long cartId;
    private Long productId;
    private int quantity;
    // getters & setters
}

