package org.example.booksfrog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.booksfrog.model.Book;
import org.example.booksfrog.model.Category;
import org.example.booksfrog.model.Favorite;

@Data
@AllArgsConstructor
public class FavoriteFullDTO {
    private Long bookId;
    private String bookTitle;
    private String bookAuthor;
    private String bookSummary;
    private String bookCoverImage;
    private Long bookCategoryId;
    private String bookCategoryName;

    public static FavoriteFullDTO fromEntity(Favorite favorite) {
        Book book = favorite.getBook();
        Category category = book.getCategory();

        return new FavoriteFullDTO(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getSummary(),
                book.getCoverImage(),
                category != null ? category.getId() : null,
                category != null ? category.getName() : null
        );
    }
}
