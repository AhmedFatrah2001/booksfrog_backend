package org.example.booksfrog.service;

import org.example.booksfrog.dto.FavoriteFullDTO;
import org.example.booksfrog.dto.FavoriteIdDTO;
import org.example.booksfrog.model.Book;
import org.example.booksfrog.model.Favorite;
import org.example.booksfrog.model.User;
import org.example.booksfrog.repository.BookRepository;
import org.example.booksfrog.repository.FavoriteRepository;
import org.example.booksfrog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;

    private final UserRepository userRepository;

    private final BookRepository bookRepository;

    @Autowired
    public FavoriteService(FavoriteRepository favoriteRepository, UserRepository userRepository, BookRepository bookRepository) {
        this.favoriteRepository = favoriteRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    public void addFavorite(Long userId, Long bookId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        // Check if the favorite already exists
        boolean exists = favoriteRepository.existsByUserAndBook(user, book);
        if (exists) {
            throw new RuntimeException("Book is already in favorites");
        }

        Favorite favorite = Favorite.builder()
                .user(user)
                .book(book)
                .build();
        favoriteRepository.save(favorite);
    }


    public List<FavoriteFullDTO> getFavoriteBookDetailsByUserId(Long userId) {
        return favoriteRepository.findByUserId(userId)
                .stream()
                .map(FavoriteFullDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<FavoriteIdDTO> getFavoriteBookIdsByUserId(Long userId) {
        return favoriteRepository.findByUserId(userId)
                .stream()
                .map(favorite -> new FavoriteIdDTO(favorite.getBook().getId()))
                .collect(Collectors.toList());
    }
    @Transactional
    public void removeFavorite(Long userId, Long bookId) {
        favoriteRepository.deleteByUserIdAndBookId(userId, bookId);
    }
}

