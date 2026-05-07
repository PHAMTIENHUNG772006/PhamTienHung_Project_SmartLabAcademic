package com.re.controller;


import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SecurityController {

    @GetMapping("/auth/logout")
    public String logout(HttpSession session) {

        session.invalidate();
        return "redirect:/auth/login?logout=true";
    }
}
