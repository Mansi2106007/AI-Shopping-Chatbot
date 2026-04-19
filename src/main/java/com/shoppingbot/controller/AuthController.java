package com.shoppingbot.controller;

import com.shoppingbot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * POST /register
     * Body: { "username": "user", "email": "user@gmail.com", "password": "1234" }
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String email    = body.get("email");
        String password = body.get("password");

        // Basic validation
        if (username == null || username.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(error("Username is required"));
        }
        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(error("Email is required"));
        }
        if (password == null || password.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(error("Password is required"));
        }
        if (password.length() < 4) {
            return ResponseEntity.badRequest().body(error("Password must be at least 4 characters"));
        }

        Map<String, Object> result = userService.registerUser(username.trim(), email.trim(), password);

        if ((boolean) result.get("success")) {
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(result);
        }
    }

    /**
     * POST /login
     * Body: { "username": "user", "password": "1234" }
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        if (username == null || username.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(error("Username is required"));
        }
        if (password == null || password.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(error("Password is required"));
        }

        Map<String, Object> result = userService.loginUser(username.trim(), password);

        if ((boolean) result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
        }
    }

    /**
     * GET /health - Simple health check
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> resp = new HashMap<>();
        resp.put("status", "UP");
        resp.put("app", "AI Shopping Chat Bot");
        resp.put("version", "1.0.0");
        return ResponseEntity.ok(resp);
    }

    private Map<String, Object> error(String message) {
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", false);
        resp.put("message", message);
        return resp;
    }
}
