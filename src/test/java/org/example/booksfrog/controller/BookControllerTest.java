package org.example.booksfrog.controller;

import org.example.booksfrog.dto.BookDTO;
import org.example.booksfrog.mapper.BookMapper;
import org.example.booksfrog.model.Book;
import org.example.booksfrog.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookControllerTest {

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookController bookController;

    private Book book;
    private BookDTO bookDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        book = Book.builder()
                .id(1L)
                .title("Test Book")
                .author("Test Author")
                .summary("Test Summary")
                .views(10)
                .totalPages(100)
                .build();

        bookDTO = BookMapper.toDTO(book);
    }

    @Test
    void testGetBookById_BookExists() {
        when(bookService.getBookById(1L)).thenReturn(Optional.of(book));

        ResponseEntity<BookDTO> response = bookController.getBookById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(bookDTO, response.getBody());
        verify(bookService, times(1)).getBookById(1L);
    }

    @Test
    void testGetBookById_BookNotFound() {
        when(bookService.getBookById(1L)).thenReturn(Optional.empty());

        ResponseEntity<BookDTO> response = bookController.getBookById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(bookService, times(1)).getBookById(1L);
    }

    @Test
    void testCreateBook() {
        when(bookService.createBook(any(Book.class))).thenReturn(book);

        ResponseEntity<Book> response = bookController.createBook(book);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(book, response.getBody());
        verify(bookService, times(1)).createBook(book);
    }

    @Test
    void testUpdateBook_BookExists() {
        when(bookService.getBookById(1L)).thenReturn(Optional.of(book));
        when(bookService.updateBook(any(Book.class))).thenReturn(book);

        ResponseEntity<Book> response = bookController.updateBook(1L, book);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(book, response.getBody());
        verify(bookService, times(1)).getBookById(1L);
        verify(bookService, times(1)).updateBook(book);
    }

    @Test
    void testUpdateBook_BookNotFound() {
        when(bookService.getBookById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Book> response = bookController.updateBook(1L, book);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(bookService, times(1)).getBookById(1L);
        verify(bookService, never()).updateBook(any(Book.class));
    }

    @Test
    void testDeleteBook() {
        doNothing().when(bookService).deleteBook(1L);

        ResponseEntity<Void> response = bookController.deleteBook(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(bookService, times(1)).deleteBook(1L);
    }

    @Test
    void testGetLast12Books() {
        when(bookService.getLast12Books()).thenReturn(Collections.singletonList(bookDTO));

        List<BookDTO> books = bookController.getLast12Books();

        assertNotNull(books);
        assertEquals(1, books.size());
        assertEquals(bookDTO, books.get(0));
        verify(bookService, times(1)).getLast12Books();
    }

    @Test
    void testGetAllBooks() {
        Page<BookDTO> bookPage = new PageImpl<>(Collections.singletonList(bookDTO));
        when(bookService.getAllBooks(0, 10)).thenReturn(bookPage);

        ResponseEntity<Page<BookDTO>> response = bookController.getAllBooks(0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(bookPage, response.getBody());
        verify(bookService, times(1)).getAllBooks(0, 10);
    }

    @Test
    void testAssignCategoryToBook_Success() {
        Map<String, Long> requestBody = new HashMap<>();
        requestBody.put("categoryId", 2L);

        when(bookService.assignCategory(1L, 2L)).thenReturn(true);

        ResponseEntity<String> response = bookController.assignCategoryToBook(1L, requestBody);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Category assigned successfully.", response.getBody());
        verify(bookService, times(1)).assignCategory(1L, 2L);
    }

    @Test
    void testAssignCategoryToBook_Failure() {
        Map<String, Long> requestBody = new HashMap<>();
        requestBody.put("categoryId", 2L);

        when(bookService.assignCategory(1L, 2L)).thenReturn(false);

        ResponseEntity<String> response = bookController.assignCategoryToBook(1L, requestBody);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Book or Category not found, or the book already has a category.", response.getBody());
        verify(bookService, times(1)).assignCategory(1L, 2L);
    }

    @Test
    void testSearchBooks_WithTitle() {
        Page<BookDTO> bookPage = new PageImpl<>(Collections.singletonList(bookDTO));
        when(bookService.searchBooksByTitle("Test", PageRequest.of(0, 10))).thenReturn(bookPage);

        ResponseEntity<Page<BookDTO>> response = bookController.searchBooks("Test", null, null, PageRequest.of(0, 10));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(bookPage, response.getBody());
        verify(bookService, times(1)).searchBooksByTitle("Test", PageRequest.of(0, 10));
    }

    @Test
    void testSearchBooks_WithAuthor() {
        Page<BookDTO> bookPage = new PageImpl<>(Collections.singletonList(bookDTO));
        when(bookService.searchBooksByAuthor("Author", PageRequest.of(0, 10))).thenReturn(bookPage);

        ResponseEntity<Page<BookDTO>> response = bookController.searchBooks(null, "Author", null, PageRequest.of(0, 10));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(bookPage, response.getBody());
        verify(bookService, times(1)).searchBooksByAuthor("Author", PageRequest.of(0, 10));
    }

    @Test
    void testSearchBooks_WithCategory() {
        Page<BookDTO> bookPage = new PageImpl<>(Collections.singletonList(bookDTO));
        when(bookService.searchBooksByCategory("Category", PageRequest.of(0, 10))).thenReturn(bookPage);

        ResponseEntity<Page<BookDTO>> response = bookController.searchBooks(null, null, "Category", PageRequest.of(0, 10));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(bookPage, response.getBody());
        verify(bookService, times(1)).searchBooksByCategory("Category", PageRequest.of(0, 10));
    }

    @Test
    void testSearchBooks_NoCriteria() {
        ResponseEntity<Page<BookDTO>> response = bookController.searchBooks(null, null, null, PageRequest.of(0, 10));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).isEmpty());
        verify(bookService, never()).searchBooksByTitle(anyString(), any(PageRequest.class));
        verify(bookService, never()).searchBooksByAuthor(anyString(), any(PageRequest.class));
        verify(bookService, never()).searchBooksByCategory(anyString(), any(PageRequest.class));
    }

    @Test
    void testRecalculateTotalPages() {
        doNothing().when(bookService).recalculateTotalPages();

        ResponseEntity<String> response = bookController.recalculateTotalPages();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Total pages recalculated for all books.", response.getBody());
        verify(bookService, times(1)).recalculateTotalPages();
    }

    @Test
    void testGetBookContent_Exists() {
        byte[] content = new byte[]{1, 2, 3};
        when(bookService.getBookContentById(1L)).thenReturn(content);

        ResponseEntity<byte[]> response = bookController.getBookContent(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertArrayEquals(content, response.getBody());
        assertEquals(MediaType.APPLICATION_PDF, response.getHeaders().getContentType());
        verify(bookService, times(1)).getBookContentById(1L);
    }

    @Test
    void testGetBookContent_NotExists() {
        when(bookService.getBookContentById(1L)).thenReturn(new byte[]{});

        ResponseEntity<byte[]> response = bookController.getBookContent(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(bookService, times(1)).getBookContentById(1L);
    }
}