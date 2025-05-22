package com.cart.cartservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    // ✅ Constructor cần thiết cho new Cart(userId)
    public Cart(Long userId) {
        this.userId = userId;
    }

}
