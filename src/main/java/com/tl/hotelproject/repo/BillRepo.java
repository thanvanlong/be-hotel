package com.tl.hotelproject.repo;

import com.tl.hotelproject.entity.bill.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BillRepo extends JpaRepository<Bill, String> {

}
