package com.tl.hotelproject.repo;

import com.tl.hotelproject.entity.promotion.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromotionRepo extends JpaRepository<Promotion, String> {
    Promotion findBySlug(String slug);
}
