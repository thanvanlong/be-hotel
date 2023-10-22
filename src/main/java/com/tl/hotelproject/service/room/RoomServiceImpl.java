package com.tl.hotelproject.service.room;

import com.tl.hotelproject.entity.Metadata;
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
import java.util.Optional;

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
    public Room findById(String id) throws Exception{
        Optional<Room> room = roomRepo.findById(id);
        if (room.isPresent()) {
            return room.get();
        } else {
            throw new Exception("Không tìm thấy phòng với ID đã cho");
        }
    }

    @Override
    public  Map<String, Object> pagingSort(int page, int limit) {
        Pageable pagingSort = PageRequest.of(page, limit);
        Page<Room> roomPage = roomRepo.findAll(pagingSort);

        Metadata metadata = new Metadata();
        metadata.setPageNumber(roomPage.getNumber());
        metadata.setPageSize(roomPage.getSize());
        metadata.setTotalPages(roomPage.getTotalPages());
        metadata.setTotalItems(roomPage.getTotalElements());

        Map<String, Object> response = new HashMap<>();
        response.put("results", roomPage.getContent());
        response.put("metadata", metadata);


        return response;
    }

    @Override
    public Room getRoomWithFeature(String id) {
        return this.roomRepo.findRoomWithFeatureRooms(id);
    }

    @Override
    public Room getRoomBySlugWithFeature(String slug) {
        return this.roomRepo.findRoomBySlugWithFeatureRooms(slug);
    }

    @Override
    public Map<String, Object> pagingSortSearch(int page, int limit, String search) {
        Pageable pagingSort = PageRequest.of(page, limit);
        Page<Room> roomPage = roomRepo.findBySlugContaining(search, pagingSort);

        Metadata metadata = new Metadata();
        metadata.setPageNumber(roomPage.getNumber());
        metadata.setPageSize(roomPage.getSize());
        metadata.setTotalPages(roomPage.getTotalPages());
        metadata.setTotalItems(roomPage.getTotalElements());

        Map<String, Object> response = new HashMap<>();
        response.put("results", roomPage.getContent());
        response.put("metadata", metadata);


        return response;
    }
}
