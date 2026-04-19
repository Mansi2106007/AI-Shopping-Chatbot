package com.shoppingbot.service;

import com.shoppingbot.model.Product;
import com.shoppingbot.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ChatbotService {

    @Autowired
    private ProductRepository productRepository;

    // In-memory session state per user session (keyed by sessionId)
    private final Map<String, Map<String, Object>> sessionState = new ConcurrentHashMap<>();

    /**
     * Main chat entry point
     */
    public Map<String, Object> processMessage(String message, String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            sessionId = "default";
        }

        // Get or create session
        Map<String, Object> session = sessionState.computeIfAbsent(sessionId, k -> new HashMap<>());

        String lowerMsg = message.toLowerCase().trim();
        Map<String, Object> response = new HashMap<>();

        // --- GREETING ---
        if (isGreeting(lowerMsg)) {
            clearSession(session);
            response.put("message", "👋 Hello! Welcome to **AI Shopping Bot**! 🛒\n\nI can help you find the perfect product. What are you looking for today?\n\n📱 **Phones** | 💻 **Laptops** | 🎧 **Accessories**\n\nJust type what you need!");
            response.put("type", "greeting");
            return response;
        }

        // --- HELP ---
        if (lowerMsg.contains("help") || lowerMsg.contains("what can you do")) {
            response.put("message", "🤖 Here's what I can help with:\n\n• **Find phones** by budget\n• **Compare products** by brand\n• **Check prices** and specs\n\nJust say something like:\n👉 \"I want a phone\"\n👉 \"Show me phones under 15000\"\n👉 \"Best Samsung phone\"");
            response.put("type", "help");
            return response;
        }

        // --- PHONE INTENT ---
        if (isPhoneIntent(lowerMsg)) {
            session.put("category", "phone");

            // Check if budget already in same message
            BigDecimal budget = extractBudget(lowerMsg);
            if (budget != null) {
                return fetchProductsForBudget("phone", budget, response);
            }

            response.put("message", "📱 Great choice! Phones are our specialty.\n\nWhat's your **budget**? You can say:\n• \"Under 10000\"\n• \"Under 20000\"\n• \"Under 50000\"\n• Or any amount that works for you!");
            response.put("type", "ask_budget");
            return response;
        }

        // --- BUDGET RESPONSE (if phone category is set) ---
        String category = (String) session.get("category");
        if (category != null) {
            BigDecimal budget = extractBudget(lowerMsg);
            if (budget != null) {
                return fetchProductsForBudget(category, budget, response);
            }
        }

        // --- DIRECT BUDGET (like "under 20000" without prior context) ---
        BigDecimal directBudget = extractBudget(lowerMsg);
        if (directBudget != null && category == null) {
            session.put("category", "phone");
            return fetchProductsForBudget("phone", directBudget, response);
        }

        // --- BRAND SEARCH ---
        String brand = extractBrand(lowerMsg);
        if (brand != null) {
            List<Product> products = productRepository.searchByKeyword(brand);
            if (!products.isEmpty()) {
                return buildProductListResponse(products.subList(0, Math.min(3, products.size())), "Here are **" + brand + "** products I found:", response);
            }
        }

        // --- SHOW ALL / LIST ---
        if (lowerMsg.contains("show all") || lowerMsg.contains("all phones") || lowerMsg.contains("list")) {
            List<Product> all = productRepository.findByCategory("phone");
            if (!all.isEmpty()) {
                return buildProductListResponse(all.subList(0, Math.min(5, all.size())), "📋 Here are our top phones:", response);
            }
        }

        // --- RESET / BYE ---
        if (lowerMsg.contains("bye") || lowerMsg.contains("exit") || lowerMsg.contains("quit") || lowerMsg.contains("reset")) {
            clearSession(session);
            response.put("message", "👋 Thanks for shopping with **AI Shopping Bot**! Have a great day! Come back anytime. 😊");
            response.put("type", "farewell");
            return response;
        }

        // --- THANK YOU ---
        if (lowerMsg.contains("thank") || lowerMsg.contains("thanks")) {
            response.put("message", "😊 You're welcome! Happy shopping! Is there anything else I can help you with?");
            response.put("type", "thanks");
            return response;
        }

        // --- DEFAULT FALLBACK ---
        response.put("message", "🤔 I didn't quite get that. I'm specialized in helping you find the **best phones** within your budget!\n\nTry saying:\n• **\"I want a phone\"**\n• **\"Phones under 20000\"**\n• **\"Show Samsung phones\"**\n\nOr type **help** for more options.");
        response.put("type", "fallback");
        return response;
    }

    // ─── Helper Methods ───────────────────────────────────────────────────────

    private boolean isGreeting(String msg) {
        return msg.matches(".*(\\bhi\\b|\\bhello\\b|\\bhey\\b|\\bnamaskar\\b|\\bnamaste\\b|\\bstart\\b|\\bbegin\\b).*");
    }

    private boolean isPhoneIntent(String msg) {
        return msg.matches(".*(\\bphone\\b|\\bmobile\\b|\\bsmartphone\\b|\\bhandset\\b|\\bflagship\\b).*");
    }

    private String extractBrand(String msg) {
        String[] brands = {"apple", "samsung", "oneplus", "xiaomi", "redmi", "realme", "poco", "motorola", "nokia", "oppo", "vivo", "iqoo", "tecno", "infinix"};
        for (String brand : brands) {
            if (msg.contains(brand)) {
                return brand.substring(0, 1).toUpperCase() + brand.substring(1);
            }
        }
        return null;
    }

    private BigDecimal extractBudget(String msg) {
        // Remove commas
        msg = msg.replace(",", "").replace("₹", "").replace("rs", "").replace("inr", "").trim();

        // Patterns: "under 20000", "below 15000", "less than 10000", "20000", "20k", "budget 15000"
        String[] patterns = {
            "under\\s+(\\d+)",
            "below\\s+(\\d+)",
            "less\\s+than\\s+(\\d+)",
            "within\\s+(\\d+)",
            "budget\\s+(?:of\\s+|is\\s+)?(\\d+)",
            "upto\\s+(\\d+)",
            "up\\s+to\\s+(\\d+)",
            "(\\d+)k\\b",   // 20k → 20000
            "\\b(\\d{4,6})\\b" // raw number 4-6 digits
        };

        for (String pattern : patterns) {
            java.util.regex.Matcher m = java.util.regex.Pattern.compile(pattern).matcher(msg);
            if (m.find()) {
                String numStr = m.group(1);
                try {
                    BigDecimal val = new BigDecimal(numStr);
                    if (pattern.contains("k\\b")) val = val.multiply(BigDecimal.valueOf(1000));
                    if (val.compareTo(BigDecimal.valueOf(500)) > 0) { // ignore tiny numbers
                        return val;
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
        return null;
    }

    private Map<String, Object> fetchProductsForBudget(String category, BigDecimal budget, Map<String, Object> response) {
        List<Product> products = productRepository.findAffordableProducts(category, budget);

        if (products.isEmpty()) {
            response.put("message", "😔 Sorry, no " + category + "s found under ₹" + budget.toPlainString() + ".\n\nTry a higher budget or say **\"show all phones\"** to see everything we have!");
            response.put("type", "no_results");
            return response;
        }

        List<Product> top3 = products.subList(0, Math.min(3, products.size()));
        String header = "🎯 Here are the **best " + category + "s under ₹" + budget.toPlainString() + "**:";
        return buildProductListResponse(top3, header, response);
    }

    private Map<String, Object> buildProductListResponse(List<Product> products, String header, Map<String, Object> response) {
        List<Map<String, Object>> productList = new ArrayList<>();
        for (Product p : products) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", p.getId());
            item.put("name", p.getName());
            item.put("brand", p.getBrand());
            item.put("price", p.getPrice());
            item.put("description", p.getDescription());
            item.put("imageUrl", p.getImageUrl());
            productList.add(item);
        }

        response.put("message", header);
        response.put("type", "products");
        response.put("products", productList);
        response.put("followUp", "💬 Want to see more options or change your budget? Just ask!");
        return response;
    }

    private void clearSession(Map<String, Object> session) {
        session.clear();
    }
}
