package com.tl.hotelproject.service.services;

import com.tl.hotelproject.entity.services.Services;
import com.tl.hotelproject.entity.services.UsedServices;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface ServicesService {

    Services findById(String id) throws Exception;
    String save(UsedServices usedServices) throws  Exception;
    Map<String, Object> pagingSort(int page, int limit);
    Map<String, Object> search(int page, int limit, String search);

    String save(Services services) throws Exception;

    String update(Services services) throws Exception;

    Services getByName(String name) throws Exception;
}
