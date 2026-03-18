package com.emphathai.authservice.controller;

import com.emphathai.authservice.dto.ResetPasswordRequest;
import com.emphathai.authservice.entity.*;
import com.emphathai.authservice.repository.UserRepository;
import com.emphathai.authservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/students")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN', 'ROLE_SCHOOL_ADMIN')")
    public ResponseEntity<List<User>> getAllStudents() {
        return ResponseEntity.ok(
                userRepository.findAll().stream()
                        .filter(u -> u.getRole() == Role.STUDENT)
                        .toList()
        );
    }

    @PostMapping("/students")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN', 'ROLE_SCHOOL_ADMIN')")
    public ResponseEntity<User> addStudent(@RequestBody User student) {
        student.setRole(Role.STUDENT);
        student.setPassword(passwordEncoder.encode(student.getPassword()));
        return ResponseEntity.ok(userRepository.save(student));
    }

    @GetMapping("/students/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN', 'ROLE_SCHOOL_ADMIN')")
    public ResponseEntity<User> getStudentById(@PathVariable Long id) {
        return ResponseEntity.ok(
                userRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Student not found"))
        );
    }

    @PutMapping("/students/{id}/reset-password")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN', 'ROLE_SCHOOL_ADMIN')")
    public ResponseEntity<String> resetPassword(@PathVariable Long id,
                                                @RequestBody ResetPasswordRequest request) {
        authService.resetStudentPassword(id, request.getNewPassword());
        return ResponseEntity.ok("Password reset successfully");
    }

    @DeleteMapping("/students/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN', 'ROLE_SCHOOL_ADMIN')")
    public ResponseEntity<String> deleteStudent(@PathVariable Long id) {
        userRepository.deleteById(id);
        return ResponseEntity.ok("Student deleted");
    }
}