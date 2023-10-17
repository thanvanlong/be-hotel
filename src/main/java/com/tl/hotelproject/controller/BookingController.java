package com.tl.hotelproject.controller;

import com.tl.hotelproject.dtos.booking.AddBookingDto;
import com.tl.hotelproject.entity.ResponseDTO;
import com.tl.hotelproject.service.booking.BookingService;
import com.tl.hotelproject.service.room.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/booking")
@CrossOrigin("*")
public class BookingController {
    @Autowired
    private RoomService roomService;

    @Autowired
    private BookingService bookingService;

    @PostMapping("")
    public ResponseEntity<ResponseDTO<String>> booking(@RequestBody AddBookingDto body) {
        try {
            return ResponseEntity.ok(new ResponseDTO<>(this.bookingService.save(body), "200","Success", true)) ;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
