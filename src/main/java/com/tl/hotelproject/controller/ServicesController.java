package com.tl.hotelproject.controller;

import com.tl.hotelproject.entity.ResponseDTO;
import com.tl.hotelproject.entity.room.Room;
import com.tl.hotelproject.entity.services.Services;
import com.tl.hotelproject.repo.ServicesRepo;
import com.tl.hotelproject.service.services.ServicesService;
import com.tl.hotelproject.utils.CloudinaryUtils;
import com.tl.hotelproject.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("api/v1/services")
@CrossOrigin("*")
public class ServicesController {
    @Autowired
    private ServicesService servicesService;

    @Autowired
    private ServicesRepo servicesRepo;

    @GetMapping("list")
    public ResponseEntity<ResponseDTO<Map<String, Object>>> listServices(@RequestParam(defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "10") int limit,
                                                                     @RequestParam(defaultValue = "id,desc") String[] sort,
                                                                     @RequestParam(required = false, defaultValue = "") String search) {

        Map<String, Object> servicesList = servicesService.search(page, limit, search);


        return ResponseEntity.ok(new ResponseDTO<>(servicesList, "200", "Success", true));
    }

    @GetMapping("{id}")
    public ResponseEntity<ResponseDTO<Services>> getDetailServices(@PathVariable("id") String id) throws Exception {
        return ResponseEntity.ok(new ResponseDTO<>(this.servicesService.findById(id), "200", "Success", true)) ;
    }

    @PostMapping(value = "", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ResponseDTO<String>> save(@RequestParam("name") String name,
                                                    @RequestParam("unity") String unity,
                                                    @RequestParam(value = "description", required = false) String description,
                                                    @RequestParam("price") int price,
                                                    @RequestParam("file") MultipartFile file) throws Exception {
        Services services = new Services();
        services.setName(name);
        services.setUnity(unity);
        services.setPrice(price);
        services.setDescription(description);
        services.setImage(CloudinaryUtils.uploadImg(file.getBytes(), StringUtils.uuidFileName(services.getName())));

        return ResponseEntity.ok(new ResponseDTO<>(this.servicesService.save(services), "200", "Success", true)) ;
    }

    @PutMapping("")
    public ResponseEntity<ResponseDTO<String>> update(@RequestParam("id") String id,
                                                      @RequestParam(value = "name", required = false) String name,
                                                      @RequestParam(value = "unity", required = false) String unity,
                                                      @RequestParam(value = "description", required = false) String description,
                                                      @RequestParam(value = "price", required = false) Integer price,
                                                      @RequestParam(value = "file", required = false) MultipartFile file) throws Exception {
        Services services = new Services();
        services.setId(id);
        if(name != null) services.setName(name);
        if(unity != null) services.setUnity(unity);
        if(description != null) services.setDescription(description);
        if(price != null) services.setPrice(price);
        if(file != null) services.setImage(CloudinaryUtils.uploadImg(file.getBytes(), StringUtils.uuidFileName(services.getName())));
        return ResponseEntity.ok(new ResponseDTO<>(this.servicesService.update(services), "200", "Success", true)) ;
    }

    @DeleteMapping("")
    public ResponseEntity<ResponseDTO<String>> delete(@RequestBody String[] ids) throws Exception{
        for (String id : ids) {
            this.servicesService.findById(id);
        }

        for (String id : ids) {
            Services services = this.servicesService.findById(id);
            servicesRepo.delete(services);
        }
        return ResponseEntity.ok(new ResponseDTO<>("done", "200", "Success", true));
    }


}
