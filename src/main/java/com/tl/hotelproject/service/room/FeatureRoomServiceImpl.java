package com.tl.hotelproject.service.room;

import com.tl.hotelproject.entity.Metadata;
import com.tl.hotelproject.entity.room.FeatureRoom;
import com.tl.hotelproject.entity.room.Room;
import com.tl.hotelproject.repo.FeatureRoomRepo;
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
public class FeatureRoomServiceImpl implements FeatureRoomService{

    @Autowired
    private FeatureRoomRepo featureRoomRepo;

    @Override
    public Map<String, Object> pagingSort(int page, int limit) {
        Pageable pagingSort = PageRequest.of(page, limit);
        Page<FeatureRoom> featureRoomPage = featureRoomRepo.findAll(pagingSort);

        Metadata metadata = new Metadata();
        metadata.setPageNumber(featureRoomPage.getNumber());
        metadata.setPageSize(featureRoomPage.getSize());
        metadata.setTotalPages(featureRoomPage.getTotalPages());
        metadata.setTotalItems(featureRoomPage.getTotalElements());

        Map<String, Object> response = new HashMap<>();
        response.put("results", featureRoomPage.getContent());
        response.put("metadata", metadata);


        return response;
    }
}
