package com.tl.hotelproject.service.zoom;

import com.tl.hotelproject.entity.room.Room;
import com.tl.hotelproject.repo.RoomRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {
    @Autowired
    private RoomRepo roomRepo;

    @Override
    public void save(Room room) throws Exception {
        roomRepo.save(room);
    }

    @Override
    public void update(Room room) {

    }

    @Override
    public void delete(Room room) {

    }

    @Override
    public void find(String id) {

    }

    @Override
    public  Map<String, Object> pagingSort(int page, int limit) {
        Pageable pagingSort = PageRequest.of(page, limit);
        Page<Room> roomPage = roomRepo.findAll(pagingSort);

        Map<String, Number> metadata = new HashMap<>();
        metadata.put("totalPages",roomPage.getTotalPages());
        metadata.put("currentPage", roomPage.getNumber());
        metadata.put("totalItems", roomPage.getTotalElements());

        Map<String, Object> response = new HashMap<>();
        response.put("results", roomPage.getContent());
        response.put("metadata", metadata);


        return response;
    }

    @Override
    public Room getRoomWithFeature(String id) {
        return this.roomRepo.findRoomWithFeatureRooms(id);
    }
}
