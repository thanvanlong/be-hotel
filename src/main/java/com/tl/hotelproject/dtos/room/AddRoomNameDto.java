package com.tl.hotelproject.dtos.room;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddRoomNameDto {
    private String name;
    private String id;
}
