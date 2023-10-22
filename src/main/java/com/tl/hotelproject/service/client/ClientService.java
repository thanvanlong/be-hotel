package com.tl.hotelproject.service.client;

import com.tl.hotelproject.entity.client.Client;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface ClientService {
    void save(Client client) throws Exception;

    Client getOne(String id) throws Exception;

    Map<String, Object> pagingSort(int page, int limit);
    Map<String, Object> pagingSortSearch(int page, int limit, String search);
}
