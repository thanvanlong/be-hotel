package com.tl.hotelproject.service.client;

import com.tl.hotelproject.entity.client.Client;
import org.springframework.stereotype.Service;

@Service
public interface ClientService {
    void save(Client client) throws Exception;
}
