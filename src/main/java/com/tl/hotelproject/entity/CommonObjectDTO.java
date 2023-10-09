package com.tl.hotelproject.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public abstract class CommonObjectDTO {
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
}
