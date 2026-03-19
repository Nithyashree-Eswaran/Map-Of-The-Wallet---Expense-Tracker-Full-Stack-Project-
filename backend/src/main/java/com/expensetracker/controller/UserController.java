
package com.expensetracker.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.expensetracker.model.User;
import com.expensetracker.repository.UserRepository;
import com.expensetracker.security.services.UserDetailsImpl;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

   @GetMapping("/profile")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<?> getUserProfile() {
    UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();

    Optional<User> user = userRepository.findById(userDetails.getId());

    if (user.isPresent()) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", user.get().getId());
        response.put("name", user.get().getName());
        response.put("email", user.get().getEmail());
        response.put("avatarUrl", user.get().getAvatarUrl());
        response.put("phone", user.get().getPhone());
        response.put("address", user.get().getAddress());

        return ResponseEntity.ok(response);
    }

    return ResponseEntity.notFound().build();
}

   @PutMapping("/profile")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<?> updateUserProfile(@RequestBody User userUpdate) {
    UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();

    Optional<User> userOptional = userRepository.findById(userDetails.getId());

    if (userOptional.isPresent()) {
        User user = userOptional.get();

        // Only update allowed fields
        if (userUpdate.getName() != null) {
            user.setName(userUpdate.getName());
        }
        if (userUpdate.getAvatarUrl() != null) {
            user.setAvatarUrl(userUpdate.getAvatarUrl());
        }
        if (userUpdate.getPhone() != null) {
            user.setPhone(userUpdate.getPhone());
        }
        if (userUpdate.getAddress() != null) {
            user.setAddress(userUpdate.getAddress());
        }
        // Password is handled separately

        userRepository.save(user);

        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("name", user.getName());
        response.put("email", user.getEmail());
        response.put("avatarUrl", user.getAvatarUrl());
        response.put("phone", user.getPhone());
        response.put("address", user.getAddress());

        return ResponseEntity.ok(response);
    }

    return ResponseEntity.notFound().build();
}
}
