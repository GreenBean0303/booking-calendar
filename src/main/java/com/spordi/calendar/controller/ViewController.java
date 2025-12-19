package com.spordi.calendar.controller;

import com.spordi.calendar.model.User;
import com.spordi.calendar.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class ViewController {

    private final UserRepository userRepository;

    @GetMapping("/")
    public String landingPage() {
        return "login"; // renders login.html
    }

    @PostMapping("/select-role")
    public String selectRole(@RequestParam("role") String role, HttpSession session) {

        // Pick which DB username to use for this role
        String usernameToUse;
        if ("ADMIN".equalsIgnoreCase(role)) {
            usernameToUse = "admin";      // <-- adjust if your admin is named differently
        } else {
            usernameToUse = "testuser";   // <-- normal user account in DB
        }

        User user = userRepository.findByUsername(usernameToUse)
                .orElseThrow(() -> new IllegalStateException("No user '" + usernameToUse + "' in DB"));

        session.setAttribute("role", user.getRole().name());   // e.g. "ADMIN"/"USER"
        session.setAttribute("username", user.getUsername());  // e.g. "admin"/"testuser"
        session.setAttribute("userId", user.getId());          // real DB id

        return "redirect:/bookings";
    }

    @GetMapping("/bookings")
    public String bookingsPage(HttpSession session, Model model) {
        model.addAttribute("role", session.getAttribute("role"));
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("userId", session.getAttribute("userId"));
        return "bookings"; // renders bookings.html
    }
}
