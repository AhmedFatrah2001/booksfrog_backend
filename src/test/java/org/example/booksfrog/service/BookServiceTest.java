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

import java.util.Arrays;
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

    private Book sampleBook;
    private Category sampleCategory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sampleBook = new Book();
        sampleBook.setId(1L);
        sampleBook.setTitle("Sample Book");
        sampleBook.setAuthor("Author Name");
        sampleBook.setContent(new byte[10]); // Dummy content

        sampleCategory = new Category();
        sampleCategory.setId(1L);
        sampleCategory.setName("Category Name");
    }

    @Test
    void testGetBookById() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(sampleBook));

        Optional<Book> result = bookService.getBookById(1L);

        assertTrue(result.isPresent());
        assertEquals("Sample Book", result.get().getTitle());
        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    void testGetBookContentById() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(sampleBook));

        byte[] content = bookService.getBookContentById(1L);

        assertNotNull(content);
        assertEquals(10, content.length);
        verify(bookRepository, times(1)).findById(1L);
    }



    @Test
    void testDeleteBook() {
        doNothing().when(bookRepository).deleteById(1L);

        bookService.deleteBook(1L);

        verify(bookRepository, times(1)).deleteById(1L);
    }



    @Test
    void testGetLast12Books() {
        List<Book> books = Arrays.asList(sampleBook, new Book());
        when(bookRepository.findLast12Books()).thenReturn(books);

        List<BookDTO> result = bookService.getLast12Books();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(bookRepository, times(1)).findLast12Books();
    }

    @Test
    void testAssignCategory_Success() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(sampleBook));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(sampleCategory));

        boolean success = bookService.assignCategory(1L, 1L);

        assertTrue(success);
        verify(bookRepository, times(1)).save(sampleBook);
    }

    @Test
    void testAssignCategory_Failure() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        boolean success = bookService.assignCategory(1L, 1L);

        assertFalse(success);
        verify(bookRepository, never()).save(any(Book.class));
    }


    @Test
    void testSearchBooksByTitle() {
        Page<Book> page = new PageImpl<>(List.of(sampleBook));
        when(bookRepository.findByTitleContainingIgnoreCase(eq("Sample"), any(PageRequest.class)))
                .thenReturn(page);

        Page<BookDTO> result = bookService.searchBooksByTitle("Sample", PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(bookRepository, times(1))
                .findByTitleContainingIgnoreCase(eq("Sample"), any(PageRequest.class));
    }
}
