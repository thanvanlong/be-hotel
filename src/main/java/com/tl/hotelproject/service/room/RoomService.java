package com.tl.hotelproject.service.room;


import com.tl.hotelproject.entity.room.Room;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public interface RoomService {
    void save(Room room) throws Exception;
    void update(Room room);
    void delete(Room room);
    void find(String id);

    Room findById(String id) throws Exception;
    Map<String, Object> pagingSort(int page, int limit);

    Room getRoomWithFeature(String id);
    Room getRoomBySlugWithFeature(String slug);

    Map<String, Object> pagingSortSearch(int page, int limit, String search);


}
