package com.re.controller;

import com.re.model.dto.BookingProcessDTO;
import com.re.model.entity.*;
import com.re.model.enums.SessionStatus;
import com.re.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/student")
@SessionAttributes("bookingData")
public class StudentController {

    @Autowired
    private UserService userService;

    @Autowired
    private DepartmentService departmentService;


    @Autowired
    private LecturerService lecturerService;


    @Autowired
    private MentoringSessionService mentoringSessionService;


    private User getUserFromDb(HttpSession session) {
        User userSession = (User) session.getAttribute("user");
        if (userSession == null) return null;
        return userService.findByEmail(userSession.getEmail());
    }


    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/auth/login";

        List<MentoringSession> sessions = mentoringSessionService
                .findByStudentId(user.getUserId())
                .stream()
                .sorted(Comparator.comparing(MentoringSession::getStartTime).reversed())
                .limit(5)
                .toList();
        Integer countScheduleBecoming = mentoringSessionService.findByStudentIdAndStatus(user.getUserId(), SessionStatus.AWAITING_EQUIPMENT).size();

        model.addAttribute("sessions", sessions);
        model.addAttribute("studentName", user.getFullName());
        model.addAttribute("countScheduleBecoming", countScheduleBecoming);
        model.addAttribute("user", user);
        return "student/dashboard";
    }




    // ==================== GET BOOKING ====================
    @GetMapping("/booking")
    public String booking(@RequestParam(defaultValue = "0") int page,
                          @RequestParam(defaultValue = "9") int itemPerPage,
                          Model model,
                          HttpSession session) {

        // Nếu chưa có dữ liệu trong session thì khởi tạo mới
        BookingProcessDTO bookingData =
                (BookingProcessDTO) session.getAttribute("bookingData");

        if (bookingData == null) {
            bookingData = new BookingProcessDTO();
            session.setAttribute("bookingData", bookingData);
        }

        // Load danh sách khoa có phân trang
        Pageable pageable = PageRequest.of(page, itemPerPage);
        Page<Department> departments = departmentService.findAll(pageable);

        model.addAttribute("bookingData", bookingData);
        model.addAttribute("departments", departments.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", departments.getTotalPages());
        model.addAttribute("totalItems", departments.getTotalElements());

        // Nếu truy cập trực tiếp /student/booking thì luôn bắt đầu từ step 1
        model.addAttribute("step", 1);

        return "student/booking-session";
    }


    // ==================== POST PROCESS BOOKING ====================
    @PostMapping("/booking/process")
    public String processBooking(@ModelAttribute("bookingData") BookingProcessDTO dto,
                                 BindingResult result,
                                 @RequestParam("action") String action,
                                 Model model,
                                 HttpSession session) {

        // ===== 1. Merge dữ liệu từ form vào session =====
        BookingProcessDTO sessionDto =
                (BookingProcessDTO) session.getAttribute("bookingData");

        if (sessionDto == null) {
            sessionDto = new BookingProcessDTO();
        }

        if (dto.getDepartmentId() != null) {
            sessionDto.setDepartmentId(dto.getDepartmentId());
        }

        if (dto.getLecturerId() != null) {
            sessionDto.setLecturerId(dto.getLecturerId());
        }

        if (dto.getBookingDate() != null) {
            sessionDto.setBookingDate(dto.getBookingDate());
        }

        if (dto.getReason() != null && !dto.getReason().isBlank()) {
            sessionDto.setReason(dto.getReason());
        }

        dto = sessionDto;

        session.setAttribute("bookingData", dto);
        model.addAttribute("bookingData", dto);

        // ===== 2. Load departments + pagination cho step 1 =====
        Pageable pageable = PageRequest.of(0, 9);
        Page<Department> departments = departmentService.findAll(pageable);

        model.addAttribute("departments", departments.getContent());
        model.addAttribute("currentPage", 0);
        model.addAttribute("totalPages", departments.getTotalPages());
        model.addAttribute("totalItems", departments.getTotalElements());

        // ===== 3. Validate step 3 =====
        if ("select-time".equals(action) && result.hasErrors()) {
            model.addAttribute("step", 3);
            return "student/booking-session";
        }

        // ===== 4. Xử lý wizard =====
        switch (action) {

            // STEP 1 -> STEP 2
            case "select-dept":
                model.addAttribute("lecturers",
                        lecturerService.findLecturerByDepartmentId(dto.getDepartmentId()));
                model.addAttribute("step", 2);
                break;

            // STEP 2 -> STEP 3
            case "select-lecturer":
                model.addAttribute("step", 3);
                break;

            // STEP 3 -> STEP 4
            case "select-time":
                Lecturer lecturer = lecturerService.findById(dto.getLecturerId())
                        .orElse(null);

                model.addAttribute("selectedLecturer", lecturer);
                model.addAttribute("step", 4);
                break;

            // STEP 4 -> Lưu booking
            case "confirm":
                User user = (User) session.getAttribute("user");

                try {
                    if (dto != null && dto.getBookingDate() != null) {
                        mentoringSessionService.createBookingRequest(dto, user);

                        // Xóa dữ liệu wizard sau khi lưu thành công
                        session.removeAttribute("bookingData");

                        return "redirect:/student/dashboard?bookingSuccess";
                    } else {
                        model.addAttribute("error", "Dữ liệu thời gian không hợp lệ.");
                        model.addAttribute("step", 3);
                        return "student/booking-session";
                    }
                } catch (RuntimeException e) {
                    model.addAttribute("error", e.getMessage());
                    model.addAttribute("step", 3);
                    return "student/booking-session";
                }

                // STEP 2 -> STEP 1
            case "back-to-step1":
                model.addAttribute("step", 1);
                break;

            // STEP 3 -> STEP 2
            case "back-to-step2":
                model.addAttribute("lecturers",
                        lecturerService.findLecturerByDepartmentId(dto.getDepartmentId()));
                model.addAttribute("step", 2);
                break;

            // STEP 4 -> STEP 3
            case "back-to-step3":
                model.addAttribute("step", 3);
                break;

            // Action không hợp lệ
            default:
                model.addAttribute("step", 1);
                break;
        }

        return "student/booking-session";
    }

    @GetMapping("/history/cancel/{id}")
    public String cancelBooking(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/auth/login";

        try {
            mentoringSessionService.cancelSession(id, user.getUserId());
            redirectAttributes.addFlashAttribute("success", "Đã hủy lịch tư vấn thành công.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/student/history";
    }

    @GetMapping("/history")
    public String viewHistory(HttpSession session, Model model) {
        User userDb = getUserFromDb(session);
        if (userDb == null) return "redirect:/auth/login";

        List<MentoringSession> history = mentoringSessionService.getStudentHistory(userDb.getUserId());
        model.addAttribute("history", history);
        model.addAttribute("currentMenu", "history");
        return "student/history";
    }


    @GetMapping("/history/detail/{id}")
    public String viewHistory(@PathVariable Long id, Model model) {
        MentoringSession session  = mentoringSessionService.findById(id).orElse(null);

        if (session == null || session.getAcademicEvaluation() == null) {
            return "redirect:/student/history";
        }

        model.addAttribute("item", session);
        return "student/history-detail";
    }
}