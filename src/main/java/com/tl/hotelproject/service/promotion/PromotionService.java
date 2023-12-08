package com.tl.hotelproject.service.promotion;

import com.tl.hotelproject.entity.promotion.Promotion;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.Map;

@Service
public interface PromotionService {
    String save(Promotion promotion) throws Exception;
    String update(Promotion promotion) throws Exception;

    Promotion getById(String id) throws Exception;

    Promotion getBySlug(String slug);

    Map<String, Object> pagingSort(int page, int limit);
    Map<String, Object> search(int page, int limit, String search);

    Promotion getPromotionByStartDateAndEndDate();

    Boolean checkPromotionByStartDateAndEndDate(LocalDate date, String id);
}
