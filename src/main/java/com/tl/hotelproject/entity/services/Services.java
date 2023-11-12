package com.tl.hotelproject.entity.services;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @Column(unique = true)
    private String name;
    private String unity;
    private int price;
    private String description;
    private String image;

    @OneToMany(mappedBy = "services")
    @JsonIgnore
    private List<UsedServices> usedServices;
}
