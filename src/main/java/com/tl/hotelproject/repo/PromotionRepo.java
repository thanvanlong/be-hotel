package com.tl.hotelproject.repo;

import com.tl.hotelproject.entity.promotion.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface PromotionRepo extends JpaRepository<Promotion, String> {
    Promotion findBySlug(String slug);

    Promotion findAllByStartDateIsBeforeAndEndDateIsAfter(LocalDate start, LocalDate end);
}
