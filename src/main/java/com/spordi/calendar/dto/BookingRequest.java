package com.spordi.calendar.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingRequest {

    private String roomName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
