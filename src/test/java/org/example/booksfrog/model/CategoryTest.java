package org.example.booksfrog.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CategoryTest {

    private Category category;

    @BeforeEach
    void setUp() {
        category = Category.builder()
                .id(1L)
                .name("Test Category")
                .books(new ArrayList<>())
                .build();
    }

    @Test
    void testCategoryAttributes() {
        assertEquals(1L, category.getId());
        assertEquals("Test Category", category.getName());
    }

    @Test
    void testSetAndGetBooks() {
        Book book1 = Book.builder().id(1L).title("Book 1").build();
        Book book2 = Book.builder().id(2L).title("Book 2").build();

        List<Book> books = new ArrayList<>();
        books.add(book1);
        books.add(book2);

        category.setBooks(books);

        assertEquals(2, category.getBooks().size());
        assertTrue(category.getBooks().contains(book1));
        assertTrue(category.getBooks().contains(book2));
    }

    @Test
    void testRemoveBook() {
        Book book = Book.builder().id(1L).title("Book 1").build();
        category.getBooks().add(book);

        assertEquals(1, category.getBooks().size());

        category.getBooks().remove(book);

        assertTrue(category.getBooks().isEmpty());
    }

    @Test
    void testAddBook() {
        Book book = Book.builder().id(1L).title("New Book").build();

        category.getBooks().add(book);

        assertEquals(1, category.getBooks().size());
        assertEquals("New Book", category.getBooks().get(0).getTitle());
    }
}
