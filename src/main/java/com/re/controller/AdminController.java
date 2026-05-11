package com.re.controller;


import com.re.model.dto.EquipmentDTO;
import com.re.model.dto.UserRequestDTO;
import com.re.model.entity.*;
import com.re.model.enums.BorrowingStatus;
import com.re.model.enums.Role;
import com.re.repository.LecturerTopConsulting;
import com.re.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final EquimentService equimentService;
    private final LabService labService;
    private final DepartmentService departmentService;
    private final BorrowingRecordService borrowingService;
    private final MentoringSessionService mentoringSessionService;
    private final LecturerService lecturerService;

    public AdminController(UserService userService,
                           EquimentService equimentService,
                           LabService labService,
                           DepartmentService departmentService,
                           BorrowingRecordService borrowingService,
                           MentoringSessionService mentoringSessionService,
                           LecturerService lecturerService) {
        this.userService = userService;
        this.equimentService = equimentService;
        this.labService = labService;
        this.departmentService = departmentService;
        this.borrowingService = borrowingService;
        this.mentoringSessionService = mentoringSessionService;
        this.lecturerService = lecturerService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<BorrowingRecord> records = borrowingService.findAll();
        List<MentoringSession> sessions = mentoringSessionService.findAll();
        List<Equipment> equipments = equimentService.findAll();

        LecturerTopConsulting topLecturerData = lecturerService.getMostActiveLecturer();

        if (topLecturerData != null) {
            model.addAttribute("topLecturer", topLecturerData.getLecturer().getUser().getFullName());
            model.addAttribute("department",topLecturerData.getLecturer().getDepartment().getName());
            model.addAttribute("topLecturerCount", topLecturerData.getSessionCount());
        }

        long currentBorrowing = records.stream()
                .filter(r -> r.getStatus() == BorrowingStatus.BORROWED)
                .count();


        long awaitingDelivery = records.stream()
                .filter(r -> r.getStatus() == BorrowingStatus.PENDING)
                .count();

        List<Equipment> lowStockEquipments = equipments.stream()
                .filter(e -> e.getAvailableQuantity() < 10)
                .collect(Collectors.toList());

        model.addAttribute("totalEquipment", equipments.size());
        model.addAttribute("currentBorrowing", currentBorrowing);
        model.addAttribute("awaitingDelivery", awaitingDelivery);
        model.addAttribute("totalSession", sessions.size());
        model.addAttribute("lowStockEquipments", lowStockEquipments);

        return "admin/dashboard";
    }


    @GetMapping("/equipments")
    public String equipment(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int itemPerpage, Model model){

        Pageable pageable = PageRequest.of(page, itemPerpage);


        Page<Equipment> equipments = equimentService.findAllActive(pageable);


        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", equipments.getTotalPages());
        model.addAttribute("totalItems", equipments.getTotalElements());
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
                .status(true)
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

        User userDb = userService.findById(id);

        if (userDb.getUserId().equals(id)){
            redirectAttributes.addFlashAttribute("error","Bạn không thể khóa tài khoản chính mình");

            return "redirect:/admin/users";
        }


        userService.lock(id);
        redirectAttributes.addFlashAttribute("success","Khóa tài khoản thành công");

        return "redirect:/admin/users";
    }

    @GetMapping("/labs")
    public String labs(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int itemPerpage, Model model){

        Pageable pageable = PageRequest.of(page, itemPerpage);

        Page<Lab> labs = labService.findAll(pageable);

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", labs.getTotalPages());
        model.addAttribute("totalItems", labs.getTotalElements());
        model.addAttribute("labs", labs);

        return "admin/lab/labs-manager";
    }


    @GetMapping("/departments")
    public String departments(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int itemPerpage,Model model){

        Pageable pageable = PageRequest.of(page,itemPerpage);

       Page<Department> departmentPage = departmentService.findAll(pageable);

        model.addAttribute("departments", departmentPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", departmentPage.getTotalPages());
        model.addAttribute("totalItems", departmentPage.getTotalElements());

        return "admin/department/department-manager";
    }

    @GetMapping("/users")
    public String users(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int itemPerpage,Model model){

        Pageable pageable = PageRequest.of(page, itemPerpage);

        Page<User> users = userService.findAll(pageable);

        model.addAttribute("users", users.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", users.getTotalPages());
        model.addAttribute("totalItems", users.getTotalElements());


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
    public String handleCreateUser(@Valid @ModelAttribute("userDTO") UserRequestDTO dto,
                                   BindingResult result,
                                   @RequestParam("action") String action,
                                   Model model) {

        if (result.hasErrors()) {
            // In lỗi ra Console để bạn nhìn thấy tận mắt
            result.getFieldErrors().forEach(f ->
                    System.out.println("Lỗi tại trường: " + f.getField() + " | Giá trị lỗi: " + f.getRejectedValue() + " | Thông báo: " + f.getDefaultMessage())
            );

            model.addAttribute("departments", departmentService.findAll());
            model.addAttribute("roles", Role.values());
            return "admin/user/user-create";
        }

        if (userService.findByEmail(dto.getEmail()) != null) {
            result.rejectValue("email", "error.userDTO", "Email này đã tồn tại trong hệ thống!");
        }

        // 1. Phải ưu tiên xử lý "refresh" TRƯỚC khi check lỗi validation
        if ("refresh".equals(action)) {
            model.addAttribute("departments", departmentService.findAll());
            model.addAttribute("roles", Role.values());
            return "admin/user/user-create";
        }

        // 2. Sau đó mới check lỗi khi người dùng nhấn "save"
        if (result.hasErrors()) {
            model.addAttribute("departments", departmentService.findAll());
            model.addAttribute("roles", Role.values());
            return "admin/user/user-create";
        }

        System.out.println(action);

        if ("save".equals(action)) {

            userService.adminCreateUser(dto);
            return "redirect:/admin/users?success";
        }

        return "admin/user/user-create";
    }


    // Duyệt đơn mượn thiết bị - Màn hình danh sách
    @GetMapping("/equipments/borrowing")
    public String list(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int itemPerpage, Model model) {

        Sort sort = Sort.by(Sort.Order.asc("status"));

        Pageable pageable = PageRequest.of(page, itemPerpage,sort);

        Page<BorrowingRecord> records = borrowingService.findAll(pageable);

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", records.getTotalPages());
        model.addAttribute("totalItems", records.getTotalElements());
        model.addAttribute("borrowingRecords", records);
        model.addAttribute("pendingCount", borrowingService.countBorrowingRecordByStatus(BorrowingStatus.PENDING));
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