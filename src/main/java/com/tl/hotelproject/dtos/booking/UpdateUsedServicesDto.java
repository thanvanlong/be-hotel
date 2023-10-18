package com.tl.hotelproject.dtos.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUsedServicesDto {
    private String id;
    private String idService;
    private int quantity;
}
