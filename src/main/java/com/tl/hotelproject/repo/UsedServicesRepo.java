package com.tl.hotelproject.repo;

import com.tl.hotelproject.entity.services.UsedServices;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsedServicesRepo extends JpaRepository<UsedServices, String> {
}
