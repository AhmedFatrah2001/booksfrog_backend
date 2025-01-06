package org.example.booksfrog.controller;

import org.example.booksfrog.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/tokens")
public class TokenController {

    private final TokenService tokenService;

    @Autowired
    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping("/grant")
    public ResponseEntity<?> grantDailyTokens(@RequestParam Long userId) {
        tokenService.grantDailyTokens(userId);
        return ResponseEntity.ok("Daily tokens granted successfully.");
    }

    @GetMapping("/{userId}/timeleftinsecond")
    public ResponseEntity<?> getTimeLeftForDailyToken(@PathVariable Long userId) {
        try {
            long timeLeft = tokenService.getTimeLeftForDailyToken(userId);
            return ResponseEntity.ok(Map.of(
                "userId", userId,
                "timeLeftInSeconds", timeLeft
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/amount")
    public ResponseEntity<?> addTokens(@RequestParam Long userId, @RequestParam int amount) {
        try {
            tokenService.addTokens(userId, amount);

            return ResponseEntity.ok(Map.of(
                "userId", userId,
                "amountAdded", amount,
                "message", "Tokens added successfully."
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "error", e.getMessage()
            ));
        }
    }



    @GetMapping("/{userId}")
    public ResponseEntity<Integer> getTotalTokens(@PathVariable Long userId) {
        Integer totalTokens = tokenService.getTotalTokens(userId);
        return ResponseEntity.ok(totalTokens);
    }

    private Long getAuthenticatedUserId() {
        String userIdStr = SecurityContextHolder.getContext().getAuthentication().getName();
        return Long.parseLong(userIdStr);
    }
}

