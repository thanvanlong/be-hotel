package com.tl.hotelproject.service.room;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface FeatureRoomService {
    Map<String, Object> pagingSort(int page, int limit);
    long count();
}
