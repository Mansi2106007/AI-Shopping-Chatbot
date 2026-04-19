package com.shoppingbot.service;

import com.shoppingbot.model.User;
import com.shoppingbot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * Register a new user
     */
    public Map<String, Object> registerUser(String username, String email, String password) {
        Map<String, Object> response = new HashMap<>();

        // Validate username uniqueness
        if (userRepository.existsByUsername(username)) {
            response.put("success", false);
            response.put("message", "Username '" + username + "' is already taken. Please choose another.");
            return response;
        }

        // Validate email uniqueness
        if (userRepository.existsByEmail(email)) {
            response.put("success", false);
            response.put("message", "Email '" + email + "' is already registered. Please login or use another email.");
            return response;
        }

        // Hash the password
        String hashedPassword = passwordEncoder.encode(password);

        // Create and save user
        User user = new User(username, email, hashedPassword);
        userRepository.save(user);

        response.put("success", true);
        response.put("message", "Registration successful! Welcome, " + username + "! Please login to continue.");
        response.put("username", username);
        return response;
    }

    /**
     * Authenticate a user login
     */
    public Map<String, Object> loginUser(String username, String password) {
        Map<String, Object> response = new HashMap<>();

        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isEmpty()) {
            response.put("success", false);
            response.put("message", "User not found. Please register first.");
            return response;
        }

        User user = userOpt.get();

        // Verify password against hashed password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            response.put("success", false);
            response.put("message", "Incorrect password. Please try again.");
            return response;
        }

        response.put("success", true);
        response.put("message", "Login successful! Welcome back, " + username + "!");
        response.put("username", username);
        response.put("email", user.getEmail());
        return response;
    }
}
