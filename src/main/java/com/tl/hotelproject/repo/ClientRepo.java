package com.tl.hotelproject.repo;

import com.tl.hotelproject.entity.client.Client;
import com.tl.hotelproject.entity.room.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepo extends JpaRepository<Client, String> {
    Page<Client> findByNameContaining(String search, Pageable pageable);
}
