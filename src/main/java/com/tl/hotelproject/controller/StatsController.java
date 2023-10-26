package com.tl.hotelproject.controller;

import com.tl.hotelproject.entity.ResponseDTO;
import com.tl.hotelproject.repo.BillRepo;
import com.tl.hotelproject.repo.BookingRepo;
import com.tl.hotelproject.repo.RoomRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/v1/stats")
@CrossOrigin("*")
public class StatsController {
    @Autowired
    private BookingRepo bookingRepo;

    @Autowired
    private BillRepo billRepo;

    @Autowired
    private RoomRepo roomRepo;

//[
//    {
//        "year": "1991",
//            "value": 3,
//            "type": "Lon"
//    },
//    {
//        "year": "1992",
//            "value": 4,
//            "type": "Lon"
//    }
//]
    @GetMapping("stats-rooms")
    public ResponseEntity<ResponseDTO<Map<String, Object>>> statsRooms() {


        return ResponseEntity.ok(new ResponseDTO<>(new HashMap<>(), "200", "Success", true));
    }
}
