package com.re.model.dto;

import com.re.model.enums.Role;
import com.re.validation.OnSave;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequestDTO {

    // --- PHẦN CHUNG ---
    @NotBlank(message = "Họ tên không được để trống")
    private String fullName;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    private String email;

    @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
    private String password;

    @NotNull(message = "Vui lòng chọn vai trò")
    private Role role;


    @NotNull(message = "Vui lòng chọn khoa", groups = OnSave.class)
    private Long departmentId;

    private String specialization;

    private String degree;

    @Min(value = 0, message = "Số năm kinh nghiệm không được âm")
    private Integer experienceYears;
}