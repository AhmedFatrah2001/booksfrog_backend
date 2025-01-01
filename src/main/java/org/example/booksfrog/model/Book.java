package org.example.booksfrog.model;

import lombok.*;
import jakarta.persistence.*;

import java.util.Base64;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column
    private String author;

    @Lob
    private String summary;

    @Lob
    private byte[] content;

    @Lob
    private byte[] cover;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "book")
    List<Favorite> favorites;

    @Column(nullable = false, columnDefinition = "int default 0")
    private int views;

    @Column(name = "total_pages", nullable = true)
    private Integer totalPages;


    public Book(Long id, String title, String author, String summary, byte[] cover, Long categoryId) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.summary = summary;
        this.cover = cover;
        this.category = new Category();
        this.category.setId(categoryId);
    }

    public Long getCategoryId() {
        return category != null ? category.getId() : null;
    }

    public String getCoverImage() {
        return cover != null ? "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(cover) : null;
    }
}
