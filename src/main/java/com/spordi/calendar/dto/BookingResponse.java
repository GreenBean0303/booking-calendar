package com.spordi.calendar.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponse {

    private Long id;
    private Long userId;
    private String username;
    private String roomName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime cancelledAt;
}
