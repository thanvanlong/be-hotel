package com.tl.hotelproject.service.bill;

import com.tl.hotelproject.entity.bill.Bill;
import org.springframework.stereotype.Service;

@Service
public interface BillService {
    Bill getBillById(String id) throws Exception;
    void initBill(Bill bill);
    void fulfilledBill(String id) throws Exception;
    void rejectBill(String id) throws Exception;
}
