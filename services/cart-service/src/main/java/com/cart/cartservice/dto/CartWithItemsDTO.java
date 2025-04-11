package com.cart.cartservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartWithItemsDTO {
    private Long id;
    private Long userId;
    private List<CartItemDetailDTO> items;
}

