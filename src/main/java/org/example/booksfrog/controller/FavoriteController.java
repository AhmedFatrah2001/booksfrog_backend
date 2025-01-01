package org.example.booksfrog.controller;

import org.example.booksfrog.model.Favorite;
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

    @GetMapping("/{userId}")
    public ResponseEntity<List<Favorite>> getFavorites(@PathVariable Long userId) {
        List<Favorite> favorites = favoriteService.getFavoritesByUserId(userId);
        return ResponseEntity.ok(favorites);
    }

    @DeleteMapping("/{userId}/{bookId}")
    public ResponseEntity<String> removeFavorite(@PathVariable Long userId, @PathVariable Long bookId) {
        favoriteService.removeFavorite(userId, bookId);
        return ResponseEntity.ok("Book removed from favorites");
    }
}

