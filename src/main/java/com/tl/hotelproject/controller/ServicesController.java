package com.tl.hotelproject.controller;

import com.tl.hotelproject.entity.ResponseDTO;
import com.tl.hotelproject.entity.services.Services;
import com.tl.hotelproject.service.services.ServicesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/v1/services")
@CrossOrigin("*")
public class ServicesController {
    @Autowired
    private ServicesService servicesService;

    @GetMapping("list")
    public ResponseEntity<ResponseDTO<Map<String, Object>>> listServices(@RequestParam(defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "10") int limit,
                                                                     @RequestParam(defaultValue = "id,desc") String[] sort,
                                                                     @RequestParam(required = false) String filter) {

        Map<String, Object> servicesList = servicesService.pagingSort(page, limit);


        return ResponseEntity.ok(new ResponseDTO<>(servicesList, "200", "Success", true));
    }

    @GetMapping("{id}")
    public ResponseEntity<ResponseDTO<Services>> getDetailServices(@PathVariable("id") String id) throws Exception {
        return ResponseEntity.ok(new ResponseDTO<>(this.servicesService.findById(id), "200", "Success", true)) ;
    }

    @PostMapping("")
    public ResponseEntity<ResponseDTO<String>> save(@RequestBody Services services) throws Exception {
        return ResponseEntity.ok(new ResponseDTO<>(this.servicesService.save(services), "200", "Success", true)) ;
    }

}
