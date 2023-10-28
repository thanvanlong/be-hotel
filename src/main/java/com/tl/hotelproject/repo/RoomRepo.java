package com.tl.hotelproject.repo;

import com.tl.hotelproject.entity.room.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository()
public interface RoomRepo extends JpaRepository<Room, String> {

    Page<Room> findBySearchContaining(String search, Pageable pageable);

    Room getRoomByName(String name);

    @Query("SELECT r FROM Room r LEFT JOIN FETCH r.featureRooms WHERE r.id = :roomId")
    Room findRoomWithFeatureRooms(@Param("roomId") String roomId);

    @Query("SELECT r FROM Room r LEFT JOIN FETCH r.featureRooms LEFT join FETCH r.images WHERE r.slug = :slug")
    Room findRoomBySlugWithFeatureRooms(@Param("slug") String slug);

    @Query("SELECT r.name, r.id FROM Room r WHERE r.isDelete = false ")
    List<Object[]> listRoomSelect();
}
