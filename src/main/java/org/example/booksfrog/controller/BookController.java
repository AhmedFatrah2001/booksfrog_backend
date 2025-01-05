package org.example.booksfrog.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.*;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.example.booksfrog.dto.BookDTO;
import org.example.booksfrog.mapper.BookMapper;
import org.example.booksfrog.model.Book;
import org.example.booksfrog.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;

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
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);

        ContentDisposition contentDisposition = ContentDisposition.inline()
                .filename("book-" + id + ".pdf")
                .build();
        headers.setContentDisposition(contentDisposition);

        return new ResponseEntity<>(content, headers, HttpStatus.OK);
    }
    @GetMapping("/{id}/content-img/{page}")
    public ResponseEntity<Map<String, Object>> getBookContentImage(@PathVariable Long id, @PathVariable int page) {
        try {
            byte[] content = bookService.getBookContentById(id);
            if (content == null || content.length == 0) {
                return ResponseEntity.noContent().build();
            }

            PDDocument document = PDDocument.load(content);
            int totalPages = document.getNumberOfPages();

            if (page >= totalPages) {
                document.close();
                return ResponseEntity.notFound().build();
            }

            PDFRenderer renderer = new PDFRenderer(document);
            BufferedImage image = renderer.renderImageWithDPI(page, 80, ImageType.RGB);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            String base64Image = Base64.getEncoder().encodeToString(baos.toByteArray());

            document.close();

            // Create a response map containing the image data and total pages
            Map<String, Object> response = new HashMap<>();
            response.put("image", base64Image);
            response.put("totalPages", totalPages);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
