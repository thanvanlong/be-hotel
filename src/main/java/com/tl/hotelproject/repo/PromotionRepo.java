package com.tl.hotelproject.repo;

import com.tl.hotelproject.entity.promotion.Promotion;
import com.tl.hotelproject.entity.services.Services;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface PromotionRepo extends JpaRepository<Promotion, String> {
    Promotion findBySlug(String slug);

    Promotion findAllByStartDateIsBeforeAndEndDateIsAfter(LocalDate start, LocalDate end);

    Page<Promotion> findByNameContaining(String search, Pageable pageable);

    @Query("select p from Promotion p where :start between p.startDate and p.endDate")
    Promotion findByDate(LocalDate start);

    @Query("select p from Promotion p where (:start between p.startDate and p.endDate) and p.id != :id")
    Promotion findByDateNotId(LocalDate start, String id);
}
