package com.tl.hotelproject.service.bill;

import com.tl.hotelproject.entity.bill.Bill;
import com.tl.hotelproject.entity.bill.PaymentType;
import com.tl.hotelproject.entity.booking.Booking;
import com.tl.hotelproject.entity.client.Client;
import org.springframework.stereotype.Service;

@Service
public interface BillService {
    Bill getBillById(String id) throws Exception;
    Bill getBillByOrderId(String orderId) throws Exception;
    String initBill(Booking booking, PaymentType type) throws Exception;
    String fulfilledBill(String orderId) throws Exception;
    String rejectBill(String id, String code) throws Exception;

    String setBillServices(Bill bill) throws Exception;

    String setBillDone(String id) throws Exception;

}
