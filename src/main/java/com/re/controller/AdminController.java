package com.re.controller;


import com.re.model.dto.EquipmentDTO;
import com.re.model.dto.UserDTO;
import com.re.model.dto.UserRequestDTO;
import com.re.model.entity.*;
import com.re.model.enums.BorrowingStatus;
import com.re.model.enums.Role;
import com.re.service.*;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private EquimentService equimentService;


    @Autowired
    private LabService labService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private UserService userServices;

    @Autowired
    private BorrowwingRecordService borrowingService;


    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("currentMenu", "dashboard");
        model.addAttribute("title", "Bảng điều khiển Admin");
        return "admin/dashboard";
    }

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        User userSession = (User) session.getAttribute("user");
        if (userSession == null) return "redirect:/auth/login";

        User userDb = userService.findByEmail(userSession.getEmail());

        model.addAttribute("user", userDb);
        model.addAttribute("profile", userDb.getProfile() != null ? userDb.getProfile() : new UserProfile());
        model.addAttribute("adminName", userDb.getFullName());

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

        return "redirect:/admin/profile?success";
    }

    @GetMapping("/equipments")
    public String equipment(Model model){

        List<Equipment> equipments = equimentService.findAll();

        model.addAttribute("equipments", equipments);

        return "admin/equipment/equipment-manager";
    }


    @GetMapping("/equipments/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            equimentService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Xóa thiết bị thành công!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/equipments";
    }

    @GetMapping("/equipments/add")
    public String add(Model model){

        model.addAttribute("equipment", new Equipment());

        return "admin/equipment/equipment-form";
    }

    @PostMapping("/equipments/save")
    public String save(@Valid @ModelAttribute("equipment") EquipmentDTO request,
                       BindingResult result) {

        if (result.hasErrors()) {
            return "admin/equipment/equipment-form";
        }

        Equipment equipment = Equipment.builder()
                .id(request.getId())
                .name(request.getName())
                .description(request.getDescription())
                .availableQuantity(request.getAvailableQuantity())
                .build();

        equimentService.save(equipment);


        return "redirect:/admin/equipments";
    }

    @GetMapping("/equipments/edit/{id}")
    public String update(@PathVariable Long id,Model model){

        Equipment equipment = equimentService.findById(id);

        EquipmentDTO equipmentRequest = new EquipmentDTO();
        equipmentRequest.setName(equipment.getName());
        equipmentRequest.setId(equipment.getId());
        equipmentRequest.setDescription(equipment.getDescription());
        equipmentRequest.setAvailableQuantity(equipment.getAvailableQuantity());
        model.addAttribute("equipment",equipmentRequest);

        return "admin/equipment/equipment-form";
    }

    @GetMapping("/user/lock/{id}")
    public String lock(@PathVariable Long id, RedirectAttributes redirectAttributes) {

        userService.lock(id);

        return "redirect:/admin/users";
    }

    @GetMapping("/labs")
    public String labs(Model model){

        List<Lab> labs = labService.findAll();

        model.addAttribute("labs", labs);

        return "admin/lab/labs-manager";
    }


    @GetMapping("/departments")
    public String departments(Model model){

       List<Department> departments = departmentService.findAll();

       model.addAttribute("departments", departments);

        return "admin/department/department-manager";
    }

    @GetMapping("/users")
    public String users(Model model){

        List<User> users = userServices.findAll();

        model.addAttribute("users", users);

        return "admin/user/user-manager";
    }


    @GetMapping("/users/create")
    public String showCreateUserForm(Model model) {
        model.addAttribute("userDTO", new UserRequestDTO());
        model.addAttribute("departments", departmentService.findAll());
        model.addAttribute("roles", Role.values());
        return "admin/user/user-create";
    }


    @PostMapping("/users/create")
    public String handleCreateUser(@ModelAttribute("userDTO") UserRequestDTO dto,
                                   @RequestParam("action") String action,
                                   Model model) {


        if ("refresh".equals(action)) {
            model.addAttribute("departments", departmentService.findAll());
            model.addAttribute("userDTO", dto);
            return "admin/user/user-create";
        }


        if ("save".equals(action)) {
            userService.adminCreateUser(dto);
            return "redirect:/admin/users?success";
        }

        return "admin/user/user-create";
    }


    // Duyệt đơn mượn thiết bị - Màn hình danh sách
    @GetMapping("/equipments/borrowing")
    public String list(Model model) {
        List<BorrowingRecord> records = borrowingService.findAll();
        model.addAttribute("borrowingRecords", records);
        model.addAttribute("pendingCount", records.stream()
                .filter(r -> r.getStatus() == BorrowingStatus.PENDING).count());
        return "admin/equipment/approved-equipment";
    }


    @PostMapping("/equipments/borrowing/confirm/{id}")
    public String confirm(@PathVariable Long id) {
        try {
            borrowingService.confirmBorrowing(id);

            return "redirect:/admin/equipments/borrowing?success=confirmed";
        } catch (Exception e) {
            return "redirect:/admin/equipments/borrowing?error=" + e.getMessage();
        }
    }

    @PostMapping("/equipments/borrowing/cancel/{id}")
    public String cancel(@PathVariable Long id) {
        borrowingService.cancelBorrowing(id);
        return "redirect:/admin/equipments/borrowing?success=cancelled";
    }
}