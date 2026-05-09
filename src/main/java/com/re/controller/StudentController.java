package com.re.controller;

import com.re.model.dto.BookingProcessDTO;
import com.re.model.entity.*;
import com.re.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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
    private EquimentService equimentService;

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

        model.addAttribute("studentName", user.getFullName());
        model.addAttribute("user", user);
        return "student/dashboard";
    }




    @GetMapping("/booking")
    public String booking(Model model, HttpSession session) {

        BookingProcessDTO dto = new BookingProcessDTO();
        model.addAttribute("bookingData", dto);

        model.addAttribute("departments", departmentService.findAll());
        model.addAttribute("step", 1);
        return "student/booking-session";
    }


    @PostMapping("/booking/process")
    public String processBooking(@ModelAttribute("bookingData") BookingProcessDTO dto,
                                 BindingResult result,
                                 @RequestParam("action") String action,
                                 Model model, HttpSession session) {
        session.setAttribute("bookingData", dto);

        if (result.hasErrors()) {
            return "student/booking-session";
        }


        if ("select-dept".equals(action)) {
            model.addAttribute("lecturers", lecturerService.findLecturerByDepartmentId(dto.getDepartmentId()));
            model.addAttribute("step", 2);
        }
        else if ("select-lecturer".equals(action)) {
            model.addAttribute("equipments", equimentService.findAll());
            model.addAttribute("step", 3);
        }
        else if ("select-time".equals(action)) {

            Lecturer lecturer = lecturerService.findById(dto.getLecturerId()).orElse(null);

            model.addAttribute("selectedLecturer", lecturer);
            model.addAttribute("step", 4);
        }
        else if ("confirm".equals(action)) {
            BookingProcessDTO fullDto = (BookingProcessDTO) session.getAttribute("bookingData");
            User user = (User) session.getAttribute("user");

            try {
                if (fullDto != null && fullDto.getBookingDate() != null) {
                    mentoringSessionService.createBookingRequest(fullDto, user);
                    session.removeAttribute("bookingData");
                    return "redirect:/student/dashboard?bookingSuccess";
                } else {
                    model.addAttribute("error", "Dữ liệu thời gian không hợp lệ.");
                    model.addAttribute("step", 3);
                    return "student/booking-session";
                }
            } catch (RuntimeException e) {

                model.addAttribute("error", e.getMessage());
                model.addAttribute("departments", departmentService.findAll());
                model.addAttribute("step", 3);
                return "student/booking-session";
            }
        }

        model.addAttribute("departments", departmentService.findAll());
        return "student/booking-session";
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
}