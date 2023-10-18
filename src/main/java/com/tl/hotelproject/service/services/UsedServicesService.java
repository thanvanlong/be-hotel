package com.tl.hotelproject.service.services;

import com.tl.hotelproject.entity.services.UsedServices;
import org.springframework.stereotype.Service;

@Service
public interface UsedServicesService {
    UsedServices save(UsedServices usedServices) throws Exception;

//    UsedServices findById(String id) throws Exception;
}
