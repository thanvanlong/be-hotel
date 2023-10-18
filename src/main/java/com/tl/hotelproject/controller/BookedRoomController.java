package com.tl.hotelproject.controller;

import com.tl.hotelproject.dtos.booking.UpdateUsedServicesDto;
import com.tl.hotelproject.entity.ResponseDTO;
import com.tl.hotelproject.service.booking.BookedRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/booked")
@CrossOrigin("*")
public class BookedRoomController {
    @Autowired
    private BookedRoomService bookedRoomService;

//    @PutMapping("{id}")
//    public ResponseEntity<ResponseDTO<String>> update(@PathVariable("id") String id, @RequestBody UpdateUsedServicesDto body) throws Exception{
//        return ResponseEntity.ok(new ResponseDTO<>(this.bookedRoomService.update(new UpdateUsedServicesDto(id, body.getIdService(), body.getQuantity())), "200", "success", true));
//    }
}
