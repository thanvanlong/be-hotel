package com.tl.hotelproject.repo;

import com.tl.hotelproject.entity.room.RoomName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoomNameRepo extends JpaRepository<RoomName, String> {

    Optional<RoomName> findByName(String name);
    Optional<RoomName> findBySearch(String name);
}
