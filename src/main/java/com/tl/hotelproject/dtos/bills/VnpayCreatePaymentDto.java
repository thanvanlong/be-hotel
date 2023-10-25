package com.tl.hotelproject.dtos.bills;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VnpayCreatePaymentDto {
    private int amount;
    private String orderId;
    private String orderInfo;

    private String bankCode;
    private String language = "vn";
}
