package org.example.booksfrog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookDTO {
    private Long id;
    private String title;
    private String author;
    private String summary;
    private String coverImage;
    private Long categoryId;
    private String categoryName;
    private int views;
    private Integer totalPages;
}
 