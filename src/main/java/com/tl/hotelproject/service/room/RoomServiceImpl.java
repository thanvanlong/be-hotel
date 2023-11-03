package com.tl.hotelproject.service.room;

import com.tl.hotelproject.entity.Metadata;
import com.tl.hotelproject.entity.room.FeatureRoom;
import com.tl.hotelproject.entity.room.Room;
import com.tl.hotelproject.entity.room.RoomName;
import com.tl.hotelproject.repo.FeatureRoomRepo;
import com.tl.hotelproject.repo.RoomNameRepo;
import com.tl.hotelproject.repo.RoomRepo;
import com.tl.hotelproject.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {
    @Autowired
    private RoomRepo roomRepo;

    @Autowired
    private RoomNameRepo roomNameRepo;

    @Override
    public String save(Room room) throws Exception {
        //check
        for(RoomName roomName: room.getRoomNames()) {
            Optional<RoomName> roomName1 = this.roomNameRepo.findBySearch(StringUtils.removeAccents(roomName.getName().toLowerCase()));

            if(roomName1.isPresent()) throw new Exception("Phong" + roomName.getName() + "da duoc dung");
        }

        // set lai room
        for(RoomName roomName: room.getRoomNames()){
            roomName.setRoom(room);
            roomNameRepo.save(roomName);
        }

        roomRepo.save(room);

        return "Them phong thanh cong";
    }

    @Override
    public void update(Room room) throws Exception{
        roomRepo.save(room);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String updateRoomName(Room room) throws Exception{
        Room room1 = findById(room.getId());

        if(room.getRoomNames() != null){
            for(RoomName roomName: room.getRoomNames()) {
                // check ton tai
                if(roomName.getId() != null) {
                    Optional<RoomName> roomName1 = this.roomNameRepo.findBySearch(StringUtils.removeAccents(roomName.getName().toLowerCase()));
                    if(roomName1.isPresent()){
                        if(!Objects.equals(roomName1.get().getId(), roomName.getId())) {
                            throw new Exception("ten da duoc dung");
                        }
                    }
                }
                // neu khong co id
                else {
                    Optional<RoomName> roomName1 = this.roomNameRepo.findBySearch(StringUtils.removeAccents(roomName.getName().toLowerCase()));
                    // neu phong co ton tai
                    if(roomName1.isPresent()){
                        throw new Exception("Phong da duoc dung");
                        // for check
//                        for(RoomName rName: room1.getRoomNames()) {
//                            // neu phong co ten giong voi ten cua phong co san
//                            if(roomName1.get().getName().equals(rName.getName())) throw new Exception("ten da duoc dung");
//                        }

                    }
                }
            }

            // xoa phong
            for(RoomName roomName: room1.getRoomNames()){
                boolean check = false;
                for(RoomName roomName1: room.getRoomNames()){
                    if(roomName1.getId() == null) {
                        continue;
                    }

                    if(roomName.getId().equals(roomName1.getId())){
                        check = true;
                        break;
                    }
                }
                if(roomName.isBooking()) throw new Exception("Phong dang duoc dung");
                if(!check) this.roomNameRepo.delete(roomName);
            }

            for(RoomName roomName: room.getRoomNames()) {
                if(roomName.getId() != null) {
                    // lay ra room name co cung ten;
                    Optional<RoomName> roomName1 = this.roomNameRepo.findBySearch(StringUtils.removeAccents(roomName.getName().toLowerCase()));

                    // update ten phong con
                    // neu co ten
                    if(roomName1.isPresent()){
                        // neu ten do khong phai cua id nay
                        if(!Objects.equals(roomName1.get().getId(), roomName.getId())) {
                            throw new Exception("ten da duoc dung");
                        }

                        // neu ten do khac voi ten nay thi can update lai ten
                        if(!roomName1.get().getSearch().equals(StringUtils.removeAccents(roomName.getName().toLowerCase()))){
                            roomName.setCreatedAt(roomName1.get().getCreatedAt());
                            this.roomNameRepo.save(roomName);
                            // nhay den ten tiep theo
                            continue;
                        }
                    }
                }
                // neu khong co id thi them moi
                roomName.setRoom(room);
                roomNameRepo.save(roomName);
            }
        }
        return "update thanh cong";
    }

    @Override
    public RoomName checkRoomName(String name) throws Exception {
//        Optional<RoomName> roomName1 = this.roomNameRepo.findBySearch(StringUtils.removeAccents(name.toLowerCase()));
//
//        return roomName1.get();
        return null;
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
        Pageable pagingSort = PageRequest.of(page, limit, Sort.Direction.DESC, "updatedAt");
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
        return this.roomRepo.findBySlug(slug);
    }

    @Override
    public Map<String, Object> pagingSortSearch(int page, int limit, String search) {
        Pageable pagingSort = PageRequest.of(page, limit);
        Page<Room> roomPage = roomRepo.findBySearchContaining(search.toLowerCase(), pagingSort);

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
    public String revertRoom(String id, List<String> roomName) throws Exception{
        Room room = this.findById(id);
        for(String name: roomName) {
            Optional<RoomName> roomName1 = this.roomNameRepo.findBySearch(StringUtils.removeAccents(name.toLowerCase()));
            roomName1.ifPresent(value -> value.setBooking(false));
            roomNameRepo.save(roomName1.get());
        }

        room.setQuantity(room.getQuantity() + roomName.size());
        roomRepo.save(room);

        return "ok";
    }
}
