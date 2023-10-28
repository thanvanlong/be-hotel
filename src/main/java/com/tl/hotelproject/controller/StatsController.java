package com.tl.hotelproject.controller;

import com.google.gson.Gson;
import com.tl.hotelproject.entity.ResponseDTO;
import com.tl.hotelproject.repo.BillRepo;
import com.tl.hotelproject.repo.BookingRepo;
import com.tl.hotelproject.repo.RoomRepo;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
class Revenue {
    private String type;
    private int value = 0;
}

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

    @GetMapping("stats-room")
    public ResponseEntity<ResponseDTO<Map<String, String>>> statsRoom(){
        return ResponseEntity.ok(new ResponseDTO<>(new HashMap<>(), "200", "Success", true));
    }

//    @PostConstruct
//    public void test(){
//        List<Object[]> result = bookingRepo.calculateRevenueByMonth(Integer.parseInt("2023"));
//        System.out.println(new Gson().toJson(result));
//
//        Map<String, String> months = new HashMap<>();
//        months.put("1", "0");
//        months.put("2", "0");
//        months.put("3", "0");
//        months.put("4", "0");
//        months.put("5", "0");
//        months.put("6", "0");
//        months.put("7", "0");
//        months.put("8", "0");
//        months.put("9", "0");
//        months.put("10", "0");
//        months.put("11", "0");
//        months.put("12", "0");
//
//        for (Object[] row : result) {
//            String month = row[0].toString();
//            String revenue = row[1].toString();
//            months.put(month, revenue);
//        }
//
//        System.out.println(months);
//
//    }

    @GetMapping("revenue")
    public ResponseEntity<ResponseDTO<Revenue[]>> statsRevenue(@RequestParam("year") String year){
        List<Object[]> result = bookingRepo.calculateRevenueByMonth(Integer.parseInt(year));

        Revenue[] revenues = new Revenue[12];

        for(int i = 1; i <= 12; i++){
            Revenue revenue = new Revenue();
            revenue.setType("Tháng "+ i);
            revenues[i-1] = revenue;
        }

        for (Object[] row : result) {
            String month = row[0].toString();
            String revenue = row[1].toString();

            int temp = Integer.parseInt(month);
            revenues[temp-1].setValue(Integer.parseInt(revenue));
        }

        return ResponseEntity.ok(new ResponseDTO<>(revenues, "200", "Success", true));
    }
}
