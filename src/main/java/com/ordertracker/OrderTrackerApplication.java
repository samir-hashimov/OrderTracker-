package com.ordertracker;

import com.ordertracker.auth.dao.entity.User;
import com.ordertracker.auth.dao.repository.UserRepository;
import com.ordertracker.util.Role;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class OrderTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderTrackerApplication.class, args);
    }

    @Bean
    CommandLineRunner createAdmin(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {

            String adminEmail = "qarayevibrahimm@gmail.com";

            User admin = userRepository.findByEmail(adminEmail)
                    .orElseGet(() -> User.builder()
                            .firstname("Admin")
                            .lastname("User")
                            .email(adminEmail)
                            .password(passwordEncoder.encode("1234567890"))
                            .role(Role.ADMIN)
                            .build()
                    );

            // Əgər user əvvəldən mövcuddursa, ADMIN et
            admin.setRole(Role.ADMIN);

            // İstəsən password-u da yenilə
            admin.setPassword(passwordEncoder.encode("1234567890"));

            userRepository.save(admin);

            System.out.println("ADMIN user hazırdır: " + adminEmail);
        };
    }
}
