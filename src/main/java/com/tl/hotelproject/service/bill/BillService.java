package com.tl.hotelproject.service.bill;

import com.tl.hotelproject.entity.bill.Bill;
import com.tl.hotelproject.entity.bill.PaymentType;
import org.springframework.stereotype.Service;

@Service
public interface BillService {
    Bill getBillById(String id) throws Exception;
    String initBill(Bill bill, PaymentType type) throws Exception;
    void fulfilledBill(String id) throws Exception;
    void rejectBill(String id) throws Exception;
}
