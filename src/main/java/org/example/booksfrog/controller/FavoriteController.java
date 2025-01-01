package org.example.booksfrog.controller;

import org.example.booksfrog.dto.FavoriteFullDTO;
import org.example.booksfrog.dto.FavoriteIdDTO;
import org.example.booksfrog.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @Autowired
    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @PostMapping("/{userId}/{bookId}")
    public ResponseEntity<String> addFavorite(@PathVariable Long userId, @PathVariable Long bookId) {
        favoriteService.addFavorite(userId, bookId);
        return ResponseEntity.ok("Book added to favorites");
    }

    @GetMapping("/{userId}/book-ids")
    public ResponseEntity<List<FavoriteIdDTO>> getFavoriteBookIds(@PathVariable Long userId) {
        List<FavoriteIdDTO> favoriteBookIds = favoriteService.getFavoriteBookIdsByUserId(userId);
        return ResponseEntity.ok(favoriteBookIds);
    }

    @GetMapping("/{userId}/full-details")
    public ResponseEntity<List<FavoriteFullDTO>> getFavoriteFullDetails(@PathVariable Long userId) {
        List<FavoriteFullDTO> favoriteDetails = favoriteService.getFavoriteBookDetailsByUserId(userId);
        return ResponseEntity.ok(favoriteDetails);
    }

    @DeleteMapping("/{userId}/{bookId}")
    public ResponseEntity<String> removeFavorite(@PathVariable Long userId, @PathVariable Long bookId) {
        favoriteService.removeFavorite(userId, bookId);
        return ResponseEntity.ok("Book removed from favorites");
    }
}

