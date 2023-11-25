package com.tl.hotelproject.repo;

import com.tl.hotelproject.entity.booking.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface BookingRepo extends JpaRepository<Booking, String> {
    @Query("SELECT b FROM Booking b LEFT JOIN FETCH b.room WHERE b.id = :bookingId")
    Booking getBookingWithRelationship(@Param("bookingId") String bookingId);

    @Query("SELECT b  FROM Booking b" +
//            " LEFT JOIN FETCH b.user LEFT JOIN FETCH b.usedServices " +
            " LEFT JOIN FETCH b.client c WHERE c.name LIKE %:search% or c.email like %:search% or c.tel like %:search%")
    Page<Booking> search(@Param("search") String search, Pageable pageable);

    @Query("SELECT b FROM Booking b LEFT JOIN FETCH b.client LEFT JOIN FETCH b.bills b1 WHERE b1.id = :billId")
    Booking getBookingByBill(@Param("billId") String billId);

    @Query("SELECT b.room.id, b.room.name, SUM(b.totalAmount), COUNT(b) " +
            "FROM Booking b " +
            "where (b.bookingState = 2) AND YEAR(b.createdAt) = :year "+
            "and MONTH(b.createdAt) = :month and DAY(b.createdAt) = :day " +
            "and b.id = :id "+
            "GROUP BY b.room.id, b.room.name")
    Object[] calculate7day(int year, int month, int day, String id);

    @Query("SELECT MONTH(b.createdAt), SUM(b.totalAmount) FROM Booking b " +
            "WHERE (b.bookingState = 2 OR b.bookingState = 3) AND YEAR(b.createdAt) = :year " +
            "GROUP BY MONTH(b.createdAt)")
    List<Object[]> calculateRevenueByMonth(int year);

    @Query("SELECT b.room.id, b.room.name, SUM(b.quantity * (b.price - b.price * b.selloff / 100)), SUM(b.quantity), (b.price - b.price * b.selloff / 100)  " +
            "FROM Booking b " +
            "where (b.bookingState = 2 OR b.bookingState = 3) AND YEAR(b.createdAt) = :year "+
            "GROUP BY b.room.id, b.room.name, b.quantity, b.price, b.selloff")
    List<Object[]> calculateRoomRevenueAndBookings(int year);

    @Query("SELECT b.room.id, b.room.name, SUM(b.quantity * (b.price - b.price * b.selloff / 100)), SUM(b.quantity), (b.price - b.price * b.selloff / 100) " +
            "FROM Booking b " +
            "where (b.bookingState = 2 OR b.bookingState = 3) AND YEAR(b.createdAt) = :year "+
            "and MONTH(b.createdAt) = :month "+
            "GROUP BY b.room.id, b.room.name, b.quantity, b.price, b.selloff")
    List<Object[]> calculateRoomRevenueAndBookings(int year, int month);

    @Query("SELECT b.room.id, b.room.name, SUM(b.quantity * (b.price - b.price * b.selloff / 100)), SUM(b.quantity), (b.price - b.price * b.selloff / 100) " +
            "FROM Booking b " +
            "where (b.bookingState = 2 OR b.bookingState = 3) AND YEAR(b.createdAt) = :year "+
            "and MONTH(b.createdAt) = :month and DAY(b.createdAt) = :day " +
            "GROUP BY b.room.id, b.room.name, b.quantity, b.price, b.selloff")
    List<Object[]> calculateRoomRevenueAndBookings(int year, int month, int day);

    @Query("SELECT b FROM Booking b WHERE b.createdAt >= :sevenDaysAgo AND b.createdAt <= :currentDate")
    List<Object> findDataWithin7Days(@Param("sevenDaysAgo") Date sevenDaysAgo, @Param("currentDate") Date currentDate);

    @Query("SELECT b.room.id, b.room.name, SUM(b.totalAmount), COUNT(b)  FROM Booking b WHERE b.createdAt = :date " +
            "GROUP BY b.room.id, b.room.name")
    List<Object[]> findDataForDate(@Param("date") Date date);

    @Query("SELECT u.services.id, u.services.name, SUM(u.price * u.quantity), SUM(u.quantity), u.price " +
            "FROM Booking b " +
            "INNER JOIN b.usedServices u " +
            "where (b.bookingState = 2 OR b.bookingState = 3) AND YEAR(b.createdAt) = :year "+
            "GROUP BY u.services.id, u.services.name, u.price, u.quantity")
    List<Object[]> calculateRevenueByService(int year);

    @Query("SELECT u.services.id, u.services.name, SUM(u.price * u.quantity), SUM(u.quantity), u.price " +
            "FROM Booking b " +
            "INNER JOIN b.usedServices u " +
            "where (b.bookingState = 2 OR b.bookingState = 3) AND YEAR(b.createdAt) = :year "+
            "and MONTH(b.createdAt) = :month " +
            "GROUP BY u.services.id, u.services.name, u.price, u.quantity")
    List<Object[]> calculateRevenueByService(int year, int month);

    @Query("SELECT u.services.id, u.services.name, SUM(u.price * u.quantity), SUM(u.quantity), u.price " +
            "FROM Booking b " +
            "INNER JOIN b.usedServices u " +
            "where (b.bookingState = 2 OR b.bookingState = 3) AND YEAR(b.createdAt) = :year "+
            "and MONTH(b.createdAt) = :month and DAY(b.createdAt) = :day " +
            "GROUP BY u.services.id, u.services.name, u.price, u.quantity")
    List<Object[]> calculateRevenueByService(int year, int month, int day);
}
