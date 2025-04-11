package com.cart.cartservice.dto;

import com.cart.cartservice.entity.Cart;
import java.util.List;

public class CartWithItems {
    private Cart cart;
    private List<CartItemDTO> items;

    public CartWithItems() {}

    public CartWithItems(Cart cart, List<CartItemDTO> items) {
        this.cart = cart;
        this.items = items;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public List<CartItemDTO> getItems() {
        return items;
    }

    public void setItems(List<CartItemDTO> items) {
        this.items = items;
    }
}

