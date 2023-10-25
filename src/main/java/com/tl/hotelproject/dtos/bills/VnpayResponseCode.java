package com.tl.hotelproject.dtos.bills;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VnpayResponseCode {
    private int vnp_Amount;
    private String vnp_BankCode;
    private String vnp_CardType;
    private String vnp_OrderInfo;
    private int vnp_PayDate;
    private int vnp_ResponseCode;
    private String vnp_TmnCode;
    private int vnp_TransactionNo;
    private String vnp_TransactionStatus;
    private String vnp_TxnRef;
    private String vnp_SecureHash;
}
