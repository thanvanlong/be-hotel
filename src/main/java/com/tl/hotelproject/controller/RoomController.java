package com.tl.hotelproject.controller;

import com.tl.hotelproject.entity.ResponseDTO;
import com.tl.hotelproject.entity.room.Room;
import com.tl.hotelproject.repo.RoomRepo;
import com.tl.hotelproject.service.room.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("api/v1/room")
@CrossOrigin("*")
public class RoomController {
    @Autowired
    private RoomService roomService;

    @Autowired
    private RoomRepo roomRepo;

    @GetMapping("/list")
    public ResponseEntity<ResponseDTO<Map<String, Object>>> listRoom(@RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "10") int limit,
                                                      @RequestParam(defaultValue = "id,desc") String[] sort,
                                                      @RequestParam(required = false) String filter) {

        Map<String, Object> zoomList = roomService.pagingSort(page, limit);


        return ResponseEntity.ok(new ResponseDTO<>(zoomList, "200", "Success", true));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<ResponseDTO<Room>> getRoom(@PathVariable("slug") String slug) {
        Room room = this.roomService.getRoomBySlugWithFeature(slug);
        return ResponseEntity.ok(new ResponseDTO<>(room, "200", "Success", true));
    }

    @PostMapping("/save")
    public ResponseEntity<ResponseDTO<String>> saveRoom(@RequestBody Room room, @RequestParam("file") MultipartFile file) {
        roomRepo.save(room);
        return ResponseEntity.ok(new ResponseDTO<>("Save Room Done!", "200", "Success", true));
    }

}
