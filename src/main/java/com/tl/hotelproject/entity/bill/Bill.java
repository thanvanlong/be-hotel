package com.tl.hotelproject.entity.bill;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tl.hotelproject.entity.CommonObjectDTO;
import com.tl.hotelproject.entity.booking.Booking;
import com.tl.hotelproject.entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "bill")
public class Bill extends CommonObjectDTO {
    @Id
    @UuidGenerator
    private String id;

    private Date paymentDate = new Date();
    private int totalAmount;
    private String note;
    private String orderId;
    private String requestId;

    @Enumerated(EnumType.ORDINAL)
    private PaymentType paymentType;

    @Enumerated(EnumType.ORDINAL)
    private PaymentFor paymentFor = PaymentFor.Hotel;

    @Enumerated(EnumType.ORDINAL)
    private PaymentState paymentState = PaymentState.Pending;

    @ManyToOne()
    @JoinColumn(name = "booking_id")
    @JsonIgnore
    private Booking booking;

    @ManyToOne()
    @JoinColumn(name = "user_id")
    private User user;

}
