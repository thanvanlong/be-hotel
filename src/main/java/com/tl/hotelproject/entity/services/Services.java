package com.tl.hotelproject.entity.services;

import com.tl.hotelproject.entity.CommonObjectDTO;
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
@Table(name = "services")
public class Services extends CommonObjectDTO {
    @Id
    @UuidGenerator
    private String id;

    private String name;
    private String unity;
    private double price;
    private String description;

    @OneToMany(mappedBy = "services")
    private List<UsedServices> usedServices;
}
