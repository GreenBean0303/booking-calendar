package com.spordi.calendar.config;

import com.spordi.calendar.model.User;
import com.spordi.calendar.model.User.Role;
import com.spordi.calendar.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            User admin = User.builder()
                    .username("admin")
                    .fullName("Admin User")
                    .role(Role.ADMIN)
                    .build();

            User user = User.builder()
                    .username("user1")
                    .fullName("Regular User")
                    .role(Role.USER)
                    .build();

            userRepository.saveAll(List.of(admin, user));
        }
    }
}
