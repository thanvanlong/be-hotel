package com.tl.hotelproject.controller;

import com.google.gson.Gson;
import com.tl.hotelproject.entity.ResponseDTO;
import com.tl.hotelproject.entity.room.Room;
import com.tl.hotelproject.repo.BillRepo;
import com.tl.hotelproject.repo.BookingRepo;
import com.tl.hotelproject.repo.RoomRepo;
import com.tl.hotelproject.repo.ServicesRepo;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
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
class RoomStatsExcel extends RoomStats{
    private String description;
    private Date createdAt;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class RevenueByService {
    private String name;
    private long value;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class RevenueByServiceExcel {
    private String name;
    private String description;
    private String unity;
    private Date createdAt;
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

    @GetMapping("/export-excel-revenue")
    public void exportExcel(@RequestParam("year") int year, HttpServletResponse response) throws IOException {
        Revenue[] revenues = this.revenueMonth(year);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Revenue " + year);

        // Tạo dữ liệu
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Month");
        headerRow.createCell(1).setCellValue("Revenue");

        // Tạo một CellStyle cho header với màu nền
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Set font chu
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);

        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            headerRow.getCell(i).setCellStyle(headerStyle);
            sheet.autoSizeColumn(i);
        }

        int sum = 0;

        // Thêm dữ liệu
        for(int i = 1; i <= revenues.length ; i++) {
            Row dataRow = sheet.createRow(i);
            dataRow.createCell(0).setCellValue(revenues[i-1].getType());
            dataRow.createCell(1).setCellValue(revenues[i-1].getValue());
            sum += revenues[i-1].getValue();
        }

        Row dataRow = sheet.createRow(revenues.length + 1);
        dataRow.createCell(0).setCellValue("Total");
        dataRow.createCell(1).setCellValue(sum);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename="+"revenue-"+year+ ".xlsx");

        workbook.write(response.getOutputStream());
        workbook.close();
    }

    @GetMapping("/export-excel-service")
    public void exportExcelService(@RequestParam("year") int year,
                                   @RequestParam(value = "month", defaultValue = "0") int month,
                                   @RequestParam(value = "day", defaultValue = "0") int day,
                                   HttpServletResponse response) throws Exception {

        List<RevenueByServiceExcel> revenueByServiceExcels = this.statsService(year, month, day);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Revenue Service");

        // Tạo dữ liệu
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Name");
        headerRow.createCell(1).setCellValue("Description");
        headerRow.createCell(2).setCellValue("Created At");
        headerRow.createCell(3).setCellValue("Revenue");


        // Tạo một CellStyle cho header với màu nền
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Set font chu
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);

        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            headerRow.getCell(i).setCellStyle(headerStyle);
            sheet.autoSizeColumn(i);
        }

        // Thêm dữ liệu
        for(int i = 1; i <= revenueByServiceExcels.size() ; i++) {
            Row dataRow = sheet.createRow(i);
            dataRow.createCell(0).setCellValue(revenueByServiceExcels.get(i-1).getName());
            dataRow.createCell(1).setCellValue(revenueByServiceExcels.get(i-1).getDescription());
            dataRow.createCell(2).setCellValue(revenueByServiceExcels.get(i-1).getCreatedAt());
            dataRow.createCell(3).setCellValue(revenueByServiceExcels.get(i-1).getValue());
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=revenue-service.xlsx");

        workbook.write(response.getOutputStream());
        workbook.close();
    }

    @GetMapping("/export-excel-room")
    public void exportExcelRoom(@RequestParam("year") int year,
                                   @RequestParam(value = "month", defaultValue = "0") int month,
                                   @RequestParam(value = "day", defaultValue = "0") int day,
                                   HttpServletResponse response) throws Exception {

        List<RoomStatsExcel> revenueByRoomExcels = this.statsRoom(year, month, day);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Revenue Service");

        // Tạo dữ liệu
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Name");
        headerRow.createCell(1).setCellValue("Description");
        headerRow.createCell(2).setCellValue("Created At");
        headerRow.createCell(3).setCellValue("Revenue");


        // Tạo một CellStyle cho header với màu nền
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Set font chu
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);

        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            headerRow.getCell(i).setCellStyle(headerStyle);
            sheet.autoSizeColumn(i);
        }

        // Thêm dữ liệu
        for(int i = 1; i <= revenueByRoomExcels.size() ; i++) {
            Row dataRow = sheet.createRow(i);
            dataRow.createCell(0).setCellValue(revenueByRoomExcels.get(i-1).getName());
            dataRow.createCell(1).setCellValue(revenueByRoomExcels.get(i-1).getDescription());
            dataRow.createCell(2).setCellValue(revenueByRoomExcels.get(i-1).getCreatedAt());
            dataRow.createCell(3).setCellValue(revenueByRoomExcels.get(i-1).getValue());
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=revenue-room.xlsx");

        workbook.write(response.getOutputStream());
        workbook.close();
    }

    @GetMapping("stats-rooms")
    public ResponseEntity<ResponseDTO<List<RoomStats>>> statsRooms(
            @RequestParam("year") int year,
            @RequestParam(value = "month", required = false) Integer month,
            @RequestParam(value = "day", required = false) Integer day
    ) throws Exception{
        List<Object[]> result;

        if(day != null) {
            if(month == null) throw new Exception("du lieu khong dung");
            result = bookingRepo.calculateRoomRevenueAndBookings(year, month, day);
        }

        else if(month != null) result = bookingRepo.calculateRoomRevenueAndBookings(year, month);
        else result = bookingRepo.calculateRoomRevenueAndBookings(year);

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

    @GetMapping("stats-room/{id}")
    public ResponseEntity<ResponseDTO<List<RoomStats>>> statsRoom(@PathVariable("id") String id){
        LocalDate currentDate = LocalDate.now();

        List<LocalDate> dateList = new ArrayList<>();
        for (int i = 1; i<= 7; i++) {
            dateList.add(currentDate.minusDays(i));
        }

        List<RoomStats> roomStats = new ArrayList<>();
        for (LocalDate date: dateList) {
            int day = date.getDayOfMonth();
            int month = date.getMonthValue();
            int year = date.getYear();
            Object[] dataForDate = this.bookingRepo.calculate7day(year, month, day, id);
            String t = day + "/" + month + "/" + year;


            if(dataForDate.length == 0) {
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
            String revenue = dataForDate[2].toString();
            String count = dataForDate[3].toString();

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
        return ResponseEntity.ok(new ResponseDTO<>(this.revenueMonth(year), "200", "Success", true));
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

    private Revenue[] revenueMonth(int year){
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

        return revenues;
    }

    private List<RevenueByServiceExcel> statsService(int year, int month, int day) throws Exception{
        List<Object[]> result;

        if(day != 0) {
            if(month == 0) throw new Exception("Du lieu dinh dang ko dung");

            result = bookingRepo.calculateRevenueByService(year, month, day);

        }
        else if(month != 0){
            result = bookingRepo.calculateRevenueByService(year, month);
        }
        else result = bookingRepo.calculateRevenueByService(year);


        List<Object[]> serviceList = servicesRepo.listServiceSelect();

        System.out.println(new Gson().toJson(serviceList));
        List<RevenueByServiceExcel> revenueList = new ArrayList<>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (Object[] service : serviceList) {
            boolean check = false;
            RevenueByServiceExcel revenueByService = new RevenueByServiceExcel();
            for(Object[] object: result) {
                String id = object[0].toString();
                if(service[1].toString().equals(id)){
                    check = true;
                    String name = object[1].toString();
                    String revenue = object[2].toString();

                    revenueByService.setName(name);
                    revenueByService.setValue(Long.parseLong(revenue));
                    break;
                }
            }
            if(!check) {
                revenueByService.setName(service[0].toString());
                revenueByService.setValue(0);

            }

            revenueByService.setCreatedAt(dateFormat.parse(service[3].toString()));
            revenueByService.setDescription(service[4].toString());
            revenueByService.setUnity(service[2].toString());
            revenueList.add(revenueByService);
        }
        return revenueList;
    }

    private List<RoomStatsExcel> statsRoom(int year, int month, int day) throws Exception{
        List<Object[]> result;
        if(day != 0) {
            if(month == 0) throw new Exception("du lieu khong dung");
            result = bookingRepo.calculateRoomRevenueAndBookings(year, month, day);
        }

        else if(month != 0) result = bookingRepo.calculateRoomRevenueAndBookings(year, month);
        else result = bookingRepo.calculateRoomRevenueAndBookings(year);

        List<Object[]> roomList = roomRepo.listRoomSelect();

        List<RoomStatsExcel> roomStats = new ArrayList<>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (Object[] room : roomList) {
            boolean check = false;
            RoomStatsExcel roomStats1 = new RoomStatsExcel();

            for(Object[] object: result) {
                String id = object[0].toString();
                if(room[1].toString().equals(id)){
                    check = true;
                    String name = object[1].toString();
                    String revenue = object[2].toString();

                    roomStats1.setName(name);
                    roomStats1.setValue(Long.parseLong(revenue));

                    break;
                }
            }
            if(!check) {
                roomStats1.setName(room[0].toString());
                roomStats1.setValue(0);

            }
            roomStats1.setCreatedAt(dateFormat.parse(room[3].toString()));
            roomStats1.setDescription(room[2].toString());
            roomStats.add(roomStats1);
        }
        return roomStats;
    }
}
