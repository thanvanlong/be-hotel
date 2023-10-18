package com.tl.hotelproject.repo;

import com.tl.hotelproject.entity.services.Services;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServicesRepo extends JpaRepository<Services, String> {
}
