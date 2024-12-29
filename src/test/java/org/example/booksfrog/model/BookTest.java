package org.example.booksfrog.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class BookTest {

    private Book book;

    @BeforeEach
    void setUp() {
        book = Book.builder()
                .id(1L)
                .title("Test Book")
                .author("Test Author")
                .summary("This is a test summary.")
                .views(10)
                .totalPages(100)
                .cover(new byte[]{1, 2, 3})
                .build();
    }

    @Test
    void testBookAttributes() {
        assertEquals(1L, book.getId());
        assertEquals("Test Book", book.getTitle());
        assertEquals("Test Author", book.getAuthor());
        assertEquals("This is a test summary.", book.getSummary());
        assertEquals(10, book.getViews());
        assertEquals(100, book.getTotalPages());
    }

    @Test
    void testSetAndGetContent() {
        byte[] content = {10, 20, 30};
        book.setContent(content);

        assertArrayEquals(content, book.getContent());
    }

    @Test
    void testSetAndGetCover() {
        byte[] cover = {5, 10, 15};
        book.setCover(cover);

        assertArrayEquals(cover, book.getCover());
    }

    @Test
    void testGetCoverImage() {
        String expectedBase64 = "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(book.getCover());
        assertEquals(expectedBase64, book.getCoverImage());
    }

    @Test
    void testGetCategoryId() {
        Category category = new Category();
        category.setId(2L);
        book.setCategory(category);

        assertEquals(2L, book.getCategoryId());
    }

    @Test
    void testGetCategoryId_NullCategory() {
        book.setCategory(null);
        assertNull(book.getCategoryId());
    }
}
