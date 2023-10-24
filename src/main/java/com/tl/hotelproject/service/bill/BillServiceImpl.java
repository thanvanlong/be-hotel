package com.tl.hotelproject.service.bill;

import com.tl.hotelproject.dtos.bills.VnpayCreatePaymentDto;
import com.tl.hotelproject.entity.bill.Bill;
import com.tl.hotelproject.entity.bill.PaymentState;
import com.tl.hotelproject.entity.bill.PaymentType;
import com.tl.hotelproject.repo.BillRepo;
import com.tl.hotelproject.utils.VnpayUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BillServiceImpl implements BillService{
    @Autowired
    private BillRepo billRepo;

    @Autowired
    private VnpayUtils vnpayUtils;

    @Override
    public Bill getBillById(String id) throws Exception{
        Optional<Bill> bill = billRepo.findById(id);
        if(bill.isPresent()) return bill.get();
        throw new Exception("bill khong ton tai");
    }

    @Override
    public String initBill(Bill bill, PaymentType type) throws Exception{
        String url = "";
        if (type == PaymentType.Momo){
            bill.setPaymentType(PaymentType.Momo);
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
        return url;
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
