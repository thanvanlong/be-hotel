package com.tl.hotelproject.controller;

import com.tl.hotelproject.dtos.bills.ReqMomoDto;
import com.tl.hotelproject.entity.ResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/bill")
@CrossOrigin("*")
public class BillController {
    @PostMapping("/ipn-momo")
    public ResponseEntity<ResponseDTO<String>> statePaymentMomo(@RequestBody ReqMomoDto body){
        return ResponseEntity.ok(new ResponseDTO<>("", "", "", true));
    }

    @PostMapping("/ipn-vnpay")
    public ResponseEntity<ResponseDTO<String>> statePaymentVnpay(@RequestBody ReqMomoDto body){
        return ResponseEntity.ok(new ResponseDTO<>("", "", "", true));
    }
}
