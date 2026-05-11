package com.re.service.impl;


import com.re.model.dto.UserDTO;
import com.re.model.dto.UserRequestDTO;
import com.re.model.entity.Department;
import com.re.model.entity.Lecturer;
import com.re.model.entity.User;
import com.re.model.enums.Role;
import com.re.repository.UserRepository;
import com.re.service.DepartmentService;
import com.re.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {


    private final DepartmentService departmentService;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserServiceImpl(DepartmentService departmentService,
                       UserRepository userRepository,
                       BCryptPasswordEncoder passwordEncoder) {
        this.departmentService = departmentService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public User register(UserDTO request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã tồn tại trong hệ thống!");
        }

        User newUser = new User();
        newUser.setFullName(request.getUsername());
        newUser.setEmail(request.getEmail());


        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setRole(Role.STUDENT);
        newUser.setStatus(true);

        return userRepository.save(newUser);
    }

    @Override
    public User login(String email, String rawPassword) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(rawPassword, user.getPassword())) {
                return user;
            }
        }
        return null;
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Transactional
    public void adminCreateUser(UserRequestDTO dto) {
        User user = User.builder()
                .fullName(dto.getFullName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode("123456"))
                .role(dto.getRole())
                .status(true)
                .build();

        if (dto.getRole() == Role.LECTURER) {
            Department dept = null;
            if (dto.getDepartmentId() != null) {
                dept = departmentService.findById(dto.getDepartmentId()).orElse(null);
            }
            Lecturer lecturer = Lecturer.builder()
                    .user(user)
                    .department(dept)
                    .degree(dto.getDegree())
                    .specialization(dto.getSpecialization())
                    .build();

            user.setLecturer(lecturer);
        }

        userRepository.save(user);
    }

    @Override
    public User lock(Long id) {
        User user = findById(id);

        if (user != null) {

            user.setStatus(!user.getStatus());
            return userRepository.save(user);
        }

        return null;
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
}
