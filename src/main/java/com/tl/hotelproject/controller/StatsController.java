package com.tl.hotelproject.controller;

import com.google.gson.Gson;
import com.tl.hotelproject.entity.ResponseDTO;
import com.tl.hotelproject.entity.room.Room;
import com.tl.hotelproject.repo.BillRepo;
import com.tl.hotelproject.repo.BookingRepo;
import com.tl.hotelproject.repo.RoomRepo;
import com.tl.hotelproject.repo.ServicesRepo;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
class Revenue {
    private String type;
    private int value = 0;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class RoomStats {
    private String type;
    private String name;
    private long value;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class RevenueByService {
    private String name;
    private long value;
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

    @Autowired
    private ServicesRepo servicesRepo;

    @GetMapping("stats-rooms")
    public ResponseEntity<ResponseDTO<List<RoomStats>>> statsRooms(
            @RequestParam("year") int year,
            @RequestParam(value = "month", required = false) Integer month,
            @RequestParam(value = "day", required = false) Integer day
    ) throws Exception{
        List<Object[]> result;
        if(day != null) {
            if(month == null) throw new Exception("du lieu khong dung");
            result = bookingRepo.calculateRevenueByService(year, month, day);
        }

        else if(month != null) result = bookingRepo.calculateRoomRevenueAndBookings(year, month);
        else result = bookingRepo.calculateRevenueByService(year);

        List<Object[]> roomList = roomRepo.listRoomSelect();

        List<RoomStats> roomStats = new ArrayList<>();

        for (Object[] room : roomList) {
            boolean check = false;
            for(Object[] object: result) {
                String id = object[0].toString();
                if(room[1].toString().equals(id)){
                    check = true;
                    String name = object[1].toString();
                    String revenue = object[2].toString();
                    String count = object[3].toString();

                    RoomStats roomStats1 = new RoomStats();
                    roomStats1.setName(name);
                    roomStats1.setType("Doanh thu");
                    roomStats1.setValue(Long.parseLong(revenue));

                    RoomStats roomStats2 = new RoomStats();
                    roomStats2.setName(name);
                    roomStats2.setType("Luot thue");
                    roomStats2.setValue(Integer.parseInt(count));

                    roomStats.add(roomStats1);
                    roomStats.add(roomStats2);

                    break;
                }
            }
            if(!check) {
                RoomStats roomStats1 = new RoomStats();
                roomStats1.setName(room[0].toString());
                roomStats1.setType("Doanh thu");
                roomStats1.setValue(0);

                RoomStats roomStats2 = new RoomStats();
                roomStats2.setName(room[0].toString());
                roomStats2.setType("Luot thue");
                roomStats2.setValue(0);

                roomStats.add(roomStats1);
                roomStats.add(roomStats2);
            }
        }


        return ResponseEntity.ok(new ResponseDTO<>(roomStats, "200", "Success", true));
    }

    @GetMapping("stats-room")
    public ResponseEntity<ResponseDTO<List<RoomStats>>> statsRoom(){
        LocalDate currentDate = LocalDate.now();

        List<LocalDate> dateList = new ArrayList<>();
        for (int i = 1; i<= 7; i++) {
            dateList.add(currentDate.minusDays(i));
        }

        List<RoomStats> roomStats = new ArrayList<>();
        for (LocalDate date : dateList) {
            int day = date.getDayOfMonth();
            int month = date.getMonthValue();
            int year = date.getYear();
            List<Object[]> dataForDate = this.bookingRepo.calculateRoomRevenueAndBookings(year, month, day);
            String t = day + "/" + month + "/" + year;
            if(dataForDate.isEmpty()) {
                RoomStats roomStats1 = new RoomStats();
                roomStats1.setName(t);
                roomStats1.setType("Doanh thu");
                roomStats1.setValue(0);

                RoomStats roomStats2 = new RoomStats();
                roomStats2.setName(t);
                roomStats2.setType("Luot thue");
                roomStats2.setValue(0);

                roomStats.add(roomStats1);
                roomStats.add(roomStats2);
                continue;
            }
            Object[] temp = dataForDate.get(0);
            String revenue = temp[2].toString();
            String count = temp[3].toString();

            RoomStats roomStats1 = new RoomStats();
            roomStats1.setName(t);
            roomStats1.setType("Doanh thu");
            roomStats1.setValue(Long.parseLong(revenue));

            RoomStats roomStats2 = new RoomStats();
            roomStats2.setName(t);
            roomStats2.setType("Luot thue");
            roomStats2.setValue(Integer.parseInt(count));

            roomStats.add(roomStats1);
            roomStats.add(roomStats2);
        }

        return ResponseEntity.ok(new ResponseDTO<>(roomStats, "200", "Success", true));
    }

//    @PostConstruct
//    public void test(){
//
//
//    }

    @GetMapping("revenue")
    public ResponseEntity<ResponseDTO<Revenue[]>> statsRevenue(@RequestParam("year") int year){
        List<Object[]> result = bookingRepo.calculateRevenueByMonth(year);

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

    @GetMapping("stats-service")
    public ResponseEntity<ResponseDTO<List<RevenueByService>>> revenueByService(
            @RequestParam("year") int year,
            @RequestParam(value = "month", required = false) Integer month,
            @RequestParam(value = "day", required = false) Integer day
    ) throws Exception{
        List<Object[]> result;

        if(day != null) {
            if(month == null) throw new Exception("Du lieu dinh dang ko dung");

            result = bookingRepo.calculateRevenueByService(year, month, day);

        }
        else if(month != null){
            result = bookingRepo.calculateRevenueByService(year, month);
        }
        else result = bookingRepo.calculateRevenueByService(year);


        List<Object[]> serviceList = servicesRepo.listServiceSelect();

        List<RevenueByService> revenueList = new ArrayList<>();

        for (Object[] service : serviceList) {
            boolean check = false;
            for(Object[] object: result) {
                String id = object[0].toString();
                if(service[1].toString().equals(id)){
                    check = true;
                    String name = object[1].toString();
                    String revenue = object[2].toString();

                    RevenueByService revenueByService = new RevenueByService();
                    revenueByService.setName(name);
                    revenueByService.setValue(Long.parseLong(revenue));

                    revenueList.add(revenueByService);

                    break;
                }
            }
            if(!check) {
                RevenueByService revenueByService = new RevenueByService();
                revenueByService.setName(service[0].toString());
                revenueByService.setValue(0);
                revenueList.add(revenueByService);
            }
        }
        return ResponseEntity.ok(new ResponseDTO<>(revenueList, "200", "Success", true));

    }

//    @PostConstruct
//    public void test() {
//        List<Object[]> list = this.bookingRepo.calculateRevenueByService(2023);
//        System.out.println(new Gson().toJson(list));
//    }
}
