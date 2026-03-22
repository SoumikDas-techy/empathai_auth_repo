package com.emphathai.authservice.config;

import com.emphathai.authservice.entity.*;
import com.emphathai.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        // Super Admin
        if (!userRepository.existsByUsername("admin@emphathai.com")) {
            userRepository.save(User.builder()
                    .username("admin@emphathai.com")
                    .password(passwordEncoder.encode("admin1234"))
                    .role(Role.SUPER_ADMIN)
                    .build());
            System.out.println("✅ Super Admin created");
        }

        // School Admin for TIGPS
        if (!userRepository.existsByUsername("tigps@emphathai.com")) {
            userRepository.save(User.builder()
                    .username("tigps@emphathai.com")
                    .password(passwordEncoder.encode("tigps1234"))
                    .role(Role.SCHOOL_ADMIN)
                    .school("TIGPS")
                    .build());
            System.out.println("✅ TIGPS School Admin created");
        }

        // Students
        if (!userRepository.existsByUsername("Aarav Sharma")) {
            userRepository.save(User.builder()
                    .username("Aarav Sharma")
                    .password(passwordEncoder.encode("GGgvwM5Gazn4"))
                    .role(Role.STUDENT)
                    .school("TIGPS")
                    .className("4th Standard")
                    .build());
        }

        if (!userRepository.existsByUsername("Rohan Gupta")) {
            userRepository.save(User.builder()
                    .username("Rohan Gupta")
                    .password(passwordEncoder.encode("TempPass123"))
                    .role(Role.STUDENT)
                    .school("TIGPS")
                    .className("6th Standard")
                    .build());
        }

        if (!userRepository.existsByUsername("Ishaan Kumar")) {
            userRepository.save(User.builder()
                    .username("Ishaan Kumar")
                    .password(passwordEncoder.encode("TempPass789"))
                    .role(Role.STUDENT)
                    .school("TIGPS")
                    .className("5th Standard")
                    .build());
        }

        if (!userRepository.existsByUsername("falakata@emphathai.com")) {
            userRepository.save(User.builder()
                    .username("falakata@emphathai.com")
                    .password(passwordEncoder.encode("falakata1234"))
                    .role(Role.SCHOOL_ADMIN)
                    .school("FALAKATA")
                    .build());
            System.out.println("✅ Falakata School Admin created");
        }

        System.out.println("✅ Sample students created");
    }
}