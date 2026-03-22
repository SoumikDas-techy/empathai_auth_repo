package com.emphathai.authservice.controller;

import com.emphathai.authservice.entity.*;
import com.emphathai.authservice.repository.UserRepository;
import com.emphathai.authservice.dto.ResetPasswordRequest;
import lombok.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // ===== STUDENTS =====
    @GetMapping("/students")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN', 'ROLE_SCHOOL_ADMIN')")
    public ResponseEntity<List<StudentResponse>> getAllStudents() {
        List<StudentResponse> students = userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.STUDENT)
                .map(u -> new StudentResponse(
                        u.getId(), u.getUsername(), u.getSchool(),
                        u.getClassName(), u.getParentEmail(),
                        u.getCreatedBy(), u.getCreatedAt(),
                        u.getUpdatedBy(), u.getUpdatedAt()
                ))
                .toList();
        return ResponseEntity.ok(students);
    }

    @PostMapping("/students")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN', 'ROLE_SCHOOL_ADMIN')")
    public ResponseEntity<StudentResponse> addStudent(@RequestBody User student) {
        student.setRole(Role.STUDENT);
        student.setPassword(passwordEncoder.encode(student.getPassword()));
        User saved = userRepository.save(student);
        return ResponseEntity.ok(new StudentResponse(
                saved.getId(), saved.getUsername(), saved.getSchool(),
                saved.getClassName(), saved.getParentEmail(),
                saved.getCreatedBy(), saved.getCreatedAt(),
                saved.getUpdatedBy(), saved.getUpdatedAt()
        ));
    }

    @PutMapping("/students/{id}/reset-password")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN', 'ROLE_SCHOOL_ADMIN')")
    public ResponseEntity<String> resetStudentPassword(@PathVariable Long id,
                                                       @RequestBody ResetPasswordRequest request) {
        User student = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        student.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(student);
        return ResponseEntity.ok("Password reset successfully");
    }

    @DeleteMapping("/students/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN', 'ROLE_SCHOOL_ADMIN')")
    public ResponseEntity<String> deleteStudent(@PathVariable Long id) {
        userRepository.deleteById(id);
        return ResponseEntity.ok("Student deleted");
    }

    // ===== SCHOOL ADMINS =====
    @GetMapping("/school-admins")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<List<SchoolAdminResponse>> getSchoolAdmins() {
        List<SchoolAdminResponse> admins = userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.SCHOOL_ADMIN)
                .map(u -> new SchoolAdminResponse(
                        u.getId(), u.getUsername(), u.getSchool(),
                        u.getCreatedBy(), u.getCreatedAt()
                ))
                .toList();
        return ResponseEntity.ok(admins);
    }

    @PostMapping("/school-admins")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<String> addSchoolAdmin(@RequestBody SchoolAdminRequest request) {
        if (userRepository.existsByUsername(request.getEmail()))
            throw new RuntimeException("Email already exists!");
        userRepository.save(User.builder()
                .username(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.SCHOOL_ADMIN)
                .school(request.getSchool())
                .build());
        return ResponseEntity.ok("{\"message\": \"School Admin created successfully!\"}");
    }

    @PutMapping("/school-admins/{id}/reset-password")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<String> resetSchoolAdminPassword(@PathVariable Long id,
                                                           @RequestBody ResetPasswordRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        return ResponseEntity.ok("Password reset!");
    }

    @DeleteMapping("/school-admins/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<String> deleteSchoolAdmin(@PathVariable Long id) {
        userRepository.deleteById(id);
        return ResponseEntity.ok("School Admin deleted!");
    }

    // ===== PSYCHOLOGISTS =====
    @GetMapping("/psychologists")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<List<StaffResponse>> getPsychologists() {
        return ResponseEntity.ok(
                userRepository.findAll().stream()
                        .filter(u -> u.getRole() == Role.PSYCHOLOGIST)
                        .map(u -> new StaffResponse(
                                u.getId(), u.getUsername(), u.getFullName(),
                                u.getPhoneNumber(), u.getSchool(),
                                u.getCreatedBy(), u.getCreatedAt()))
                        .toList()
        );
    }

    @PostMapping("/psychologists")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<String> addPsychologist(@RequestBody StaffRequest request) {
        if (userRepository.existsByUsername(request.getEmail()))
            throw new RuntimeException("Email already exists!");
        userRepository.save(User.builder()
                .username(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.PSYCHOLOGIST)
                .fullName(request.getFullName())
                .phoneNumber(request.getPhoneNumber())
                .build());
        return ResponseEntity.ok("{\"message\": \"Psychologist created!\"}");
    }

    @PutMapping("/psychologists/{id}/reset-password")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<String> resetPsychologistPassword(@PathVariable Long id,
                                                            @RequestBody ResetPasswordRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        return ResponseEntity.ok("Password reset!");
    }

    @DeleteMapping("/psychologists/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<String> deletePsychologist(@PathVariable Long id) {
        userRepository.deleteById(id);
        return ResponseEntity.ok("Deleted!");
    }

    // ===== CONTENT ADMINS =====
    @GetMapping("/content-admins")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<List<StaffResponse>> getContentAdmins() {
        return ResponseEntity.ok(
                userRepository.findAll().stream()
                        .filter(u -> u.getRole() == Role.CONTENT_ADMIN)
                        .map(u -> new StaffResponse(
                                u.getId(), u.getUsername(), u.getFullName(),
                                u.getPhoneNumber(), u.getSchool(),
                                u.getCreatedBy(), u.getCreatedAt()))
                        .toList()
        );
    }

    @PostMapping("/content-admins")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<String> addContentAdmin(@RequestBody StaffRequest request) {
        if (userRepository.existsByUsername(request.getEmail()))
            throw new RuntimeException("Email already exists!");
        userRepository.save(User.builder()
                .username(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.CONTENT_ADMIN)
                .fullName(request.getFullName())
                .phoneNumber(request.getPhoneNumber())
                .build());
        return ResponseEntity.ok("{\"message\": \"Content Admin created!\"}");
    }

    @PutMapping("/content-admins/{id}/reset-password")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<String> resetContentAdminPassword(@PathVariable Long id,
                                                            @RequestBody ResetPasswordRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        return ResponseEntity.ok("Password reset!");
    }

    @DeleteMapping("/content-admins/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<String> deleteContentAdmin(@PathVariable Long id) {
        userRepository.deleteById(id);
        return ResponseEntity.ok("Deleted!");
    }

    // ===== DTOs =====
    @Data
    @AllArgsConstructor
    public static class StudentResponse {
        private Long id;
        private String username;
        private String school;
        private String className;
        private String parentEmail;
        private String createdBy;
        private LocalDateTime createdAt;
        private String updatedBy;
        private LocalDateTime updatedAt;
    }

    @Data
    @AllArgsConstructor
    public static class SchoolAdminResponse {
        private Long id;
        private String username;
        private String school;
        private String createdBy;
        private LocalDateTime createdAt;
    }

    @Data
    public static class SchoolAdminRequest {
        private String email;
        private String password;
        private String school;
    }

    @Data
    @AllArgsConstructor
    public static class StaffResponse {
        private Long id;
        private String username;
        private String fullName;
        private String phoneNumber;
        private String school;
        private String createdBy;
        private LocalDateTime createdAt;
    }

    @Data
    public static class StaffRequest {
        private String email;
        private String password;
        private String fullName;
        private String phoneNumber;
    }
}