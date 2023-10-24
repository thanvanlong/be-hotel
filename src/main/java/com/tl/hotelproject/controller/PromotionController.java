package com.tl.hotelproject.controller;

import com.mservice.config.Environment;
import com.mservice.enums.RequestType;
import com.mservice.models.PaymentResponse;
import com.mservice.processor.CreateOrderMoMo;
import com.mservice.shared.utils.LogUtils;
import com.tl.hotelproject.entity.ResponseDTO;
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
@RequestMapping("x  ")
@CrossOrigin("*")
public class PromotionController {
    @Autowired
    private PromotionService promotionService;

    @Autowired
    private PromotionRepo promotionRepo;

    @PostConstruct
    public void init() {
        LogUtils.init();
        String requestId = String.valueOf(System.currentTimeMillis());
        String orderId = String.valueOf(System.currentTimeMillis());
        Long transId = 2L;
        long amount = 5000;
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        System.out.println(System.currentTimeMillis());
        String partnerClientId = "partnerClientId";
        String orderInfo = "Pay With MoMo";
        String returnURL = "momosdk:/";
        String notifyURL = "https://webhook.site/df9d9c22-0473-4e6a-9cc3-d1d122e75936";
        String callbackToken = "callbackToken";
        String token = "";

        Environment environment = Environment.selectEnv("dev");
        PaymentResponse captureWalletMoMoResponse = null;
        try {
            captureWalletMoMoResponse = CreateOrderMoMo.process(environment, orderId, requestId, Long.toString(amount), orderInfo, returnURL, notifyURL, "", RequestType.CAPTURE_WALLET, Boolean.TRUE);
            System.out.println(captureWalletMoMoResponse.getDeeplink());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

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
                                                    @RequestParam(name = "description", required = false) String description,
                                                    @RequestParam(name = "file", required = false) MultipartFile file) throws Exception{
        Promotion promotion = new Promotion();
        promotion.setName(name);
        promotion.setDescription(description);
        promotion.setSlug();
        promotion.setImage(CloudinaryUtils.uploadImg(file.getBytes(), StringUtils.uuidFileName(promotion.getName())));

        return ResponseEntity.ok(new ResponseDTO<>(promotionService.save(promotion), "200", "Success", true));
    }

    @PutMapping(name = "/update/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ResponseDTO<String>> update(@PathVariable("id") String id,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "description", required = false) String description,
            @RequestParam(name = "file", required = false) MultipartFile file) throws Exception {

        Promotion promotion = promotionService.getById(id);
        if(name != null) {
            promotion.setName(name);
            promotion.setSlug();
        }
        if(description != null) promotion.setDescription(description);
        if(file != null) promotion.setImage(CloudinaryUtils.uploadImg(file.getBytes(), StringUtils.uuidFileName(promotion.getName())));

        return ResponseEntity.ok(new ResponseDTO<>(promotionService.update(promotion), "200", "Success", true));
    }

    @GetMapping("/sss")
    public void check() {
        System.out.println("long tv sss");
    }
}
