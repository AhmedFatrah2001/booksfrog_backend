package org.example.booksfrog.mapper;

import org.example.booksfrog.dto.BookDTO;
import org.example.booksfrog.model.Book;


public class BookMapper {

    // Private constructor to prevent instantiation
    private BookMapper() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static BookDTO toDTO(Book book) {
        if (book == null) {
            return null;
        }

        return BookDTO.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .summary(book.getSummary())
                .coverImage(book.getCoverImage()) // Already Base64 encoded in the `getCoverImage` method
                .categoryId(book.getCategoryId())
                .categoryName(book.getCategory() != null ? book.getCategory().getName() : null) // Add categoryName
                .views(book.getViews())
                .totalPages(book.getTotalPages())
                .build();
    }
}
