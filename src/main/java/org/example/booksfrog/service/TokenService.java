package org.example.booksfrog.service;

import org.example.booksfrog.exception.InsufficientTokensException;
import org.example.booksfrog.model.Token;
import org.example.booksfrog.model.User;
import org.example.booksfrog.repository.TokenRepository;
import org.example.booksfrog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class TokenService {

    private final TokenRepository tokenRepository;

    private final UserRepository userRepository;

    @Autowired
    public TokenService(TokenRepository tokenRepository, UserRepository userRepository) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
    }

    public void grantDailyTokens(Long userId) {
    // Fetch the user entity
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found for user ID: " + userId));

    // Fetch or create the token entity for the user
    Token token = tokenRepository.findById(userId)
            .orElseGet(() -> {
                Token newToken = new Token();
                newToken.setUser(user); // Set the user entity
                newToken.setDailyTokens(0); // Default value
                newToken.setTotalTokens(0); // Start with 0 total tokens
                newToken.setLastReset(null); // No reset yet
                tokenRepository.save(newToken);
                return newToken;
            });

    LocalDateTime now = LocalDateTime.now();

    // Check if 24 hours have passed since the last reset
    if (token.getLastReset() == null || token.getLastReset().isBefore(now.minusDays(1))) {
        int dailyTokens = user.isPremium() ? 100 : 50;

        token.setTotalTokens(token.getTotalTokens() + dailyTokens);
        token.setLastReset(now);
        tokenRepository.save(token);
    }
}




    public void deductTokens(Long userId, int amount) {
        Token token = tokenRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Token not found for user ID: " + userId));

        if (token.getTotalTokens() < amount) {
            throw new InsufficientTokensException("User ID " + userId + " has insufficient tokens.");
        }

        // Deduct the tokens
        token.setTotalTokens(token.getTotalTokens() - amount);
        tokenRepository.save(token);
    }

    public long getTimeLeftForDailyToken(Long userId) {
        Token token = tokenRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Token not found for user ID: " + userId));

        LocalDateTime now = LocalDateTime.now();

        if (token.getLastReset() == null) {
            // If no reset has occurred, tokens can be granted immediately
            return 0;
        }

        // Calculate the time left in seconds
        LocalDateTime nextReset = token.getLastReset().plusDays(1);
        if (now.isAfter(nextReset)) {
            return 0; // Tokens can be re-granted immediately
        }

        return Duration.between(now, nextReset).getSeconds();
    }

    public void addTokens(Long userId, int amount) {
        Token token = tokenRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Token not found for user ID: " + userId));

        // Add the specified amount to the total tokens
        token.setTotalTokens(token.getTotalTokens() + amount);
        tokenRepository.save(token);
    }



    public Integer getTotalTokens(Long userId) {
        Token token = tokenRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Token not found for user ID: " + userId));
        return token.getTotalTokens();
    }

}

