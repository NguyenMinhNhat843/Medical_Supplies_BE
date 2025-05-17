package com.cart.cartservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDetailDTO {
    private Long id;
    private Long cartId;
    private int quantity;
    private ProductDTO product;
}

