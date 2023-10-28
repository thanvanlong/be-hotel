package com.tl.hotelproject.controller;

import com.tl.hotelproject.entity.ResponseDTO;
import com.tl.hotelproject.entity.State;
import com.tl.hotelproject.entity.promotion.Promotion;
import com.tl.hotelproject.repo.PromotionRepo;
import com.tl.hotelproject.service.promotion.PromotionService;
import com.tl.hotelproject.utils.CloudinaryUtils;
import com.tl.hotelproject.utils.StringUtils;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/promotion")
@CrossOrigin("*")
public class PromotionController {
    @Autowired
    private PromotionService promotionService;

    @Autowired
    private PromotionRepo promotionRepo;

    @GetMapping("/list")
    public ResponseEntity<ResponseDTO<Map<String, Object>>> listPromotion(@RequestParam(defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "10") int limit,
                                                                     @RequestParam(defaultValue = "id,desc") String[] sort,
                                                                     @RequestParam(required = false) String search) {

        Map<String, Object> promotionPage = promotionService.pagingSort(page, limit);


        return ResponseEntity.ok(new ResponseDTO<>(promotionPage, "200", "Success", true));
    }

    @GetMapping("{slug}")
    public ResponseEntity<ResponseDTO<Promotion>> getDetail(@PathVariable("slug") String slug) throws Exception {
        return ResponseEntity.ok(new ResponseDTO<>(promotionService.getBySlug(slug), "200", "Success", true));
    }

    @PostMapping(name = "/save", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ResponseDTO<String>> save(@RequestParam("name") String name,
                                                    @RequestParam(name = "description", required = false, defaultValue = "") String description,
                                                    @RequestParam(name = "startDate") LocalDate startDate,
                                                    @RequestParam("endDate") LocalDate endDate,
                                                    @RequestParam(value = "state", required = false, defaultValue = "0") State state,
                                                    @RequestParam(value = "discount") Integer discount,
                                                    @RequestParam(name = "file", required = false) MultipartFile file) throws Exception{
        Promotion promotion = new Promotion();
        promotion.setName(name);
        promotion.setStartDate(startDate);
        promotion.setEndDate(endDate);
        promotion.setDescription(description);
        promotion.setSlug();
        promotion.setState(state);
        promotion.setDiscount(discount);
        promotion.setImage(CloudinaryUtils.uploadImg(file.getBytes(), StringUtils.uuidFileName(promotion.getName())));

        return ResponseEntity.ok(new ResponseDTO<>(promotionService.save(promotion), "200", "Success", true));
    }

    @PutMapping(name = "/update/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ResponseDTO<String>> update(@PathVariable("id") String id,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "description", required = false) String description,
            @RequestParam(name = "startDate", required = false) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) LocalDate endDate,
            @RequestParam(value = "state", required = false) State state,
            @RequestParam(value = "discount", required = false) Integer discount,
            @RequestParam(name = "file", required = false) MultipartFile file) throws Exception {

        Promotion promotion = promotionService.getById(id);
        if(name != null) {
            promotion.setName(name);
            promotion.setSlug();
        }
        if(description != null) promotion.setDescription(description);
        if(startDate != null) promotion.setStartDate(startDate);
        if(endDate != null) promotion.setStartDate(endDate);
        if(state != null) promotion.setState(state);
        if(discount!= null) promotion.setDiscount(discount);
        if(file != null) promotion.setImage(CloudinaryUtils.uploadImg(file.getBytes(), StringUtils.uuidFileName(promotion.getName())));

        return ResponseEntity.ok(new ResponseDTO<>(promotionService.update(promotion), "200", "Success", true));
    }
}
