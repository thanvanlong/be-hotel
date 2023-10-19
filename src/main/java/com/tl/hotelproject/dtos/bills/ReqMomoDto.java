package com.tl.hotelproject.dtos.bills;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReqMomoDto {
    private String partnerCode;
    private String orderId;
    private String requestId;
    private double amount;
    private String orderInfo;
    private String orderType;
    private String transId;
    private int resultCode;
    private String message;
    private String payType;
    private int responseTime;
    private String extraDate;
    private String signature;

}
