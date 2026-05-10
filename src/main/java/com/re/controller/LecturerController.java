package com.re.controller;

import com.re.model.entity.*;
import com.re.model.enums.LabStatus;
import com.re.model.enums.SessionStatus;
import com.re.repository.LabsRepository;
import com.re.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/lecturer")
public class LecturerController {

    @Autowired
    private UserService userService;
    @Autowired
    private EquimentService equimentService;
    @Autowired
    private LabService labService;
    @Autowired
    private LecturerService lecturerService;
    @Autowired
    private MentoringSessionService mentoringSessionService;

    // Hàm tiện ích lấy User từ Session
    private User getUserFromDb(HttpSession session) {
        User userSession = (User) session.getAttribute("user");
        if (userSession == null) return null;
        return userService.findByEmail(userSession.getEmail());
    }

    /* ================== 1. DASHBOARD & PROFILE ================== */

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User userDb = getUserFromDb(session);
        if (userDb == null) return "redirect:/auth/login";
        model.addAttribute("user", userDb);
        model.addAttribute("lecturerName", userDb.getFullName());
        return "lecturer/dashboard";
    }



    /* ================== 2. QUẢN LÝ LỊCH HẸN (APPOINTMENTS) ================== */

    @GetMapping("/appointments")
    public String appointments(HttpSession session, Model model) {
        User userDb = getUserFromDb(session);
        if (userDb == null) return "redirect:/auth/login";

        Lecturer lecturerData = lecturerService.findByUserId(userDb.getUserId()).orElse(null);
        if (lecturerData == null) return "redirect:/auth/login";

        model.addAttribute("sessions", mentoringSessionService.findByLecturerAndStatus(lecturerData.getId(), SessionStatus.PENDING));
        model.addAttribute("approvedSessions", mentoringSessionService.getActiveSessionsForLecturer(lecturerData.getId()));
        model.addAttribute("lecturerName", userDb.getFullName());

        return "lecturer/shedule-manager";
    }

    /* ================== 3. QUY TRÌNH PHÊ DUYỆT (APPROVE) ================== */

    @GetMapping("/booking/approve/{id}")
    public String showApproveForm(@PathVariable Long id, Model model) {
        MentoringSession session = mentoringSessionService.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ca tư vấn id: " + id));

        model.addAttribute("mentoringSession", session);
        model.addAttribute("labs",labService.findLabByStatus(LabStatus.AVAILABLE) );
        model.addAttribute("equipments", equimentService.findAll());
        return "lecturer/approve-session";
    }

    @PostMapping("/booking/approve/confirm")
    public String confirmApprove(@RequestParam Long sessionId,
                                 @RequestParam Long labId,
                                 @RequestParam String note,
                                 @RequestParam(value = "equipmentId", required = false) Long equipmentId
    ) {
        try {
            mentoringSessionService.approveSession(sessionId, labId, note,equipmentId);
            return "redirect:/lecturer/appointments?success=approved";
        } catch (Exception e) {
            String errorMsg = URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
            return "redirect:/lecturer/appointments?error=" + errorMsg;
        }
    }

    /* ================== 4. ĐÁNH GIÁ & HOÀN TẤT (COMPLETE) ================== */

    @GetMapping("/booking/approve-form/{id}")
    public String showEvaluationForm(@PathVariable Long id, Model model) {
        MentoringSession mentoringSession = mentoringSessionService.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ca tư vấn id: " + id));

        model.addAttribute("mentoringSession", mentoringSession);
        model.addAttribute("evaluation", new AcademicEvaluation());
        model.addAttribute("equipments", equimentService.findAll());
        return "lecturer/evaluation-form";
    }

    @PostMapping("/mentoring/complete")
    public String completeMentoring(@RequestParam(value = "sessionId", required = false) Long sessionId,
                                    @ModelAttribute("evaluation") AcademicEvaluation evaluation,
                                    @RequestParam(value = "equipmentId", required = false) Long equipmentId) {
        if (sessionId == null) return "redirect:/lecturer/appointments?error=MissingSessionId";
        try {
            mentoringSessionService.completeMentoringProcess(sessionId, evaluation, equipmentId);
            return "redirect:/lecturer/appointments?success=completed";
        } catch (Exception e) {
            String errorMsg = URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
            return "redirect:/lecturer/booking/approve-form/" + sessionId + "?error=" + errorMsg;
        }
    }

    @PostMapping("/booking/reject")
    public String rejectSession(@RequestParam Long sessionId) {
        try {
            mentoringSessionService.rejectSession(sessionId);
            return "redirect:/lecturer/appointments?success=rejected";
        } catch (Exception e) {
            return "redirect:/lecturer/appointments?error=fail";
        }
    }
}