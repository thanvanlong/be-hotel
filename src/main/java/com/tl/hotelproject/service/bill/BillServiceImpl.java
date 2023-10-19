package com.tl.hotelproject.service.bill;

import com.tl.hotelproject.entity.bill.Bill;
import com.tl.hotelproject.entity.bill.PaymentState;
import com.tl.hotelproject.repo.BillRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BillServiceImpl implements BillService{
    @Autowired
    private BillRepo billRepo;

    @Override
    public Bill getBillById(String id) throws Exception{
        Optional<Bill> bill = billRepo.findById(id);
        if(bill.isPresent()) return bill.get();
        throw new Exception("bill khong ton tai");
    }

    @Override
    public void initBill(Bill bill) {
        billRepo.save(bill);
    }

    @Override
    public void fulfilledBill(String id) throws Exception{
        Bill bill = this.getBillById(id);
        bill.setPaymentState(PaymentState.Fulfilled);
        billRepo.save(bill);
    }

    @Override
    public void rejectBill(String id) throws Exception{
        Bill bill = this.getBillById(id);
        bill.setPaymentState(PaymentState.Reject);
        billRepo.save(bill);
    }
}
