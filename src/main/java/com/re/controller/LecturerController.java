package com.re.controller;

import com.re.model.entity.*;
import com.re.service.LabService;
import com.re.service.LecturerService;
import com.re.service.MentoringSessionService;
import com.re.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/lecturer")
public class LecturerController {

    @Autowired
    private UserService userService;

    @Autowired
    private LabService labService;

    @Autowired
    private MentoringSessionService mentoringSessionService;


    private User getUserFromDb(HttpSession session) {
        User userSession = (User) session.getAttribute("user");
        if (userSession == null) return null;
        return userService.findByEmail(userSession.getEmail());
    }

    /* ================== DASHBOARD (BỔ SUNG) ================== */
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User userDb = getUserFromDb(session);
        if (userDb == null) return "redirect:/auth/login";

        model.addAttribute("user", userDb);
        model.addAttribute("lecturerName", userDb.getFullName());

        // Trả về file dashboard.html trong thư mục templates/lecturer/
        return "lecturer/dashboard";
    }


    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        User userDb = getUserFromDb(session);
        if (userDb == null) return "redirect:/auth/login";

        model.addAttribute("user", userDb);


        model.addAttribute("profile", userDb.getProfile() != null ? userDb.getProfile() : new UserProfile());


        model.addAttribute("lecturer", userDb.getLecturer() != null ? userDb.getLecturer() : new Lecturer());

        model.addAttribute("lecturerName", userDb.getFullName());


        return "conmon/profile";
    }

    /* ================== UPDATE PROFILE (POST) ================== */
    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute Lecturer lecturerData,
                                @ModelAttribute UserProfile profileData,
                                @RequestParam String fullName,
                                HttpSession session) {

        User userDb = getUserFromDb(session);
        if (userDb == null) return "redirect:/auth/login";


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


        Lecturer currentLecturer = userDb.getLecturer();
        if (currentLecturer == null) {
            currentLecturer = new Lecturer();
            currentLecturer.setUser(userDb);
            userDb.setLecturer(currentLecturer);
        }
        currentLecturer.setSpecialization(lecturerData.getSpecialization());
        currentLecturer.setDegree(lecturerData.getDegree());
        currentLecturer.setExperienceYears(lecturerData.getExperienceYears());


        userService.save(userDb);


        session.setAttribute("user", userDb);

        return "redirect:/lecturer/profile?success";
    }


//    @GetMapping("/appointments")
//    public String appointments(HttpSession session, Model model) {
//        User userDb = getUserFromDb(session);
//        if (userDb == null) return "redirect:/auth/login";
//
//        model.addAttribute("user", userDb);
//        model.addAttribute("lecturerName", userDb.getFullName());
//
////        List<MentoringSession> pendingSessions = mentoringSessionService.getPendingSessionsForLecturer(lecturer.getId());
//
//
//        List<Lab> labs = labService.findAll();
//
//        model.addAttribute("user", userDb);
//        model.addAttribute("lecturerName", userDb.getFullName());
////        model.addAttribute("pendingSessions", pendingSessions);
//        model.addAttribute("labs", labs);
//        return "lecturer/shedule-manager";
//    }


}