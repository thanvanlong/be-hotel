package com.tl.hotelproject.controller;

import com.tl.hotelproject.dtos.booking.AddBookingDto;
import com.tl.hotelproject.dtos.booking.UpdateUsedServicesDto;
import com.tl.hotelproject.entity.ResponseDTO;
import com.tl.hotelproject.entity.bill.Bill;
import com.tl.hotelproject.entity.bill.PaymentType;
import com.tl.hotelproject.service.booking.BookingService;
import com.tl.hotelproject.service.room.RoomService;
import com.tl.hotelproject.utils.VnpayUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/v1/booking")
@CrossOrigin("*")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping("client-booking")
    public ResponseEntity<ResponseDTO<String>> booking(@RequestBody AddBookingDto body) {
        try {
            return ResponseEntity.ok(new ResponseDTO<>(this.bookingService.save(body), "200","Success", true));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PutMapping("update-service")
    public ResponseEntity<ResponseDTO<String>> updateService(@Valid @RequestBody UpdateUsedServicesDto body) throws Exception{
        return ResponseEntity.ok(new ResponseDTO<>(this.bookingService.updateUsedService(body), "200", "Success", true));
    }

    @GetMapping("list")
    public ResponseEntity<ResponseDTO<Map<String, Object>>> listBooking(@RequestParam(defaultValue = "0") int page,
                                                                         @RequestParam(defaultValue = "10") int limit,
                                                                         @RequestParam(defaultValue = "id,desc") String[] sort,
                                                                         @RequestParam(required = false) String filter) {

        Map<String, Object> bookingList = bookingService.pagingSort(page, limit);


        return ResponseEntity.ok(new ResponseDTO<>(bookingList, "200", "Success", true));
    }


}
