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
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    private int bookingCount = 0;
    private long price = 0;
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
    private int bookingCount = 0;
    private long price = 0;
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

    private static String formatCurrency(long number) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        return numberFormat.format(number);
    }

    private static String formatDate() {
        LocalDate currentDate = LocalDate.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("'Hà Nội, Ngày' dd 'Tháng' MM 'Năm' yyyy", new Locale("vi"));
        return currentDate.format(formatter);
    }

    @GetMapping("/export-excel-revenue")
    public void exportExcel(@RequestParam("year") int year, HttpServletResponse response) throws IOException {
        Revenue[] revenues = this.revenueMonth(year);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Revenue " + year);
        CellStyle cellStyle = createCellStyle(sheet, workbook);
        CellStyle cellStyleDate = createCellStyleDate(sheet, workbook);
        Row rowTime = sheet.createRow( 0);
        Cell cell = rowTime.createCell(0);
        cell.setCellValue("Ngày lập: " + LocalDateTime.now().toString());
        cell.setCellStyle(cellStyle);

        rowTime = sheet.createRow( 1);
        cell = rowTime.createCell(0);
        cell.setCellValue("Khách sạn Thanh Sơn - Đc: 047A Xuân Viên P.SaPa TX. Sa Pa");
        cell.setCellStyle(cellStyle);

        writeHeaderForRevenue(sheet, 2, "Báo cáo doanh thu năm " + year);

        int sum = 0;

        // Thêm dữ liệu
        Row dataRow = sheet.createRow(4);
        cell = dataRow.createCell(0);
        cell.setCellValue("Doanh thu");

        cell.setCellStyle(cellStyle);
        for(int i = 1; i <= revenues.length ; i++) {
            cell = dataRow.createCell(i);
            cell.setCellValue(revenues[i-1].getValue());
            cell.setCellStyle(cellStyle);
            sum += revenues[i-1].getValue();
        }

        dataRow = sheet.createRow(5);
        sheet.addMergedRegion(new CellRangeAddress(5, 5, 0, 12));
        cell = dataRow.createCell(0);
        cell.setCellValue("Tổng cộng: " + formatCurrency(sum) + " VND");
        short format = (short)BuiltinFormats.getBuiltinFormat("#,##0");

        Font font = sheet.getWorkbook().createFont();
        font.setFontName("Times New Roman");
        font.setFontHeightInPoints((short) 14); // font size
        CellStyle cellStyleFormatNumber = workbook.createCellStyle();
        cellStyleFormatNumber.setDataFormat(format);
        cellStyleFormatNumber.setFont(font);
        cell.setCellStyle(cellStyleFormatNumber);

        Row row = sheet.createRow(6);
        cell = row.createCell(11);
        cell.setCellStyle(cellStyleDate);
        cell.setCellValue(formatDate());

        row = sheet.createRow(7);
        cell = row.createCell(11);
        cell.setCellValue("Người lập báo cáo");
        cell.setCellStyle(createCellStyleBC(sheet, workbook));

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = null;
        if (principal instanceof String && !((String) principal).isEmpty()) {
            user = userService.findByEmail((String) principal);
        }

        row = sheet.createRow(8);
        cell = row.createCell(11);
        cell.setCellStyle(createCellStyleBC(sheet, workbook));

        if (user != null) {
            cell.setCellValue(user.getName());
        } else {
            cell.setCellValue("Nguyễn Thị Thùy Linh");
        }

        //Create CellStyle
//        cell.setCellStyle(cellStyleFormatNumber);
        autosizeColumn(sheet, 12);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename="+"revenue-"+year+ ".xlsx");

        workbook.write(response.getOutputStream());
        workbook.close();
    }

    @GetMapping("/export-excel-service")
    public void exportExcelService( @RequestParam("startDate") String startDate,
                                    @RequestParam("endDate") String endDate,
                                   HttpServletResponse response) throws Exception {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

        Date start = dateFormat.parse(startDate);
        Date end = dateFormat.parse(endDate);

        List<RevenueByServiceExcel> revenueByServiceExcels = this.statsService(start, end);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Revenue Service");
        CellStyle cellStyle = createCellStyle(sheet, workbook);
        Row rowTime = sheet.createRow( 0);
        Cell cell = rowTime.createCell(0);
        cell.setCellValue("Ngày lập: " + LocalDateTime.now().toString());
        cell.setCellStyle(cellStyle);

        rowTime = sheet.createRow( 1);
        cell = rowTime.createCell(0);
        cell.setCellValue("Khách sạn Thanh Sơn - Đc: 047A Xuân Viên P.SaPa TX. Sa Pa");
        cell.setCellStyle(cellStyle);

        writeHeaderForService(sheet, 2, "Báo cáo doanh thu dịch vụ");

        writeSubHeader(sheet, 3,"Từ ngày "+ startDate + " đến ngày " + endDate);

        long sum = 0;
        // Thêm dữ liệu
        int i = 4;
        for(i = 4; i <= revenueByServiceExcels.size() + 3 ; i++) {
            Row dataRow = sheet.createRow(i + 1);
            cell = dataRow.createCell(0);
            cell.setCellValue(i - 3);
            cell.setCellStyle(cellStyle);
            cell = dataRow.createCell(1);
            cell.setCellValue(revenueByServiceExcels.get(i-4).getName());
            cell.setCellStyle(cellStyle);
            cell = dataRow.createCell(2);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(revenueByServiceExcels.get(i-4).getBookingCount());
            cell = dataRow.createCell(3);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(revenueByServiceExcels.get(i-4).getPrice());

            cell = dataRow.createCell(4);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(revenueByServiceExcels.get(i-4).getValue());
            sum += revenueByServiceExcels.get(i-4).getValue();
        }
        Row dataRow = sheet.createRow(i+1);
        sheet.addMergedRegion(new CellRangeAddress(i+1, i+1, 0, 4));
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 4));
        sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 4));
        cell = dataRow.createCell(0);
        cell.setCellValue("Tổng cộng: " + formatCurrency(sum) + " VND");
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
//                sheet.setColumnWidth(columnIndex, sheet.getColumnWidth(columnIndex) + 2000);
            }
            sheet.setDefaultRowHeight((short) 450);
        }

        dataRow = sheet.createRow(i+2);
        cell = dataRow.createCell(3);
        CellStyle style = createCellStyleDate(sheet, workbook);

        cell.setCellStyle(style);
        cell.setCellValue(formatDate());

        dataRow = sheet.createRow(i+3);
        cell = dataRow.createCell(3);
        cell.setCellValue("Người lập báo cáo");
        cell.setCellStyle(createCellStyleBC(sheet, workbook));

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User user = null;
        if (principal instanceof String && !((String) principal).isEmpty()) {
            user = userService.findByEmail((String) principal);
        }

        dataRow = sheet.createRow(i + 4);
        cell = dataRow.createCell(3);
        cell.setCellStyle(createCellStyleBC(sheet, workbook));

        if (user != null) {
            cell.setCellValue(user.getName());
        } else {
            cell.setCellValue("Nguyễn Thị Thùy Linh");
        }

        //Create CellStyle
//        cell.setCellStyle(cellStyleFormatNumber);
//        autosizeColumn(sheet, 12);

//        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));
//        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 4));

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=revenue-service.xlsx");

        workbook.write(response.getOutputStream());
        workbook.close();
    }

    @GetMapping("/export-excel-room")
    public void exportExcelRoom( @RequestParam("startDate") String startDate,
                                 @RequestParam("endDate") String endDate,
                                   HttpServletResponse response) throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

        Date start = dateFormat.parse(startDate);
        Date end = dateFormat.parse(endDate);

        List<RoomStatsExcel> revenueByRoomExcels = this.statsRoom(start, end);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Revenue Room");
        CellStyle cellStyle = createCellStyle(sheet, workbook);
        Row rowTime = sheet.createRow( 0);
        Cell cell = rowTime.createCell(0);
        cell.setCellValue("Ngày lập: " + LocalDateTime.now().toString());
        cell.setCellStyle(cellStyle);

        rowTime = sheet.createRow( 1);
        cell = rowTime.createCell(0);
        cell.setCellValue("Khách sạn Thanh Sơn - Đc: 047A Xuân Viên P.SaPa TX. Sa Pa");
        cell.setCellStyle(cellStyle);

        writeHeaderForRoom(sheet, 2, "Báo cáo doanh thu phòng");
        writeSubHeader(sheet, 3,"Từ ngày "+ startDate + " đến ngày " + endDate);

        long sum = 0;
        // Thêm dữ liệu
        int i = 4;
        for(i = 4; i <= revenueByRoomExcels.size() + 3 ; i++) {
            Row dataRow = sheet.createRow(i + 1);
            cell = dataRow.createCell(0);
            cell.setCellValue(i - 3);
            cell.setCellStyle(cellStyle);
            cell = dataRow.createCell(1);
            cell.setCellValue(revenueByRoomExcels.get(i-4).getName());
            cell.setCellStyle(cellStyle);
            cell = dataRow.createCell(2);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(revenueByRoomExcels.get(i-4).getBookingCount());
            cell = dataRow.createCell(3);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(revenueByRoomExcels.get(i-4).getPrice());
            cell = dataRow.createCell(4);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(revenueByRoomExcels.get(i-4).getValue());
            sum += revenueByRoomExcels.get(i-4).getValue();
        }
        Row dataRow = sheet.createRow(i+ 1);
        sheet.addMergedRegion(new CellRangeAddress(i+1, i+1, 0, 4));
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 4));
        sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 4));
        cell = dataRow.createCell(0);
        cell.setCellValue("Tổng cộng: " + formatCurrency(sum) + " VND");
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

        dataRow = sheet.createRow(i+2);
        cell = dataRow.createCell(3);
        CellStyle style = createCellStyleDate(sheet, workbook);

        cell.setCellStyle(style);
        cell.setCellValue(formatDate());

        dataRow = sheet.createRow(i+3);
        cell = dataRow.createCell(3);
        cell.setCellValue("Người lập báo cáo");
        cell.setCellStyle(createCellStyleBC(sheet, workbook));

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User user = null;
        if (principal instanceof String && !((String) principal).isEmpty()) {
            user = userService.findByEmail((String) principal);
        }

        dataRow = sheet.createRow(i + 4);
        cell = dataRow.createCell(3);
        cell.setCellStyle(createCellStyleBC(sheet, workbook));

        if (user != null) {
            cell.setCellValue(user.getName());
        } else {
            cell.setCellValue("Nguyễn Thị Thùy Linh");
        }

//        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=revenue-room.xlsx");

        workbook.write(response.getOutputStream());
        workbook.close();
    }

    @GetMapping("stats-rooms")
    public ResponseEntity<ResponseDTO<List<RoomStats>>> statsRooms(
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate
    ) throws Exception{
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

        Date start = dateFormat.parse(startDate);
        Date end = dateFormat.parse(endDate);

        List<Object[]> result = bookingRepo.calculateRoomRevenueAndBookings(start, end);

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
                    double a = Double.parseDouble(revenue);
                    String count = object[3].toString();

                    RoomStats roomStats1 = new RoomStats();
                    roomStats1.setName(name);
                    roomStats1.setType("Doanh thu");
                    roomStats1.setValue((long) a);

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
//        List<Object[]> a = this.bookingRepo.calculateRoomRevenueAndBookings(2023);
//
//        System.out.println(new Gson().toJson(a));
//
//    }

    @GetMapping("revenue")
    public ResponseEntity<ResponseDTO<Revenue[]>> statsRevenue(@RequestParam("year") int year){
        return ResponseEntity.ok(new ResponseDTO<>(this.revenueMonth(year), "200", "Success", true));
    }

    @GetMapping("stats-service")
    public ResponseEntity<ResponseDTO<List<RevenueByService>>> revenueByService(
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate
    ) throws Exception{
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

        Date start = dateFormat.parse(startDate);
        Date end = dateFormat.parse(endDate);

        List<Object[]> result = bookingRepo.calculateRevenueByService(start, end);

//        if(day != null) {
//            if(month == null) throw new Exception("Du lieu dinh dang ko dung");
//
//            result = bookingRepo.calculateRevenueByService(year, month, day);
//
//        }
//        else if(month != null){
//            result = bookingRepo.calculateRevenueByService(year, month);
//        }
//        else result = bookingRepo.calculateRevenueByService(year);


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

    private List<RevenueByServiceExcel> statsService(Date startDate, Date endDate) throws Exception{
        List<Object[]> result = bookingRepo.calculateRevenueByService(startDate, endDate);


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
                    revenueByService.setBookingCount(Integer.parseInt(object[3].toString()));
                    revenueByService.setPrice(Long.parseLong(object[4].toString()));
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

    private List<RoomStatsExcel> statsRoom(Date startDate, Date endDate) throws Exception{
        List<Object[]> result = bookingRepo.calculateRoomRevenueAndBookings(startDate, endDate);

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
                    roomStats1.setValue((long) Double.parseDouble(revenue));
                    roomStats1.setBookingCount(Integer.parseInt(object[3].toString()));
                    roomStats1.setPrice((long) Double.parseDouble((object[4].toString())));

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

    private static void writeHeaderForService(Sheet sheet, int rowIndex, String header) {
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
        cell.setCellValue("STT");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(1);
        cell.setCellValue("Loại DV");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(2);
        cell.setCellValue("Số lần thuê");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(3);
        cell.setCellValue("Đơn giá");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(4);
        cell.setCellValue("Doanh thu");
        cell.setCellStyle(cellStyle);

    }

    private static void writeSubHeader(Sheet sheet, int rowIndex, String header) {
        // create CellStyle
        CellStyle cellStyle = createStyleForHeader(sheet);
        Font font = sheet.getWorkbook().createFont();
        font.setFontName("Times New Roman");
        font.setItalic(true);
        font.setFontHeightInPoints((short) 12); // font size

        CellStyle cellStyle1 = sheet.getWorkbook().createCellStyle();
        cellStyle1.setFont(font);
//        cellStyle1.setBorderBottom(BorderStyle.THIN);
//        cellStyle1.setBorderLeft(BorderStyle.THIN);
//        cellStyle1.setBorderRight(BorderStyle.THIN);
//        cellStyle1.setBorderTop(BorderStyle.THIN);

        Row row1 = sheet.createRow(rowIndex);
        row1.setHeight((short) 550);
        Cell cell2 = row1.createCell(0);
        cell2.setCellValue(header);
        cellStyle1.setAlignment(HorizontalAlignment.CENTER);
        cellStyle1.setVerticalAlignment(VerticalAlignment.CENTER);
        cell2.setCellStyle(cellStyle1);
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
        cell.setCellValue("STT");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(1);
        cell.setCellValue("Loại phòng");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(2);
        cell.setCellValue("Số lần thuê");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(3);
        cell.setCellValue("Đơn giá");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(4);
        cell.setCellValue("Doanh thu");
        cell.setCellStyle(cellStyle);

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

        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

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


        sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, sizeMerge));

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

    private CellStyle createCellStyleDate(Sheet sheet, Workbook workbook) {
        short format = (short)BuiltinFormats.getBuiltinFormat("#,##0");

        Font font = sheet.getWorkbook().createFont();
        font.setFontName("Times New Roman");
        font.setFontHeightInPoints((short) 12); // font size
        font.setItalic(true);

        //Create CellStyle
        CellStyle cellStyleFormatNumber = workbook.createCellStyle();
        cellStyleFormatNumber.setDataFormat(format);
        cellStyleFormatNumber.setFont(font);
        cellStyleFormatNumber.setAlignment(HorizontalAlignment.CENTER);
        cellStyleFormatNumber.setVerticalAlignment(VerticalAlignment.CENTER);

        return cellStyleFormatNumber;
    }

    private CellStyle createCellStyleBC(Sheet sheet, Workbook workbook) {
        short format = (short)BuiltinFormats.getBuiltinFormat("#,##0");

        Font font = sheet.getWorkbook().createFont();
        font.setFontName("Times New Roman");
        font.setFontHeightInPoints((short) 13); // font size

        //Create CellStyle
        CellStyle cellStyleFormatNumber = workbook.createCellStyle();
        cellStyleFormatNumber.setDataFormat(format);
        cellStyleFormatNumber.setFont(font);
        cellStyleFormatNumber.setAlignment(HorizontalAlignment.CENTER);
        cellStyleFormatNumber.setVerticalAlignment(VerticalAlignment.CENTER);

        return cellStyleFormatNumber;
    }
}
