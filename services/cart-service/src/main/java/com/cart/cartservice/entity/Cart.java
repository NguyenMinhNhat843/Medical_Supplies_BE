package com.cart.cartservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    // ✅ Constructor cần thiết cho new Cart(userId)
    public Cart(Long userId) {
        this.userId = userId;
    }

}
