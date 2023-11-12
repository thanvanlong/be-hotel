package com.tl.hotelproject.entity.booking;

public enum BookingState {
    Init, // khoi tao
    AdminInit, // khoi tao bang admin
    Success, // khi thanh toan phong thanh cong
    Done, // check out
    Reject // don bi huy
}
