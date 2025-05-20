package com.recommended.recommendedservice.repository;

import com.recommended.recommendedservice.entity.Recommended;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendedRepository extends JpaRepository<Recommended, Long> {

    List<Recommended> findByUserId(Long userId);

    List<Recommended> findByProductId(Long productId);

    List<Recommended> findByUserIdAndInteractionType(Long userId, String interactionType);

    @Query("SELECT r.productId FROM Recommended r WHERE r.userId = :userId AND r.interactionType = :interactionType")
    List<Long> findProductIdsByUserIdAndInteractionType(@Param("userId") Long userId, @Param("interactionType") String interactionType);

    @Query("SELECT r.productId, COUNT(r) as count FROM Recommended r WHERE r.interactionType = :interactionType GROUP BY r.productId ORDER BY count DESC")
    List<Object[]> findMostPopularProductsByInteractionType(@Param("interactionType") String interactionType);
}
