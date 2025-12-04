package com.spordi.calendar.controller;

import com.spordi.calendar.dto.BookingRequest;
import com.spordi.calendar.dto.BookingResponse;
import com.spordi.calendar.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @RequestBody BookingRequest request,
            @RequestHeader(value = "User-Id", defaultValue = "1") Long userId) {

        BookingResponse booking = bookingService.createBooking(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(booking);
    }

    @GetMapping
    public ResponseEntity<List<BookingResponse>> getAllActiveBookings() {
        List<BookingResponse> bookings = bookingService.getAllActiveBookings();
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getBookingById(@PathVariable Long id) {
        BookingResponse booking = bookingService.getBookingById(id);
        return ResponseEntity.ok(booking);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookingResponse>> getUserBookings(@PathVariable Long userId) {
        List<BookingResponse> bookings = bookingService.getUserBookings(userId);
        return ResponseEntity.ok(bookings);
    }

    // Get bookings for specific date
    @GetMapping("/date")
    public ResponseEntity<List<BookingResponse>> getBookingsByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {

        List<BookingResponse> bookings = bookingService.getBookingsByDate(date);
        return ResponseEntity.ok(bookings);
    }

    // Cancel booking - 24h rule applies
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelBooking(
            @PathVariable Long id,
            @RequestHeader(value = "User-Id", defaultValue = "1") Long userId) {

        bookingService.cancelBooking(id, userId);
        return ResponseEntity.noContent().build();
    }

    // Admin only - force delete
    @DeleteMapping("/{id}/admin")
    public ResponseEntity<Void> deleteBooking(
            @PathVariable Long id,
            @RequestHeader(value = "User-Id", defaultValue = "1") Long userId) {

        bookingService.deleteBooking(id, userId);
        return ResponseEntity.noContent().build();
    }
}