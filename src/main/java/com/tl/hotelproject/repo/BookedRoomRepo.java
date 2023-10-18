package com.tl.hotelproject.repo;

import com.tl.hotelproject.entity.booking.BookedRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookedRoomRepo extends JpaRepository<BookedRoom, String> {

//    @Query("SELECT bk FROM BookedRoom bk LEFT JOIN FETCH bk.usedServices WHERE bk.id = :bkId")
//    BookedRoom getBookedRoomWithRelationship(@Param("bkId") String bkId);
}
