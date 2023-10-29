package com.tl.hotelproject.dtos.services;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddServicesDto {
    private String name;
    private String unity;
    private int price;
    private String description;
}
