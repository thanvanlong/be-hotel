package com.tl.hotelproject.repo;

import com.tl.hotelproject.entity.client.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepo extends JpaRepository<Client, String> {
}
