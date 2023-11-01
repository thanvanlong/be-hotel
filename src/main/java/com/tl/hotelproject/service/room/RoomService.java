package com.tl.hotelproject.service.room;


import com.tl.hotelproject.entity.room.Room;
import com.tl.hotelproject.entity.room.RoomName;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public interface RoomService {
    String save(Room room) throws Exception;
    void update(Room room) throws Exception;

    String updateRoomName(Room room) throws Exception;

    RoomName checkRoomName(String name) throws Exception;
    void delete(Room room);
    void find(String id);

    Room findById(String id) throws Exception;
    Map<String, Object> pagingSort(int page, int limit);

    Room getRoomWithFeature(String id);
    Room getRoomBySlugWithFeature(String slug);

    Map<String, Object> pagingSortSearch(int page, int limit, String search);

    String revertRoom(String id, List<String> roomName) throws Exception;
}
