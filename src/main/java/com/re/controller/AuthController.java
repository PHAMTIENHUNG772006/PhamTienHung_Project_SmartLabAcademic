package com.re.controller;

import com.re.model.dto.UserDTO;
import com.re.model.entity.User;
import com.re.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("registerRequest", new UserDTO());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("registerRequest") UserDTO request,
                               BindingResult result, Model model) {

        if (result.hasErrors()) {
            return "auth/register";
        }

        User existingUser = userService.findByEmail(request.getEmail());
        if (existingUser != null) {
            result.rejectValue("email", "error.registerRequest", "Email này đã được sử dụng");
            return "auth/register";
        }

        try {
            userService.register(request);
            return "redirect:/auth/login?success";
        } catch (Exception e) {
            model.addAttribute("error", "Đã có lỗi xảy ra, vui lòng thử lại");
            return "auth/register";
        }
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam(required = false) String email,
                        @RequestParam(required = false) String password,
                        HttpSession session,
                        RedirectAttributes redirect) {

        boolean hasError = false;
        if (email == null || email.trim().isEmpty()) {
            redirect.addFlashAttribute("emailError", "Email không được để trống");
            hasError = true;
        }
        if (password == null || password.trim().isEmpty()) {
            redirect.addFlashAttribute("passwordError", "Mật khẩu không được để trống");
            hasError = true;
        }

        if (hasError) return "redirect:/auth/login";


        User user = userService.login(email, password);

        if (user != null && user.getStatus() == true) {
            session.setAttribute("user", user);
            String role = user.getRole().name();
            if (role.equals("ADMIN")) return "redirect:/admin/dashboard";
            if (role.equals("LECTURER")) return "redirect:/lecturer/dashboard";
            return "redirect:/student/dashboard";
        }


        redirect.addFlashAttribute("error", "Email hoặc mật khẩu không đúng");
        return "redirect:/auth/login";
    }

}