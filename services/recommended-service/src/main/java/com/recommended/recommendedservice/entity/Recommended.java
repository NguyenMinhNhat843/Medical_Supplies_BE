package com.recommended.recommendedservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Entity
@Table(name = "recommended")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Recommended {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "product_id")
    private Long productId;
    
    @Column(name = "interaction_type")
    private String interactionType; // VIEW, PURCHASE, CART, WISHLIST, REVIEW
    
    @Column(name = "interaction_value")
    private Integer interactionValue; // Có thể là rating hoặc số lượng mua
    
    @CreationTimestamp
    @Column(name = "created_at")
    private Date createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;
}
