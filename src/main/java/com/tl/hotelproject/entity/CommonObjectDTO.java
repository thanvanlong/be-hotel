package com.tl.hotelproject.entity;

import jakarta.persistence.Id;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;

@Data
public abstract class CommonObjectDTO {
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
}
