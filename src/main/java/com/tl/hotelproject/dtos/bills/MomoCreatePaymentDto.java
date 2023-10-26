package com.tl.hotelproject.dtos.bills;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MomoCreatePaymentDto {
    private String requestId;
    private String orderId;
    private int amount;
    private String orderInfo;
    private String returnURL;
    private String notifyURL;
}
