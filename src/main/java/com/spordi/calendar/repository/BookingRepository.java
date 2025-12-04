// src/main/java/com/spordi/calendar/repository/BookingRepository.java
package com.spordi.calendar.repository;

import com.spordi.calendar.model.Booking;
import com.spordi.calendar.model.Booking.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserId(Long userId);

    List<Booking> findByStatus(BookingStatus status);

    // ✅ NEW: all bookings with given status, ordered by startTime ascending
    List<Booking> findByStatusOrderByStartTimeAsc(BookingStatus status);

    List<Booking> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM Booking b " +
            "WHERE b.status = 'ACTIVE' " +
            "AND b.roomName = :roomName " +
            "AND b.startTime < :endTime " +
            "AND b.endTime > :startTime")
    boolean existsOverlappingBooking(
            @Param("roomName") String roomName,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    // ✅ restore the date-based query you had before
    @Query("SELECT b FROM Booking b WHERE b.status = 'ACTIVE' " +
            "AND DATE(b.startTime) = DATE(:date) " +
            "ORDER BY b.startTime ASC")
    List<Booking> findActiveBookingsByDate(@Param("date") LocalDateTime date);
}
