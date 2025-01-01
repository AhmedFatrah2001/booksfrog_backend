package org.example.booksfrog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

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
}
