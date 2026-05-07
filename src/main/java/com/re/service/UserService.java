package com.re.service;

import com.re.model.dto.UserDTO;
import com.re.model.dto.UserRequestDTO;
import com.re.model.entity.User;

import java.util.List;

public interface UserService {
        User register(UserDTO user);
        User login(String email, String rawPassword);
        User findByEmail(String email);
        void save(User user);
        List<User> findAll();
        void adminCreateUser(UserRequestDTO user);
        User lock(Long id);
        User findById(Long id);
}
