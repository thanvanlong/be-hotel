package com.tl.hotelproject.controller;

import com.google.gson.Gson;
import com.tl.hotelproject.entity.ResponseDTO;
import com.tl.hotelproject.entity.room.Room;
import com.tl.hotelproject.entity.user.User;
import com.tl.hotelproject.repo.BillRepo;
import com.tl.hotelproject.repo.BookingRepo;
import com.tl.hotelproject.repo.RoomRepo;
import com.tl.hotelproject.repo.ServicesRepo;
import com.tl.hotelproject.service.user.UserService;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @Autowired
    private UserService userService;

    @GetMapping("/export-excel-revenue")
    public void exportExcel(@RequestParam("year") int year, HttpServletResponse response) throws IOException {
        Revenue[] revenues = this.revenueMonth(year);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Revenue " + year);
        CellStyle cellStyle = createCellStyle(sheet, workbook);
        Row rowTime = sheet.createRow( 0);
        Cell cell = rowTime.createCell(0);
        cell.setCellValue("Ngày lập: " + LocalDateTime.now().toString());
        cell.setCellStyle(cellStyle);
        writeHeaderForRevenue(sheet, 1, "Báo cáo doanh thu năm " + year);

        int sum = 0;

        // Thêm dữ liệu
        Row dataRow = sheet.createRow(3);
        cell = dataRow.createCell(0);
        cell.setCellValue("Doanh thu");

        cell.setCellStyle(cellStyle);
        for(int i = 1; i <= revenues.length ; i++) {
            cell = dataRow.createCell(i);
            cell.setCellValue(revenues[i-1].getValue());
            cell.setCellStyle(cellStyle);
            sum += revenues[i-1].getValue();
        }

        dataRow = sheet.createRow(3);
        sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 12));
        cell = dataRow.createCell(0);
        cell.setCellValue("Tổng cộng: " + sum);
        short format = (short)BuiltinFormats.getBuiltinFormat("#,##0");

        Font font = sheet.getWorkbook().createFont();
        font.setFontName("Times New Roman");
        font.setFontHeightInPoints((short) 14); // font size
        CellStyle cellStyleFormatNumber = workbook.createCellStyle();
        cellStyleFormatNumber.setDataFormat(format);
        cellStyleFormatNumber.setFont(font);
        cell.setCellStyle(cellStyleFormatNumber);

        Row row = sheet.createRow(5);
        cell = row.createCell(11);
        cell.setCellValue("Người lập báo cáo");

        cell.setCellStyle(cellStyleFormatNumber);
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println(principal);
        User user = null;
        if (principal instanceof String && !((String) principal).isEmpty()) {
            user = userService.findByEmail((String) principal);
        }

        row = sheet.createRow(6);
        cell = row.createCell(11);

        cell.setCellStyle(cellStyle);
        if (user != null) {
            cell.setCellValue(user.getName());
        } else {
            cell.setCellValue("Nguyễn Thị Linh");
        }

        //Create CellStyle
        cell.setCellStyle(cellStyleFormatNumber);
        autosizeColumn(sheet, 12);

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
        CellStyle cellStyle = createCellStyle(sheet, workbook);
        Row rowTime = sheet.createRow( 0);
        Cell cell = rowTime.createCell(0);
        cell.setCellValue("Ngày lập: " + LocalDateTime.now().toString());
        cell.setCellStyle(cellStyle);
        writeHeaderForRoom(sheet, 1, "Báo cáo doanh thu dịch vụ năm 2023");


        long sum = 0;
        // Thêm dữ liệu
        for(int i = 1; i <= revenueByServiceExcels.size() ; i++) {
            Row dataRow = sheet.createRow(i + 1);
            cell = dataRow.createCell(0);
            cell.setCellValue(i);
            cell.setCellStyle(cellStyle);
            cell = dataRow.createCell(1);
            cell.setCellValue(revenueByServiceExcels.get(i-1).getName());
            cell.setCellStyle(cellStyle);
            cell = dataRow.createCell(2);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(revenueByServiceExcels.get(i-1).getDescription());
            cell = dataRow.createCell(3);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(revenueByServiceExcels.get(i-1).getCreatedAt().toString());

            cell = dataRow.createCell(4);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(revenueByServiceExcels.get(i-1).getValue());
            sum += revenueByServiceExcels.get(i-1).getValue();
        }
        Row dataRow = sheet.createRow(revenueByServiceExcels.size() + 4);
        sheet.addMergedRegion(new CellRangeAddress(revenueByServiceExcels.size() + 4, revenueByServiceExcels.size() + 4, 0, 4));
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 4));
        cell = dataRow.createCell(0);
        cell.setCellValue("Tổng cộng: " + sum);
        short format = (short)BuiltinFormats.getBuiltinFormat("#,##0");

        Font font = sheet.getWorkbook().createFont();
        font.setFontName("Times New Roman");
        font.setFontHeightInPoints((short) 14); // font size

        //Create CellStyle
        CellStyle cellStyleFormatNumber = workbook.createCellStyle();
        cellStyleFormatNumber.setDataFormat(format);
        cellStyleFormatNumber.setFont(font);
        cell.setCellStyle(cellStyleFormatNumber);

        for (int columnIndex = 0; columnIndex < 20; columnIndex++) {
            if (columnIndex == 0) {
                sheet.setColumnWidth(columnIndex, 2000);
            } else {
                sheet.autoSizeColumn(columnIndex);
                sheet.setColumnWidth(columnIndex, sheet.getColumnWidth(columnIndex) + 2000);
            }
            sheet.setDefaultRowHeight((short) 450);
        }



        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));

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
        CellStyle cellStyle = createCellStyle(sheet, workbook);
        Row rowTime = sheet.createRow( 0);
        Cell cell = rowTime.createCell(0);
        cell.setCellValue("Ngày lập: " + LocalDateTime.now().toString());
        cell.setCellStyle(cellStyle);


        writeHeaderForRoom(sheet, 1, "Báo cáo doanh thu phòng năm 2023");

        long sum = 0;
        // Thêm dữ liệu
        for(int i = 1; i <= revenueByRoomExcels.size() ; i++) {
            Row dataRow = sheet.createRow(i + 1);
            cell = dataRow.createCell(0);
            cell.setCellValue(i);
            cell.setCellStyle(cellStyle);
            cell = dataRow.createCell(1);
            cell.setCellValue(revenueByRoomExcels.get(i-1).getName());
            cell.setCellStyle(cellStyle);
            cell = dataRow.createCell(2);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(revenueByRoomExcels.get(i-1).getDescription());
            cell = dataRow.createCell(3);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(revenueByRoomExcels.get(i-1).getCreatedAt().toString());
            cell = dataRow.createCell(4);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(revenueByRoomExcels.get(i-1).getValue());
            sum += revenueByRoomExcels.get(i-1).getValue();
        }
        Row dataRow = sheet.createRow(revenueByRoomExcels.size() + 4);
        sheet.addMergedRegion(new CellRangeAddress(revenueByRoomExcels.size() + 4, revenueByRoomExcels.size() + 4, 0, 4));
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 4));
        cell = dataRow.createCell(0);
        cell.setCellValue("Tổng cộng: " + sum);
        short format = (short)BuiltinFormats.getBuiltinFormat("#,##0");

        Font font = sheet.getWorkbook().createFont();
        font.setFontName("Times New Roman");
        font.setFontHeightInPoints((short) 14); // font size

        //Create CellStyle
        CellStyle cellStyleFormatNumber = workbook.createCellStyle();
        cellStyleFormatNumber.setDataFormat(format);
        cellStyleFormatNumber.setFont(font);
        cell.setCellStyle(cellStyleFormatNumber);

        for (int columnIndex = 0; columnIndex < 20; columnIndex++) {
            if (columnIndex == 0) {
                sheet.setColumnWidth(columnIndex, 2000);
            } else {
                sheet.autoSizeColumn(columnIndex);
                sheet.setColumnWidth(columnIndex, sheet.getColumnWidth(columnIndex) + 2000);
            }
            sheet.setDefaultRowHeight((short) 450);
        }


        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));

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

    private static void writeHeaderForRevenue(Sheet sheet, int rowIndex, String header) {
        // create CellStyle
        CellStyle cellStyle = createStyleForHeader(sheet);
        Font font = sheet.getWorkbook().createFont();
        font.setFontName("Times New Roman");
        font.setBold(true);
        font.setFontHeightInPoints((short) 15); // font size

        CellStyle cellStyle1 = sheet.getWorkbook().createCellStyle();
        cellStyle1.setFont(font);
        cellStyle1.setBorderBottom(BorderStyle.THIN);
        cellStyle1.setBorderLeft(BorderStyle.THIN);
        cellStyle1.setBorderRight(BorderStyle.THIN);
        cellStyle1.setBorderTop(BorderStyle.THIN);
        Row row1 = sheet.createRow(rowIndex);
        row1.setHeight((short) 550);
        Cell cell2 = row1.createCell(0);
        cell2.setCellValue(header.toUpperCase());
        cellStyle1.setAlignment(HorizontalAlignment.CENTER);
        cellStyle1.setVerticalAlignment(VerticalAlignment.CENTER);
        cell2.setCellStyle(cellStyle1);
        Row row = sheet.createRow(rowIndex + 1);

        Cell cell = row.createCell(0);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("Tháng ");

        cell = row.createCell(1);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("Tháng 1");

        cell = row.createCell(2);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("Tháng 2");

        cell = row.createCell(3);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("Tháng 3");

        cell = row.createCell(4);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("Tháng 4");

        cell = row.createCell(5);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("Tháng 5");

        cell = row.createCell(6);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("Tháng 6");

        cell = row.createCell(7);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("Tháng 7");

        cell = row.createCell(8);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("Tháng 8");

        cell = row.createCell(9);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("Tháng 9");

        cell = row.createCell(10);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("Tháng 10");


        cell = row.createCell(11);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("Tháng 11");

        cell = row.createCell(12);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("Tháng 12");
    }

    private static void writeHeaderForRoom(Sheet sheet, int rowIndex, String header) {
        // create CellStyle
        CellStyle cellStyle = createStyleForHeader(sheet);
        Font font = sheet.getWorkbook().createFont();
        font.setFontName("Times New Roman");
        font.setBold(true);
        font.setFontHeightInPoints((short) 15); // font size

        CellStyle cellStyle1 = sheet.getWorkbook().createCellStyle();
        cellStyle1.setFont(font);
        cellStyle1.setBorderBottom(BorderStyle.THIN);
        cellStyle1.setBorderLeft(BorderStyle.THIN);
        cellStyle1.setBorderRight(BorderStyle.THIN);
        cellStyle1.setBorderTop(BorderStyle.THIN);
        Row row1 = sheet.createRow(rowIndex);
        row1.setHeight((short) 550);
        Cell cell2 = row1.createCell(0);
        cell2.setCellValue(header.toUpperCase());
        cellStyle1.setAlignment(HorizontalAlignment.CENTER);
        cellStyle1.setVerticalAlignment(VerticalAlignment.CENTER);
        cell2.setCellStyle(cellStyle1);
        Row row = sheet.createRow(rowIndex + 2);

        Cell cell = row.createCell(0);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("STT");

        cell = row.createCell(1);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("Tên");

        cell = row.createCell(2);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("Mô tả");

        cell = row.createCell(3);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("Ngày tạo");

        cell = row.createCell(4);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("Doanh thu");
    }

    private static CellStyle createStyleForHeader(Sheet sheet) {
        Font font = sheet.getWorkbook().createFont();
        font.setFontName("Times New Roman");
        font.setBold(true);
        font.setFontHeightInPoints((short) 14); // font size
        font.setColor(IndexedColors.WHITE.getIndex());

        CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
        cellStyle.setFont(font);
        cellStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        return cellStyle;
    }

    private static void autosizeColumn(Sheet sheet, int sizeMerge) {
        for (int columnIndex = 0; columnIndex < 20; columnIndex++) {
            if (columnIndex != 11) {
                sheet.setColumnWidth(columnIndex, 4000);
            } else {
                sheet.autoSizeColumn(columnIndex);
            }

            sheet.setDefaultRowHeight((short) 450);
        }


        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, sizeMerge));

    }



    private CellStyle createCellStyle(Sheet sheet, Workbook workbook) {
        short format = (short)BuiltinFormats.getBuiltinFormat("#,##0");

        Font font = sheet.getWorkbook().createFont();
        font.setFontName("Times New Roman");
        font.setFontHeightInPoints((short) 14); // font size

        //Create CellStyle
        CellStyle cellStyleFormatNumber = workbook.createCellStyle();
        cellStyleFormatNumber.setDataFormat(format);
        cellStyleFormatNumber.setFont(font);
        cellStyleFormatNumber.setBorderBottom(BorderStyle.THIN);
        cellStyleFormatNumber.setBorderLeft(BorderStyle.THIN);
        cellStyleFormatNumber.setBorderRight(BorderStyle.THIN);
        cellStyleFormatNumber.setBorderTop(BorderStyle.THIN);

        return cellStyleFormatNumber;
    }

}
