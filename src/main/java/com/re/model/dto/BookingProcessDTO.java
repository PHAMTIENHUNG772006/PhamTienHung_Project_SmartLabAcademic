package com.re.model.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingProcessDTO {

    private Long departmentId;

    @NotNull(message = "Giảng viên không được để trống")
    private Long lecturerId;

    @NotNull(message = "Ngày đặt lịch không được để trống")
    @Future(message = "Ngày đặt lịch phải ở tương lai")
    private LocalDateTime bookingDate;

    @NotBlank(message = "Khung giờ không được để trống")
    private String timeSlot;

    @NotBlank(message = "Lý do tư vấn không được để trống")
    private String reason;
}