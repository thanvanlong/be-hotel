package com.tl.hotelproject.service.services;

import com.tl.hotelproject.entity.Metadata;
import com.tl.hotelproject.entity.room.Room;
import com.tl.hotelproject.entity.services.Services;
import com.tl.hotelproject.entity.services.UsedServices;
import com.tl.hotelproject.repo.ServicesRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ServicesServiceImpl implements ServicesService{
    @Autowired
    private ServicesRepo servicesRepo;

    @Override
    public Services findById(String id) throws Exception{

            Optional<Services> services = this.servicesRepo.findById(id);
            if(services.isPresent())
                return services.get();
            throw new Exception("Không tìm thấy dich vu với ID đã cho");

    }

    @Override
    public String save(UsedServices usedServices) throws Exception {
        return null;
    }

    @Override
    public Map<String, Object> pagingSort(int page, int limit) {
        Pageable pagingSort = PageRequest.of(page, limit);
        Page<Services> servicesPage = servicesRepo.findAll(pagingSort);

        Metadata metadata = new Metadata();
        metadata.setPageNumber(servicesPage.getNumber());
        metadata.setPageSize(servicesPage.getSize());
        metadata.setTotalPages(servicesPage.getTotalPages());
        metadata.setTotalItems(servicesPage.getTotalElements());

        Map<String, Object> response = new HashMap<>();
        response.put("results", servicesPage.getContent());
        response.put("metadata", metadata);
        return response;
    }
}
