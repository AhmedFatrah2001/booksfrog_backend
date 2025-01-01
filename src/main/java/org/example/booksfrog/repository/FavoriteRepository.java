package org.example.booksfrog.repository;

import org.example.booksfrog.model.Book;
import org.example.booksfrog.model.Favorite;
import org.example.booksfrog.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUserId(Long userId);
    void deleteByUserIdAndBookId(Long userId, Long bookId);
    boolean existsByUserAndBook(User user, Book book);

}

