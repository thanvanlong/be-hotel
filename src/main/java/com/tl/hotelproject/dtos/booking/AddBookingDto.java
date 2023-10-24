package com.tl.hotelproject.dtos.booking;

import com.tl.hotelproject.entity.bill.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddBookingDto {

    // client
    private String firstName;
    private String lastName;
    private String sex;
    private String email;
    private String tel;

    // bookedRoom
    private Date checkin;
    private Date checkout;
    private int quantity;
//    private double price;

    // Room
    private String idRoom;

    private PaymentType paymentType;
}
