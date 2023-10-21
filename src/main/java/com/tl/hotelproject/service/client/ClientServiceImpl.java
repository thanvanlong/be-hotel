package com.tl.hotelproject.service.client;

import com.tl.hotelproject.entity.Metadata;
import com.tl.hotelproject.entity.client.Client;
import com.tl.hotelproject.entity.room.Room;
import com.tl.hotelproject.repo.ClientRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
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
        Pageable pagingSort = PageRequest.of(page, limit);
        Page<Client> clientPage = clientRepo.findAll(pagingSort);

        Metadata metadata = new Metadata();
        metadata.setPageNumber(clientPage.getNumber());
        metadata.setPageSize(clientPage.getSize());
        metadata.setTotalPages(clientPage.getTotalPages());
        metadata.setTotalItems(clientPage.getTotalElements());

        Map<String, Object> response = new HashMap<>();
        response.put("results", clientPage.getContent());
        response.put("metadata", metadata);


        return response;
    }

    @Override
    public Map<String, Object> pagingSortSearch(int page, int limit, String search) {
        Pageable pagingSort = PageRequest.of(page, limit);
        Page<Client> clientPage = clientRepo.findByNameContaining(search, pagingSort);

        Metadata metadata = new Metadata();
        metadata.setPageNumber(clientPage.getNumber());
        metadata.setPageSize(clientPage.getSize());
        metadata.setTotalPages(clientPage.getTotalPages());
        metadata.setTotalItems(clientPage.getTotalElements());

        Map<String, Object> response = new HashMap<>();
        response.put("results", clientPage.getContent());
        response.put("metadata", metadata);


        return response;
    }
}
