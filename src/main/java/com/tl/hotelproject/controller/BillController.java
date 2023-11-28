package com.tl.hotelproject.controller;

import com.tl.hotelproject.dtos.bills.ReqMomoDto;
import com.tl.hotelproject.dtos.bills.VnpayResponseCode;
import com.tl.hotelproject.entity.ResponseDTO;
import com.tl.hotelproject.service.bill.BillService;
import com.tl.hotelproject.service.mail.EmailSender;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;



@RestController
@RequestMapping("/api/v1/bill")
@CrossOrigin("*")
public class BillController {
    @Autowired
    private BillService billService;

    @Autowired
    private EmailSender mailService;

    @PostMapping("/ipn-momo")
    public ResponseEntity<ResponseDTO<String>> statePaymentMomo(@RequestBody ReqMomoDto body){
        return ResponseEntity.ok(new ResponseDTO<>("", "", "", true));
    }

    @PostMapping(value = "/ipn-vnpay")
    public void statePaymentVnpay(@RequestParam Map<String, String> object, HttpServletResponse response) throws Exception{
        String code = object.get("vnp_ResponseCode");
        String vnp_TxnRef = object.get("vnp_TxnRef");
        if(code.equals("00")) {
            this.billService.fulfilledBill(vnp_TxnRef);
            response.sendRedirect("http://localhost:5174/thank/success");
        }

        this.billService.rejectBill(vnp_TxnRef, code);
        response.sendRedirect("http://localhost:5174/thank/fail");
    }
}
