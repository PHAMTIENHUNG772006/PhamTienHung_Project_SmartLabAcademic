package com.re.controller;

import com.re.model.dto.BookingProcessDTO;
import com.re.model.entity.Department;
import com.re.model.entity.Lecturer;
import com.re.model.entity.User;
import com.re.model.entity.UserProfile;
import com.re.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/student")
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





    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/auth/login";

        model.addAttribute("studentName", user.getFullName());
        model.addAttribute("user", user);
        return "student/dashboard";
    }

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        User userSession = (User) session.getAttribute("user");
        if (userSession == null) return "redirect:/auth/login";

        User userDb = userService.findByEmail(userSession.getEmail());

        model.addAttribute("user", userDb);
        // FIX LỖI: Luôn new UserProfile nếu null để tránh lỗi giao diện
        model.addAttribute("profile", userDb.getProfile() != null ? userDb.getProfile() : new UserProfile());
        model.addAttribute("studentName", userDb.getFullName());

        return "conmon/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(HttpSession session,
                                @ModelAttribute UserProfile profileData,
                                @RequestParam String fullName) {
        User userSession = (User) session.getAttribute("user");
        if (userSession == null) return "redirect:/auth/login";

        User userDb = userService.findByEmail(userSession.getEmail());
        userDb.setFullName(fullName);

        UserProfile currentProfile = userDb.getProfile();
        if (currentProfile == null) {
            currentProfile = new UserProfile();
            currentProfile.setUser(userDb);
            userDb.setProfile(currentProfile);
        }

        currentProfile.setPhone(profileData.getPhone());
        currentProfile.setAddress(profileData.getAddress());
        currentProfile.setDateOfBirth(profileData.getDateOfBirth());
        currentProfile.setGender(profileData.getGender());

        userService.save(userDb);
        session.setAttribute("user", userDb);

        return "redirect:/student/profile?success";
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
                                 @RequestParam("action") String action,
                                 Model model, HttpSession session) {
        session.setAttribute("bookingData", dto);

        if ("select-dept".equals(action)) {
            model.addAttribute("lecturers", lecturerService.findLecturerByDepartmentId(dto.getDepartmentId()));
            model.addAttribute("step", 2);
        }
        else if ("select-lecturer".equals(action)) {
            // Lấy danh sách thiết bị để SV chọn yêu cầu kèm theo
            model.addAttribute("equipments", equimentService.findAll());
            model.addAttribute("step", 3);
        }
        else if ("select-time".equals(action)) {

            Lecturer lecturer = lecturerService.findLecturerById(dto.getLecturerId());
            
            model.addAttribute("selectedLecturer", lecturer);
            model.addAttribute("step", 4);
        }
        else if ("confirm".equals(action)) {

            User user = (User) session.getAttribute("user");
            mentoringSessionService.createBookingRequest(dto, user);
            return "redirect:/student/dashboard?bookingSuccess";
        }

        model.addAttribute("departments", departmentService.findAll());
        return "student/booking-session";
    }
}