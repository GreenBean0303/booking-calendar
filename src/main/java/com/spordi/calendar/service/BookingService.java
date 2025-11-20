package com.spordi.calendar.service;

import com.spordi.calendar.dto.BookingRequest;
import com.spordi.calendar.dto.BookingResponse;
import com.spordi.calendar.exception.BusinessException;
import com.spordi.calendar.exception.NotFoundException;
import com.spordi.calendar.exception.UnauthorizedException;
import com.spordi.calendar.model.Booking;
import com.spordi.calendar.model.Booking.BookingStatus;
import com.spordi.calendar.model.User;
import com.spordi.calendar.repository.BookingRepository;
import com.spordi.calendar.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    // ==================== CREATE ====================

    /**
     * Loo uus broneering
     * Reeglid:
     * 1. Aeg peab olema tulevikus
     * 2. Lõpuaeg peab olema pärast algusaega
     * 3. Aeg ei tohi olla juba broneeritud
     */
    public BookingResponse createBooking(BookingRequest request, Long userId) {

        // Leia kasutaja
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        // REEGEL 1: Aeg peab olema tulevikus
        if (request.getStartTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Cannot book time in the past");
        }

        // REEGEL 2: Lõpuaeg pärast algusaega
        if (request.getEndTime().isBefore(request.getStartTime()) ||
                request.getEndTime().equals(request.getStartTime())) {
            throw new BusinessException("End time must be after start time");
        }

        // REEGEL 3: Kontrolli, kas aeg on juba broneeritud
        boolean isOverlapping = bookingRepository.existsOverlappingBooking(
                request.getRoomName(),
                request.getStartTime(),
                request.getEndTime()
        );

        if (isOverlapping) {
            throw new BusinessException("This time slot is already booked");
        }

        // Kõik OK → Loo broneering
        Booking booking = Booking.builder()
                .user(user)
                .roomName(request.getRoomName())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .status(BookingStatus.ACTIVE)
                .build();

        Booking savedBooking = bookingRepository.save(booking);

        return mapToResponse(savedBooking);
    }

    // ==================== READ ====================

    /**
     * Leia broneering ID järgi
     */
    public BookingResponse getBookingById(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        return mapToResponse(booking);
    }

    /**
     * Leia kõik kasutaja broneeringud
     */
    public List<BookingResponse> getUserBookings(Long userId) {
        List<Booking> bookings = bookingRepository.findByUserId(userId);

        return bookings.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Leia kõik aktiivsed broneeringud (kalender)
     */
    public List<BookingResponse> getAllActiveBookings() {
        List<Booking> bookings = bookingRepository.findByStatus(BookingStatus.ACTIVE);

        return bookings.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Leia broneeringud kuupäeva järgi
     */
    public List<BookingResponse> getBookingsByDate(LocalDateTime date) {
        List<Booking> bookings = bookingRepository.findActiveBookingsByDate(date);

        return bookings.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ==================== UPDATE (Cancel) ====================

    /**
     * Tühista broneering
     * ÄRIREEGEL: Saab tühistada ainult 24h enne!
     */
    public void cancelBooking(Long bookingId, Long userId) {

        // Leia broneering
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        // REEGEL 1: Kontrolli, kas see on kasutaja oma broneering
        if (!booking.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You can only cancel your own bookings");
        }

        // REEGEL 2: Kontrolli, kas on juba tühistatud
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BusinessException("Booking is already cancelled");
        }

        // REEGEL 3: 24H REEGEL - Saab tühistada ainult 24h enne!
        LocalDateTime cancelDeadline = booking.getStartTime().minusHours(24);
        LocalDateTime now = LocalDateTime.now();

        if (now.isAfter(cancelDeadline)) {
            throw new BusinessException(
                    "Cannot cancel booking within 24 hours of start time. " +
                            "Cancellation deadline was: " + cancelDeadline
            );
        }

        // Kõik OK → Tühista
        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCancelledAt(LocalDateTime.now());
        bookingRepository.save(booking);
    }

    // ==================== DELETE (Admin only) ====================

    /**
     * Kustuta broneering (ainult admin)
     */
    public void deleteBooking(Long bookingId) {
        if (!bookingRepository.existsById(bookingId)) {
            throw new NotFoundException("Booking not found");
        }

        bookingRepository.deleteById(bookingId);
    }

    // ==================== Helper Methods ====================

    /**
     * Konverteeri Booking Entity → BookingResponse DTO
     */
    private BookingResponse mapToResponse(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .userId(booking.getUser().getId())
                .username(booking.getUser().getUsername())
                .roomName(booking.getRoomName())
                .startTime(booking.getStartTime())
                .endTime(booking.getEndTime())
                .status(booking.getStatus())
                .createdAt(booking.getCreatedAt())
                .cancelledAt(booking.getCancelledAt())
                .build();
    }
}