package com.tl.hotelproject.entity.client;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tl.hotelproject.entity.CommonObjectDTO;
import com.tl.hotelproject.entity.booking.Booking;
import com.tl.hotelproject.utils.StringUtils;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "client")
public class Client extends CommonObjectDTO {
    @Id
    @UuidGenerator
    private String id;

    private String firstName;
    private String lastName;
    private String name;
    private String address;
    private String sex;
    private String email;
    private String tel;
    private String cccd;
    private String note;

    @OneToMany(mappedBy = "client")
    @JsonIgnore
    private List<Booking> bookingList;

    public void setName(){
        this.name = StringUtils.removeAccents(this.getFirstName() + " " + this.getLastName());
    }
}
