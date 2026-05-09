package com.re.controller;

import com.re.model.entity.Lecturer;
import com.re.model.entity.User;
import com.re.model.entity.UserProfile;
import com.re.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/")
public class ProfileController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        User userSession = (User) session.getAttribute("user");
        if (userSession == null) {
            return "redirect:/auth/login";
        }

        User userDb = userService.findByEmail(userSession.getEmail());

        model.addAttribute("user", userDb);
        model.addAttribute("profile",
                userDb.getProfile() != null ? userDb.getProfile() : new UserProfile());

        if (userDb.getLecturer() != null) {
            model.addAttribute("lecturer", userDb.getLecturer());
        }

        return "conmon/profile";
    }



    @PostMapping("/profile/update")
    public String updateProfile(HttpSession session,
                                @Valid @ModelAttribute("profile") UserProfile profileData,
                                BindingResult result,
                                @RequestParam String fullName,
                                @ModelAttribute Lecturer lecturerData,
                                Model model) {

        User userSession = (User) session.getAttribute("user");
        if (userSession == null) {
            return "redirect:/auth/login";
        }

        User userDb = userService.findByEmail(userSession.getEmail());

        if (result.hasErrors()) {
            model.addAttribute("user", userDb);
            if (userDb.getLecturer() != null) {
                model.addAttribute("lecturer", userDb.getLecturer());
            }
            return "conmon/profile";
        }


        userDb.setFullName(fullName);


        UserProfile currentProfile = userDb.getProfile();
        if (currentProfile == null) {
            currentProfile = new UserProfile();
            currentProfile.setUser(userDb);
            userDb.setProfile(currentProfile);
        }

        currentProfile.setFullName(fullName);
        currentProfile.setPhone(profileData.getPhone());
        currentProfile.setAddress(profileData.getAddress());
        currentProfile.setDateOfBirth(profileData.getDateOfBirth());
        currentProfile.setGender(profileData.getGender());


        if (userDb.getRole().name().equals("LECTURER")) {
            Lecturer currentLecturer = userDb.getLecturer();
            if (currentLecturer == null) {
                currentLecturer = new Lecturer();
                currentLecturer.setUser(userDb);
                userDb.setLecturer(currentLecturer);
            }

            currentLecturer.setDegree(lecturerData.getDegree());
            currentLecturer.setSpecialization(lecturerData.getSpecialization());
            currentLecturer.setExperienceYears(lecturerData.getExperienceYears());
        }

        userService.save(userDb);

        session.setAttribute("user", userDb);

        return "redirect:/profile?success";
    }
}
