package com.tl.hotelproject.controller;

import com.tl.hotelproject.dtos.booking.AddBookingDto;
import com.tl.hotelproject.entity.ResponseDTO;
import com.tl.hotelproject.entity.client.Client;
import com.tl.hotelproject.service.client.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/v1/client")
@CrossOrigin("*")
public class ClientController {
    @Autowired
    private ClientService clientService;

    @GetMapping("list")
    public ResponseEntity<ResponseDTO<Map<String, Object>>> listClient(@RequestParam(defaultValue = "0") int page,
                                                                         @RequestParam(defaultValue = "10") int limit,
                                                                         @RequestParam(defaultValue = "id,desc") String[] sort,
                                                                         @RequestParam(required = false) String search){
        Map<String, Object> clientList = clientService.pagingSort(page, limit);


        return ResponseEntity.ok(new ResponseDTO<>(clientList, "200", "Success", true));
    }

    @GetMapping("/search")
    public ResponseEntity<ResponseDTO<Map<String, Object>>> searchRoom(@RequestParam(defaultValue = "0") int page,
                                                                       @RequestParam(defaultValue = "10") int limit,
                                                                       @RequestParam(defaultValue = "id,desc") String[] sort,
                                                                       @RequestParam("search") String search) {

        Map<String, Object> clientList = clientService.pagingSortSearch(page, limit, search);


        return ResponseEntity.ok(new ResponseDTO<>(clientList, "200", "Success", true));
    }

    @GetMapping("detail/{id}")
    public ResponseEntity<ResponseDTO<Client>> getDetail(@PathVariable("id") String id){
        Client client = new Client();
        return ResponseEntity.ok(new ResponseDTO<>(client, "200", "Success", true));


    }
}
