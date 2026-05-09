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
                               BindingResult result,
                               RedirectAttributes redirectAttributes) {

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

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Tạo tài khoản thành công! Đang chuyển đến trang đăng nhập..."
            );

            return "redirect:/auth/register?success";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Đã có lỗi xảy ra, vui lòng thử lại."
            );

            return "redirect:/auth/register";
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

        // 1. Kiểm tra dữ liệu đầu vào
        if (email == null || email.trim().isEmpty()) {
            redirect.addFlashAttribute("emailError", "Email không được để trống");
            hasError = true;
        }

        if (password == null || password.trim().isEmpty()) {
            redirect.addFlashAttribute("passwordError", "Mật khẩu không được để trống");
            hasError = true;
        }

        // Nếu thiếu email hoặc password thì dừng luôn
        if (hasError) {
            return "redirect:/auth/login";
        }

        // 2. Tìm user theo email
        User userDb = userService.findByEmail(email);

        // Nếu email không tồn tại
        if (userDb == null) {
            redirect.addFlashAttribute("error", "Email hoặc mật khẩu không đúng");
            return "redirect:/auth/login";
        }

        // 3. Kiểm tra tài khoản bị khóa
        if (Boolean.FALSE.equals(userDb.getStatus())) {
            redirect.addFlashAttribute("lockAccount", "Tài khoản của bạn đang bị khóa");
            return "redirect:/auth/login";
        }

        // 4. Kiểm tra đăng nhập (email + password)
        User user = userService.login(email, password);

        // Sai mật khẩu
        if (user == null) {
            redirect.addFlashAttribute("error", "Email hoặc mật khẩu không đúng");
            return "redirect:/auth/login";
        }

        // 5. Đăng nhập thành công
        session.setAttribute("user", user);

        String role = user.getRole().name();

        if ("ADMIN".equals(role)) {
            return "redirect:/admin/dashboard";
        }

        if ("LECTURER".equals(role)) {
            return "redirect:/lecturer/dashboard";
        }

        return "redirect:/student/dashboard";
    }
}