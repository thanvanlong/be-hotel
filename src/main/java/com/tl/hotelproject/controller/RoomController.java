package com.tl.hotelproject.controller;

import com.google.gson.Gson;
import com.tl.hotelproject.dtos.room.AddRoomNameDto;
import com.tl.hotelproject.entity.ResponseDTO;
import com.tl.hotelproject.entity.room.FeatureRoom;
import com.tl.hotelproject.entity.room.Room;
import com.tl.hotelproject.entity.room.RoomName;
import com.tl.hotelproject.repo.FeatureRoomRepo;
import com.tl.hotelproject.repo.RoomRepo;
import com.tl.hotelproject.service.room.FeatureRoomService;
import com.tl.hotelproject.service.room.RoomService;
import com.tl.hotelproject.utils.CloudinaryUtils;
import com.tl.hotelproject.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/room")
@CrossOrigin("*")
public class RoomController {
    @Autowired
    private RoomService roomService;

    @Autowired
    private RoomRepo roomRepo;

    @Autowired
    private FeatureRoomService featureRoomService;

    @Autowired
    private FeatureRoomRepo featureRoomRepo;

    @GetMapping("/list-feature-room")
    public ResponseEntity<ResponseDTO<Map<String, Object>>> listFR(@RequestParam(defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "100") int limit,
                                                                     @RequestParam(defaultValue = "id,desc") String[] sort,
                                                                     @RequestParam(required = false) String search) {

        Map<String, Object> frPage = featureRoomService.pagingSort(page, limit);


        return ResponseEntity.ok(new ResponseDTO<>(frPage, "200", "Success", true));
    }

    @GetMapping("/list")
    public ResponseEntity<ResponseDTO<Map<String, Object>>> listRoom(@RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "10") int limit,
                                                      @RequestParam(defaultValue = "id,desc") String[] sort,
                                                      @RequestParam(required = false, defaultValue = "") String search) {

        Map<String, Object> roomList = roomService.pagingSortSearch(page, limit, search);


        return ResponseEntity.ok(new ResponseDTO<>(roomList, "200", "Success", true));
    }

    @GetMapping("/search")
    public ResponseEntity<ResponseDTO<?>> searchRoom(@RequestParam(defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "10") int limit,
                                                                     @RequestParam(defaultValue = "id,desc") String[] sort,
                                                                     @RequestParam("search") String search) {

        Map<String, Object> roomList = roomService.pagingSortSearch(page, limit, search);


        return ResponseEntity.ok(new ResponseDTO<>(roomList, "200", "Success", true));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<ResponseDTO<Room>> getRoom(@PathVariable("slug") String slug) {
        Room room = this.roomService.getRoomBySlugWithFeature(slug);
        return ResponseEntity.ok(new ResponseDTO<>(room, "200", "Success", true));
    }

    @PostMapping(value = "/save",  produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ResponseDTO<String>> saveRoom(@RequestParam("name") String name,
                                                        @RequestParam(value = "price", required = false, defaultValue = "0") int price,
                                                        @RequestParam(value = "description", required = false, defaultValue = "") String description,
                                                        @RequestParam(value = "featureRooms", required = false) List<String> featureRooms,

                                                        @RequestParam("files") MultipartFile[] files) {
        System.out.println(files[0].getOriginalFilename() + " " + files[0].getResource().getFilename());
        Room check = roomRepo.getRoomByName(name);
        if(check != null) {
            return ResponseEntity.ok(new ResponseDTO<String>("Ten khach san da bi trung", "500", "Failed", false));
        }

        String []typeImg = {"image/png", "image/jpeg", "image/jpg"};
        for (MultipartFile file : files){
            if (!Arrays.asList(typeImg).contains(file.getContentType())) {
                return ResponseEntity.ok(new ResponseDTO<String>("Thể loại của ảnh không hợp lệ", "404", "Failed", false));
            }
        }

        Room room = new Room();
        room.setName(name);
        room.setPrice(price);
        if(featureRooms != null) room.setFeatureRooms(featureRooms);
        room.setDescription(description);

        List<String> imgUrls = new ArrayList<>();


        try {
            for (MultipartFile file : files){
                if (!Arrays.asList(typeImg).contains(file.getContentType())) {
                    return ResponseEntity.ok(new ResponseDTO<String>("Thể loại của ảnh không hợp lệ", "404", "Failed", false));
                }
                imgUrls.add(CloudinaryUtils.uploadImg(file.getBytes(), StringUtils.uuidFileName(name)));
            }
//            List<String> arrImg = Arrays.asList(imgUrls.toString());
            room.setImages(imgUrls);

        } catch (IOException e) {
            return ResponseEntity.ok(new ResponseDTO<String>("Upload ảnh lên không thành công", "404", "Failed", false));
        }


        roomRepo.save(room);
        return ResponseEntity.ok(new ResponseDTO<>("Save Room Done!", "200", "Success", true));
    }


    @PutMapping(value = "/update", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ResponseDTO<String>> updateRoom(
                                                            @RequestParam(value = "id") String id,
                                                            @RequestParam(value = "name", required = false) String name,
                                                            @RequestParam(value = "price", required = false, defaultValue = "-1") int price,
                                                            @RequestParam(value = "description", required = false) String description,
                                                            @RequestParam(value = "featureRooms", required = false) List<String> featureRooms,
                                                            @RequestParam(value = "images", required = false) List<String> images,
                                                            @RequestParam(value = "files", required = false)
                                                            @ApiParam(value = "Danh sách tệp ảnh", required = false, type = "string", format = "binary", allowMultiple = true)
                                                            MultipartFile[] files) throws Exception{

        Room room = roomService.findById(id);

        if(room == null) {
            return ResponseEntity.ok(new ResponseDTO<String>("Không tìm thấy sách để cập nhật", "404", "Failed",false));
        }

        if(name != null) room.setName(name);
        if(description != null) room.setDescription(description);
        if(featureRooms != null) room.setFeatureRooms(featureRooms);
        if(price != -1) room.setPrice(price);

        List<String> imgUrls = new ArrayList<>();
        if(images != null) {
            imgUrls.addAll(images);
        }

        if (files != null) {

//            if(room.getImages() != null) {
//                imgUrls.addAll(room.getImages());
//            }

            String []typeImg = {"image/png", "image/jpeg", "image/jpg"};
            for (MultipartFile file : files){
                if (!Arrays.asList(typeImg).contains(file.getContentType())) {
                    return ResponseEntity.ok(new ResponseDTO<String>("Thể loại của ảnh không hợp lệ", "404", "Failed", false));
                }
            }

            try {
                for (MultipartFile file : files){
                    if (!Arrays.asList(typeImg).contains(file.getContentType())) {
                        return ResponseEntity.ok(new ResponseDTO<String>("Thể loại của ảnh không hợp lệ", "404", "Failed", false));
                    }
                    imgUrls.add(CloudinaryUtils.uploadImg(file.getBytes(), StringUtils.uuidFileName(name)));
                }
//            List<String> arrImg = Arrays.asList(imgUrls.toString());
                

            } catch (IOException e) {
                return ResponseEntity.ok(new ResponseDTO<String>("Upload ảnh lên không thành công", "404", "Failed", false));
            }
        }
        room.setImages(imgUrls);
        room.setCreatedAt(room.getCreatedAt());

        roomService.update(room);

        return ResponseEntity.ok(new ResponseDTO<>("Update Room Done!", "200", "Success", true));
    }

    @PostMapping(value = "/save-room-name/{id}",  produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ResponseDTO<String>> saveRoomName(@PathVariable("id") String id, @RequestBody List<AddRoomNameDto> body) throws Exception{
        Room room = new Room();
        room.setId(id);
        List<RoomName> roomNames = body.stream().map(it -> {
            RoomName roomName = new RoomName();
            if(it.getId() != null) roomName.setId(it.getId());
            roomName.setName(it.getName());
            return roomName;
        }).toList();

        room.setRoomNames(roomNames);
        room.setQuantity(roomNames.size());

        return ResponseEntity.ok(new ResponseDTO<>(roomService.updateRoomName(room), "200", "Success", true));
    }

    @DeleteMapping("")
    public ResponseEntity<ResponseDTO<String>> delete(@RequestBody String[] ids) throws Exception{
        for (String id : ids) {
            this.roomService.findById(id);
        }

        for (String id : ids) {
            Room room = this.roomService.findById(id);
            roomRepo.delete(room);
        }
        return ResponseEntity.ok(new ResponseDTO<>("done", "200", "Success", true));
    }

    @PostConstruct
    public void test () {

        long count = this.featureRoomService.count();
        if(count > 0) return;
        List<String> list = new ArrayList<>();
        list.add("Điều hòa 2 chiều");
        list.add("Nước lọc miễn phí");
        list.add("Điện thoại quốc tế");
        list.add("Đồ dùng trong phòng tắm");
        list.add("Minibar");
        list.add("Máy sấy tóc, áo choàng tắm, dép đi trong phòng");
        list.add("Két an toàn");
        list.add("Điện áp 220 vôn");
        list.add("Tivi màn hình phẳng 43 inch");
        list.add("Gương trang điểm");
        list.add("Bàn làm việc");
        list.add("Internet cáp quang không dây");
        list.add("Truyền hình cáp");
        list.add("Tủ quần áo");
        list.add("Ấm điện đun nước");
        list.add("Phòng tắm với vòi sen và bồn tắm");
        list.add("Internet cáp quang không dây");
        list.add("Ghế sofa");
        list.add("Không gian ngồi nghỉ");
        list.add("Hoa quả chào mừng");
        list.add("Phòng ăn riêng");

        for(String f: list) {
            FeatureRoom featureRoom = new FeatureRoom();
            featureRoom.setName(f);
            featureRoomRepo.save(featureRoom);
        }

    }

}
