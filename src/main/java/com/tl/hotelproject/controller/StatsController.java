package com.tl.hotelproject.controller;

import com.google.gson.Gson;
import com.tl.hotelproject.entity.ResponseDTO;
import com.tl.hotelproject.entity.room.Room;
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

@Data
@AllArgsConstructor
@NoArgsConstructor
class RoomStats {
    private String type;
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

    @GetMapping("stats-rooms")
    public ResponseEntity<ResponseDTO<List<RoomStats>>> statsRooms(
            @RequestParam("year") int year,
            @RequestParam(value = "month", required = false) Integer month,
            @RequestParam(value = "day", required = false) Integer day
    ) throws Exception{

        if(day != null) {
            if(month == null) throw new Exception("du lieu khong dung");
            List<Object[]> result = bookingRepo.calculateRoomRevenueAndBookings(year, month, day);
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

        if(month != null) {

            List<Object[]> result = bookingRepo.calculateRoomRevenueAndBookings(year, month);
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

        List<Object[]> result = bookingRepo.calculateRoomRevenueAndBookings(year);
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
    public ResponseEntity<ResponseDTO<Map<String, String>>> statsRoom(){
        Date currentDate = new Date();

        // Tạo một đối tượng Calendar và đặt nó thành ngày hiện tại
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);

        calendar.add(Calendar.DAY_OF_YEAR, -7);

        Date sevenDaysAgo = calendar.getTime();

        System.out.println("Ngày hiện tại: " + currentDate);
        System.out.println("Ngày 7 ngày trước: " + sevenDaysAgo);

        return ResponseEntity.ok(new ResponseDTO<>(new HashMap<>(), "200", "Success", true));
    }

    @PostConstruct
    public void test(){
        Date currentDate = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DAY_OF_YEAR, -7);
        Date sevenDaysAgo = calendar.getTime();

        List<Date> dateList = new ArrayList<>();
        calendar.setTime(sevenDaysAgo);
        for (int i = 0; i < 7; i++) {
            dateList.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        for (Date date : dateList) {
            List<Object[]> dataForDate = this.bookingRepo.findDataForDate(date);
            System.out.println(new Gson().toJson(dataForDate));
        }


    }

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
}
