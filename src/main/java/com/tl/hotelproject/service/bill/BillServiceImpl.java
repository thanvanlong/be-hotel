package com.tl.hotelproject.service.bill;

import com.mservice.config.Environment;
import com.mservice.enums.RequestType;
import com.mservice.models.PaymentResponse;
import com.mservice.processor.CreateOrderMoMo;
import com.mservice.shared.utils.LogUtils;
import com.tl.hotelproject.dtos.bills.VnpayCreatePaymentDto;
import com.tl.hotelproject.entity.bill.Bill;
import com.tl.hotelproject.entity.bill.PaymentState;
import com.tl.hotelproject.entity.bill.PaymentType;
import com.tl.hotelproject.entity.booking.Booking;
import com.tl.hotelproject.entity.booking.BookingState;
import com.tl.hotelproject.entity.client.Client;
import com.tl.hotelproject.repo.BillRepo;
import com.tl.hotelproject.repo.BookingRepo;
import com.tl.hotelproject.service.mail.EmailSender;
import com.tl.hotelproject.utils.StringUtils;
import com.tl.hotelproject.utils.VnpayUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BillServiceImpl implements BillService{
    public static final Map<String, String> responseCode = new HashMap<>();

    static {
        responseCode.put("00", "Giao dịch thành công");
        responseCode.put("07", "Trừ tiền thành công. Giao dịch bị nghi ngờ (liên quan tới lừa đảo, giao dịch bất thường");
        responseCode.put("09", "Giao dịch không thành công do: Thẻ/Tài khoản của khách hàng chưa đăng ký dịch vụ InternetBanking tại ngân hàng.");
        responseCode.put("10", "Giao dịch không thành công do: Khách hàng xác thực thông tin thẻ/tài khoản không đúng quá 3 lần");
        responseCode.put("11", "Giao dịch không thành công do: Đã hết hạn chờ thanh toán. Xin quý khách vui lòng thực hiện lại giao dịch.");
        responseCode.put("12", "Giao dịch không thành công do: Thẻ/Tài khoản của khách hàng bị khóa.");
        responseCode.put("13", "Giao dịch không thành công do Quý khách nhập sai mật khẩu xác thực giao dịch (OTP). Xin quý khách vui lòng thực hiện lại giao dịch.");
        responseCode.put("24", "Giao dịch không thành công do: Khách hàng hủy giao dịch");
        responseCode.put("51", "Giao dịch không thành công do: Tài khoản của quý khách không đủ số dư để thực hiện giao dịch.");
        responseCode.put("65", "Giao dịch không thành công do: Tài khoản của Quý khách đã vượt quá hạn mức giao dịch trong ngày.");
        responseCode.put("75", "Ngân hàng thanh toán đang bảo trì.");
        responseCode.put("79", "Giao dịch không thành công do: KH nhập sai mật khẩu thanh toán quá số lần quy định. Xin quý khách vui lòng thực hiện lại giao dịch");
        responseCode.put("99", "Các lỗi khác (lỗi còn lại, không có trong danh sách mã lỗi đã liệt kê");
    }

    @Autowired
    private BillRepo billRepo;

    @Autowired
    private BookingRepo bookingRepo;

    @Autowired
    private VnpayUtils vnpayUtils;

    @Autowired
    private EmailSender emailSender;

    @Override
    public Bill getBillById(String id) throws Exception{
        Optional<Bill> bill = billRepo.findById(id);
        if(bill.isPresent()) return bill.get();
        throw new Exception("bill khong ton tai");
    }

    @Override
    public Bill getBillByOrderId(String orderId) throws Exception {
        Optional<Bill> bill = billRepo.findByOrderId(orderId);
        if(bill.isPresent()) return bill.get();
        throw new Exception("bill khong ton tai");
    }


    @Override
    public String initBill(Booking booking, PaymentType type) throws Exception{
        Bill bill = booking.getBills().get(0);
        String url = "";
        if (type == PaymentType.Momo){
            bill.setPaymentType(PaymentType.Momo);
            bill.setRequestId(UUID.randomUUID().toString());

            LogUtils.init();
            Environment environment = Environment.selectEnv("dev");

            PaymentResponse captureWalletMoMoResponse = null;
            try {
                captureWalletMoMoResponse = CreateOrderMoMo.process(environment,
                        bill.getOrderId(),
                        bill.getRequestId(),
                        Long.toString(bill.getTotalAmount()),
                        "Thanh toán Momo",
                        "momosdk:/",
                        "momosdk:/",
                        "",
                        RequestType.CAPTURE_WALLET,
                        Boolean.TRUE);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            System.out.println(captureWalletMoMoResponse);
            url = captureWalletMoMoResponse.getPayUrl();
        } else if (type == PaymentType.Zalopay) {
            bill.setPaymentType(PaymentType.Zalopay);

        } else if (type == PaymentType.Vnpay) {
            bill.setPaymentType(PaymentType.Vnpay);

            VnpayCreatePaymentDto createPaymentDto = new VnpayCreatePaymentDto();
            createPaymentDto.setAmount(bill.getTotalAmount());
            createPaymentDto.setOrderInfo("Thanh toán VNPAY");
            createPaymentDto.setOrderId(bill.getOrderId());
            url = vnpayUtils.createPayment(createPaymentDto);
        }
        billRepo.save(bill);
        Map<String, Object> body = new HashMap<>();
        body.put("roomName", booking.getRoom().getName());
        body.put("quantity", booking.getQuantity());
        body.put("price", booking.getPrice() * booking.getQuantity());
        body.put("id", booking.getId());
        body.put("createdDate", new Date().getTime());
        body.put("paymentDate", new Date().getTime());
        body.put("s", false);
        body.put("services", new ArrayList<>());
        body.put("totalAmount", bill.getTotalAmount());

        emailSender.send(booking.getClient().getEmail(), "", "Ban co don hanh can thanh toan", body, "invoice.html");

        return url;
    }

    @Override
    public String fulfilledBill(String orderId) throws Exception{
        Bill bill = this.getBillByOrderId(orderId);
        bill.setPaymentState(PaymentState.Fulfilled);
        Booking booking = bookingRepo.getBookingByBill(bill.getId());
        booking.setBookingState(BookingState.Success);
        bookingRepo.save(booking);
        billRepo.save(bill);

        Map<String, Object> body = new HashMap<>();
        body.put("roomName", booking.getRoom().getName());
        body.put("quantity", booking.getQuantity());
        body.put("price", bill.getTotalAmount());
        body.put("id", booking.getId());
        body.put("createdDate", new Date().getTime());
        body.put("paymentDate", new Date().getTime());
        body.put("s", false);
        body.put("services", new ArrayList<>());
        body.put("totalAmount", bill.getTotalAmount());
        emailSender.send(booking.getClient().getEmail(), "", "Thanh toan thanh cong", body, "invoice.html");
        return "done";
    }

    @Override
    public String rejectBill(String orderId, String code) throws Exception{
        Bill bill = this.getBillByOrderId(orderId);
        bill.setPaymentState(PaymentState.Reject);
        Booking booking = bookingRepo.getBookingByBill(bill.getId());
        booking.setBookingState(BookingState.Reject);
        bookingRepo.save(booking);
        billRepo.save(bill);

        emailSender.send(booking.getClient().getEmail(), responseCode.get(code), "Thanh toan that bai", new HashMap<>() , "invoice.html");
        return "reject";
    }
}
