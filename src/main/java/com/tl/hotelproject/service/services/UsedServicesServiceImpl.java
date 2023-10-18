package com.tl.hotelproject.service.services;

import com.tl.hotelproject.entity.services.UsedServices;
import com.tl.hotelproject.repo.UsedServicesRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsedServicesServiceImpl implements UsedServicesService{
    @Autowired
    private UsedServicesRepo usedServicesRepo;

    @Override
    public UsedServices save(UsedServices usedServices) throws Exception {
        return usedServicesRepo.save(usedServices);
    }

//    @Override
//    public UsedServices findById(String id) throws Exception {
//        Optional<UsedServices> usedServices = this.usedServicesRepo.findById(id);
//
//        if(usedServices.isPresent()) {
//            return usedServices.get();
//        }
//        throw new Exception("Khong ton dich vu");
//    }
}
