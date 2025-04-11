package com.review.reviewservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CurrentTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

@Entity
@Table(name = "review")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReviewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "productid")
    private Long productId;

    @Column(name = "customerid")
    private Long customerId;

    @Column(name = "reviewtext")
    private String reviewText;

    @Column(name = "rating")
    private Long rating;

    @Column(name = "reviewdate")
    @CurrentTimestamp
    private Date reviewDate;

    @Column(name = "createat")
    @CurrentTimestamp
    private Date createAt;

    @Column(name = "updateat")
    @UpdateTimestamp
    private Date updateAt;


}
