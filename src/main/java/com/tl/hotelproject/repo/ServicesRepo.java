package com.tl.hotelproject.repo;

import com.tl.hotelproject.entity.client.Client;
import com.tl.hotelproject.entity.services.Services;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServicesRepo extends JpaRepository<Services, String> {

    @Query("SELECT s.name, s.id, s.unity, s.createdAt, s.description FROM Services s WHERE s.isDelete = false")
    List<Object[]> listServiceSelect();

    @Query("SELECT s FROM Services s WHERE s.isDelete = false and s.name = :name")
    Optional<Services> findByName(String name);

    Page<Services> findByNameContaining(String search, Pageable pageable);
}
