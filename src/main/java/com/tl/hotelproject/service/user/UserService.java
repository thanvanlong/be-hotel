package com.tl.hotelproject.service.user;

import com.tl.hotelproject.entity.user.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public interface UserService extends UserDetailsService {
    void save(User user) throws Exception;
    void update(User user);
    void delete(User user);
    void find(String id);
}
