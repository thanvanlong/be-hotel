package com.tl.hotelproject.service.client;

import com.tl.hotelproject.entity.client.Client;
import com.tl.hotelproject.repo.ClientRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService{
    @Autowired
    private ClientRepo clientRepo;

    @Override
    public void save(Client client) throws Exception {
        clientRepo.save(client);
    }

    @Override
    public Map<String, Object> pagingSort(int page, int limit) {
        return null;
    }
}