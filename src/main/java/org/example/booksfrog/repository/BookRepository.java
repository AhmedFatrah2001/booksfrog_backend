package org.example.booksfrog.repository;

import org.example.booksfrog.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByCategory_Id(Long categoryId);
    @Query(value = "SELECT * FROM book ORDER BY id DESC LIMIT 12", nativeQuery = true)
    List<Book> findLast12Books();

    // Custom query methods for searching with pagination
    Page<Book> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    Page<Book> findByAuthorContainingIgnoreCase(String author, Pageable pageable);

    @Query("SELECT b FROM Book b WHERE b.category.name LIKE %:categoryName%")
    Page<Book> findByCategoryNameContainingIgnoreCase(String categoryName, Pageable pageable);

}
