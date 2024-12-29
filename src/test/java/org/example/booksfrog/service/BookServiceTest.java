package org.example.booksfrog.service;

import org.example.booksfrog.dto.BookDTO;
import org.example.booksfrog.model.Book;
import org.example.booksfrog.model.Category;
import org.example.booksfrog.repository.BookRepository;
import org.example.booksfrog.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private BookService bookService;

    private Book book;
    private Category category;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        category = Category.builder().id(1L).name("Test Category").build();
        book = Book.builder()
                .id(1L)
                .title("Test Book")
                .author("Test Author")
                .summary("Test Summary")
                .category(category)
                .views(10)
                .totalPages(100)
                .build();
    }

    @Test
    void testGetBookById_BookExists() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        Optional<Book> result = bookService.getBookById(1L);

        assertTrue(result.isPresent());
        assertEquals(book.getId(), result.get().getId());
        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    void testGetBookById_BookNotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Book> result = bookService.getBookById(1L);

        assertFalse(result.isPresent());
        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    void testGetBookContentById_BookExists() {
        book.setContent(new byte[]{1, 2, 3});
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        byte[] content = bookService.getBookContentById(1L);

        assertArrayEquals(new byte[]{1, 2, 3}, content);
        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    void testGetBookContentById_BookNotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> bookService.getBookContentById(1L));
        assertEquals("Book not found with ID 1", exception.getMessage());
    }

    @Test
    void testCreateBook() {
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        Book createdBook = bookService.createBook(book);

        assertNotNull(createdBook);
        assertEquals(book.getTitle(), createdBook.getTitle());
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void testUpdateBook() {
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        Book updatedBook = bookService.updateBook(book);

        assertNotNull(updatedBook);
        assertEquals(book.getTitle(), updatedBook.getTitle());
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void testDeleteBook() {
        doNothing().when(bookRepository).deleteById(1L);

        bookService.deleteBook(1L);

        verify(bookRepository, times(1)).deleteById(1L);
    }

    @Test
    void testGetBooksByCategoryId() {
        when(bookRepository.findByCategory_Id(1L)).thenReturn(Collections.singletonList(book));

        List<Book> books = bookService.getBooksByCategoryId(1L);

        assertNotNull(books);
        assertEquals(1, books.size());
        assertEquals(book.getTitle(), books.get(0).getTitle());
        verify(bookRepository, times(1)).findByCategory_Id(1L);
    }


    @Test
    void testAssignCategory_CategoryNotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        boolean result = bookService.assignCategory(1L, 1L);

        assertFalse(result);
        verify(bookRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).findById(1L);
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void testRecalculateTotalPages_WithContentAndPagesNull() {
        book.setContent(new byte[]{1, 2, 3});
        book.setTotalPages(null);
        when(bookRepository.findAll()).thenReturn(Collections.singletonList(book));
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        bookService.recalculateTotalPages();

        assertNotNull(book.getTotalPages());
        verify(bookRepository, times(1)).findAll();
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void testRecalculateTotalPages_WithContentAndPagesNotNull() {
        book.setContent(new byte[]{1, 2, 3});
        book.setTotalPages(100);
        when(bookRepository.findAll()).thenReturn(Collections.singletonList(book));

        bookService.recalculateTotalPages();

        verify(bookRepository, times(1)).findAll();
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void testRecalculateTotalPages_WithoutContent() {
        book.setContent(null);
        when(bookRepository.findAll()).thenReturn(Collections.singletonList(book));

        bookService.recalculateTotalPages();

        verify(bookRepository, times(1)).findAll();
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void testGetAllBooks() {
        Page<Book> bookPage = new PageImpl<>(Collections.singletonList(book));
        when(bookRepository.findAll(PageRequest.of(0, 10))).thenReturn(bookPage);

        Page<BookDTO> result = bookService.getAllBooks(0, 10);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(book.getTitle(), result.getContent().get(0).getTitle());
        verify(bookRepository, times(1)).findAll(PageRequest.of(0, 10));
    }

    @Test
    void testSearchBooksByTitle() {
        Page<Book> bookPage = new PageImpl<>(Collections.singletonList(book));
        when(bookRepository.findByTitleContainingIgnoreCase(eq("Test"), any(PageRequest.class))).thenReturn(bookPage);

        Page<BookDTO> result = bookService.searchBooksByTitle("Test", PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(book.getTitle(), result.getContent().get(0).getTitle());
        verify(bookRepository, times(1)).findByTitleContainingIgnoreCase(eq("Test"), any(PageRequest.class));
    }

    @Test
    void testSearchBooksByAuthor() {
        Page<Book> bookPage = new PageImpl<>(Collections.singletonList(book));
        when(bookRepository.findByAuthorContainingIgnoreCase(eq("Author"), any(PageRequest.class))).thenReturn(bookPage);

        Page<BookDTO> result = bookService.searchBooksByAuthor("Author", PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(book.getAuthor(), result.getContent().get(0).getAuthor());
        verify(bookRepository, times(1)).findByAuthorContainingIgnoreCase(eq("Author"), any(PageRequest.class));
    }

    @Test
    void testSearchBooksByCategory() {
        Page<Book> bookPage = new PageImpl<>(Collections.singletonList(book));
        when(bookRepository.findByCategoryNameContainingIgnoreCase(eq("Category"), any(PageRequest.class))).thenReturn(bookPage);

        Page<BookDTO> result = bookService.searchBooksByCategory("Category", PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(book.getCategory().getName(), result.getContent().get(0).getCategoryName());
        verify(bookRepository, times(1)).findByCategoryNameContainingIgnoreCase(eq("Category"), any(PageRequest.class));
    }

    @Test
    void testGetLast12Books() {
        when(bookRepository.findLast12Books()).thenReturn(Collections.singletonList(book));

        List<BookDTO> books = bookService.getLast12Books();

        assertNotNull(books);
        assertEquals(1, books.size());
        assertEquals(book.getTitle(), books.get(0).getTitle());
        verify(bookRepository, times(1)).findLast12Books();
    }
}