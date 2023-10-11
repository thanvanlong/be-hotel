package com.tl.hotelproject.service.zoom;


import com.tl.hotelproject.entity.room.Room;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface RoomService {
    void save(Room room) throws Exception;
    void update(Room room);
    void delete(Room room);
    void find(String id);
    Map<String, Object> pagingSort(int page, int limit);

    Room getRoomWithFeature(String id);
}
