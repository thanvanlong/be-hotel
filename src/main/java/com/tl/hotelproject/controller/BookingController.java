package com.tl.hotelproject.controller;

import com.mservice.config.Environment;
import com.mservice.enums.RequestType;
import com.mservice.models.PaymentResponse;
import com.mservice.processor.CreateOrderMoMo;
import com.mservice.shared.utils.LogUtils;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.tl.hotelproject.dtos.booking.AddBookingDto;
import com.tl.hotelproject.dtos.booking.UpdateUsedServicesDto;
import com.tl.hotelproject.entity.ResponseDTO;
import com.tl.hotelproject.entity.booking.Booking;
import com.tl.hotelproject.entity.promotion.Promotion;
import com.tl.hotelproject.entity.services.UsedServices;
import com.tl.hotelproject.repo.BookingRepo;
import com.tl.hotelproject.service.booking.BookingService;
import com.tl.hotelproject.service.mail.EmailSender;
import com.tl.hotelproject.service.promotion.PromotionService;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.sql.Timestamp;
import java.util.*;

@RestController
@RequestMapping("api/v1/booking")
@CrossOrigin("*")
public class BookingController {

    @Autowired
    private PromotionService promotionService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingRepo bookingRepo;

    @Autowired
    private EmailSender emailSender;

    @GetMapping("/export-bill")
    public ResponseEntity<byte[]> downloadPDF(@RequestParam("id") String id) throws Exception {
        Booking booking = this.bookingService.findById(id);
        booking.setTotalAmount();

        bookingRepo.save(booking);

        List<UsedServices> services = booking.getUsedServices();

        Map<String, Object> body = new HashMap<>();
        body.put("roomName", booking.getRoom().getName());
        body.put("quantity", booking.getQuantity());
        body.put("price", booking.getPrice() * booking.getQuantity());
        body.put("id", booking.getId());
        body.put("createdDate", new Date().getTime());
        body.put("paymentDate", new Date().getTime());
        body.put("s", services == null ? false : true);
        body.put("services", services);
        body.put("totalAmount", booking.getTotalAmount());

        String html = emailSender.initContent(body, "invoice.html");
//        Document document = Jsoup.parse(html, "UTF-8");
//        document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);

        String outPath ="export/"+ id+"_invoice.pdf";
        PdfRendererBuilder builder = new PdfRendererBuilder();
        builder.useFastMode();
        builder.withHtmlContent(html, null);
        ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
        builder.toStream(pdfOutputStream);
        builder.run();

        byte[] pdfBytes = pdfOutputStream.toByteArray();


        System.out.println("Tệp PDF đã được tạo: " + outPath);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=invoice.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @GetMapping("search")
    public ResponseEntity<ResponseDTO<Map<String, Object>>> search(@RequestParam(defaultValue = "0") int page,
                                                                       @RequestParam(defaultValue = "10") int limit,
                                                                       @RequestParam(defaultValue = "id,desc") String[] sort,
                                                                       @RequestParam("search") String search) {

        Map<String, Object> bookingSearch = bookingService.search(search, page, limit);


        return ResponseEntity.ok(new ResponseDTO<>(bookingSearch, "200", "Success", true));
    }

    @PostMapping("client-booking")
    public ResponseEntity<ResponseDTO<String>> booking(@RequestBody AddBookingDto body) {
        try {
            Promotion promotion = promotionService.getPromotionByStartDateAndEndDate();
            if (promotion != null) {
                return ResponseEntity.ok(new ResponseDTO<>(this.bookingService.save(body, promotion.getDiscount(), true), "200","Success", true));
            }

            return ResponseEntity.ok(new ResponseDTO<>(this.bookingService.save(body, 0, true), "200","Success", true));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("admin-booking")
    public ResponseEntity<ResponseDTO<String>> adminBooking(@RequestBody AddBookingDto body) {
        try {
            Promotion promotion = promotionService.getPromotionByStartDateAndEndDate();
            if (promotion != null) {
                return ResponseEntity.ok(new ResponseDTO<>(this.bookingService.save(body, promotion.getDiscount(), false), "200","Success", true));
            }

            return ResponseEntity.ok(new ResponseDTO<>(this.bookingService.save(body, 0, false), "200","Success", true));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("check-in/{id}")
    public ResponseEntity<ResponseDTO<String>> checkIn(@PathVariable("id") String id) throws Exception{
        return ResponseEntity.ok(new ResponseDTO<>( this.bookingService.checkIn(id), "200", "success", true));
    }

    @PostMapping("check-out/{id}")
    public ResponseEntity<ResponseDTO<String>> checkOut(@PathVariable("id") String id) throws Exception{
        return ResponseEntity.ok(new ResponseDTO<>( this.bookingService.checkOut(id), "200", "success", true));
    }

    @PutMapping("update-service/{id}")
    public ResponseEntity<ResponseDTO<String>> updateService(@PathVariable("id") String id, @Valid @RequestBody UpdateUsedServicesDto[] body) throws Exception{
        return ResponseEntity.ok(new ResponseDTO<>(this.bookingService.updateUsedService(id, body), "200", "Success", true));
    }

    @GetMapping("list")
    public ResponseEntity<ResponseDTO<Map<String, Object>>> listBooking(@RequestParam(defaultValue = "0") int page,
                                                                         @RequestParam(defaultValue = "10") int limit,
                                                                         @RequestParam(defaultValue = "id,desc") String[] sort,
                                                                         @RequestParam(required = false) String filter) {

        Map<String, Object> bookingList = bookingService.pagingSort(page, limit);


        return ResponseEntity.ok(new ResponseDTO<>(bookingList, "200", "Success", true));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<Booking>> getBooking(@PathVariable("id") String id) throws Exception{
        return ResponseEntity.ok(new ResponseDTO<>(this.bookingService.findById(id), "200", "Success", true));
    }

}
