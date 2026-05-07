package com.re.model.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingProcessDTO {
    private Long departmentId;
    private Long lecturerId;
    private LocalDateTime bookingDate;
    private String timeSlot;
    private String reason;
}
