package com.tl.hotelproject.repo;

import com.tl.hotelproject.entity.room.FeatureRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeatureRoomRepo extends JpaRepository<FeatureRoom, String> {
}
