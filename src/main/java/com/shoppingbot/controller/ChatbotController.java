package com.shoppingbot.controller;

import com.shoppingbot.service.ChatbotService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class ChatbotController {

    @Autowired
    private ChatbotService chatbotService;

    /**
     * GET /chat?message=...
     * Returns chatbot response for the given user message
     */
    @GetMapping("/chat")
    public ResponseEntity<Map<String, Object>> chat(
            @RequestParam("message") String message,
            HttpSession session) {

        if (message == null || message.trim().isEmpty()) {
            Map<String, Object> err = new HashMap<>();
            err.put("success", false);
            err.put("message", "Please type a message.");
            return ResponseEntity.badRequest().body(err);
        }

        String sessionId = session.getId();
        Map<String, Object> response = chatbotService.processMessage(message.trim(), sessionId);
        response.put("success", true);
        response.put("userMessage", message.trim());

        return ResponseEntity.ok(response);
    }

    /**
     * POST /chat - Alternative POST endpoint for chat
     */
    @PostMapping("/chat")
    public ResponseEntity<Map<String, Object>> chatPost(
            @RequestBody Map<String, String> body,
            HttpSession session) {

        String message = body.get("message");
        return chat(message, session);
    }
}
