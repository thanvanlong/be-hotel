package com.tl.hotelproject.controller;

import com.tl.hotelproject.entity.ResponseDTO;
import com.tl.hotelproject.entity.user.Role;
import com.tl.hotelproject.entity.user.User;
import com.tl.hotelproject.service.mail.EmailSender;
import com.tl.hotelproject.service.user.UserService;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/v1/users")
@CrossOrigin("*")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private EmailSender mailService;

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO<User>> register(@Valid @RequestBody User payload) {
        try {
            userService.save(payload);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(new ResponseDTO<>(null, "200", "Success", true));
    }

    @PostConstruct
    public void test() throws Exception{

        long count = this.userService.count();
        if(count > 0) return;

        User user = new User();
        user.setEmail("admin@admin.com");
        user.setName("admin");
        user.setActive(true);
        user.setRoles(Role.ROLE_ADMIN);
        user.setPhoneNumber("0987654321");
        user.setAddress("Quang Trung, Ha Dong, Ha Noi");
        user.setPassword("123123");
        userService.save(user);

        User user1 = new User();
        user1.setEmail("receptionist@gmail.com");
        user1.setName("receptionist");
        user1.setActive(true);
        user1.setRoles(Role.ROLE_RECEPTIONIST);
        user1.setPhoneNumber("0123456789");
        user1.setAddress("Quang Trung, Hoang Mai, Ha Noi");
        user1.setPassword("123123");
        userService.save(user1);

        User user2 = new User();
        user2.setEmail("user@gmail.com");
        user2.setName("user");
        user2.setActive(true);
        user2.setRoles(Role.ROLE_USER);
        user2.setPhoneNumber("0123456789");
        user2.setAddress("Quang Trung, Hai Ba Trung, Ha Noi");
        user2.setPassword("123123");

        userService.save(user2);
    }
}
