package org.example.booksfrog.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.example.booksfrog.dto.BookDTO;
import org.example.booksfrog.mapper.BookMapper;
import org.example.booksfrog.model.Book;
import org.example.booksfrog.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDTO> getBookById(@PathVariable Long id) {
        Optional<Book> book = bookService.getBookById(id);
        return book.map(b -> ResponseEntity.ok(BookMapper.toDTO(b)))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody Book book) {
        Book createdBook = bookService.createBook(book);
        return new ResponseEntity<>(createdBook, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @RequestBody Book bookDetails) {
        Optional<Book> book = bookService.getBookById(id);
        if (book.isPresent()) {
            bookDetails.setId(id);
            Book updatedBook = bookService.updateBook(bookDetails);
            return ResponseEntity.ok(updatedBook);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/latest")
    public List<BookDTO> getLast12Books() {
        return bookService.getLast12Books();
    }

    @GetMapping
    public ResponseEntity<Page<BookDTO>> getAllBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<BookDTO> books = bookService.getAllBooks(page, size);
        return ResponseEntity.ok(books);
    }

    @PostMapping("/{bookId}/assign-category")
    public ResponseEntity<String> assignCategoryToBook(
            @PathVariable Long bookId,
            @RequestBody Map<String, Long> requestBody) {
        Long categoryId = requestBody.get("categoryId");

        boolean success = bookService.assignCategory(bookId, categoryId);

        if (success) {
            return ResponseEntity.ok("Category assigned successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Book or Category not found, or the book already has a category.");
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Page<BookDTO>> searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String category,
            Pageable pageable) {
        Page<BookDTO> books;
        if (title != null) {
            books = bookService.searchBooksByTitle(title, pageable);
        } else if (author != null) {
            books = bookService.searchBooksByAuthor(author, pageable);
        } else if (category != null) {
            books = bookService.searchBooksByCategory(category, pageable);
        } else {
            books = Page.empty(); // Return an empty page if no search criteria are provided
        }
        return ResponseEntity.ok(books);
    }

    @PostMapping("/recalculate-total-pages")
    public ResponseEntity<String> recalculateTotalPages() {
        bookService.recalculateTotalPages();
        return ResponseEntity.ok("Total pages recalculated for all books.");
    }

    // New endpoint to return PDF content
    @GetMapping("/{id}/content")
    public ResponseEntity<byte[]> getBookContent(@PathVariable Long id) {
        byte[] content = bookService.getBookContentById(id);
        if (content == null || content.length == 0) {
            return ResponseEntity.noContent().build(); 
        }

        // Set the PDF-specific headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);

        // Decide if you want to force download or display inline
        // "inline" means the browser might show it in a PDF viewer
        // "attachment" means the browser might trigger a download
        ContentDisposition contentDisposition = ContentDisposition.inline()
                .filename("book-" + id + ".pdf")
                .build();
        headers.setContentDisposition(contentDisposition);

        return new ResponseEntity<>(content, headers, HttpStatus.OK);
    }
}
