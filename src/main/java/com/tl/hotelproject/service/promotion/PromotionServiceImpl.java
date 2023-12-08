package com.tl.hotelproject.service.promotion;

import com.tl.hotelproject.entity.Metadata;
import com.tl.hotelproject.entity.promotion.Promotion;
import com.tl.hotelproject.entity.room.Room;
import com.tl.hotelproject.repo.PromotionRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService{
    @Autowired
    private PromotionRepo promotionRepo;

    @Override
    public String save(Promotion promotion) throws Exception {
        promotionRepo.save(promotion);
        return "Da them thanh cong";
    }

    @Override
    public String update(Promotion promotion) throws Exception {
        promotionRepo.save(promotion);
        return "Da cap nhat thanh cong";
    }

    @Override
    public Promotion getById(String id) throws Exception {
        Optional<Promotion> promotion = promotionRepo.findById(id);

        if(promotion.isPresent()) return promotion.get();
        throw new Exception("Khong ton tai promotion");
    }

    @Override
    public Promotion getBySlug(String slug) {
        return promotionRepo.findBySlug(slug);
    }

    @Override
    public Map<String, Object> pagingSort(int page, int limit) {
        Pageable pagingSort = PageRequest.of(page, limit);
        Page<Promotion> promotionPage = promotionRepo.findAll(pagingSort);

        Metadata metadata = new Metadata();
        metadata.setPageNumber(promotionPage.getNumber());
        metadata.setPageSize(promotionPage.getSize());
        metadata.setTotalPages(promotionPage.getTotalPages());
        metadata.setTotalItems(promotionPage.getTotalElements());

        Map<String, Object> response = new HashMap<>();
        response.put("results", promotionPage.getContent());
        response.put("metadata", metadata);

        return response;
    }

    @Override
    public Map<String, Object> search(int page, int limit, String search) {
        Pageable pagingSort = PageRequest.of(page, limit);
        Page<Promotion> promotionPage = promotionRepo.findByNameContaining(search ,pagingSort);

        Metadata metadata = new Metadata();
        metadata.setPageNumber(promotionPage.getNumber());
        metadata.setPageSize(promotionPage.getSize());
        metadata.setTotalPages(promotionPage.getTotalPages());
        metadata.setTotalItems(promotionPage.getTotalElements());

        Map<String, Object> response = new HashMap<>();
        response.put("results", promotionPage.getContent());
        response.put("metadata", metadata);

        return response;
    }

    @Override
    public Promotion getPromotionByStartDateAndEndDate() {
        return promotionRepo.findAllByStartDateIsBeforeAndEndDateIsAfter(LocalDate.now().plusDays(1), LocalDate.now().plusDays(1));
    }

    @Override
    public Boolean checkPromotionByStartDateAndEndDate(LocalDate date, String id) {
        Promotion promotion = promotionRepo.findByDate(date);
        if(id != "") promotion = promotionRepo.findByDateNotId(date, id);
        return promotion == null;
    }
}
