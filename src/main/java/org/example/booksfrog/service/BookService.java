package org.example.booksfrog.service;

import java.util.List;
import java.util.Optional;

import org.example.booksfrog.dto.BookDTO;
import org.example.booksfrog.mapper.BookMapper;
import org.example.booksfrog.model.Book;
import org.example.booksfrog.model.Category;
import org.example.booksfrog.repository.BookRepository;
import org.example.booksfrog.repository.CategoryRepository;
import org.example.booksfrog.util.PdfUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class BookService {


    private final BookRepository bookRepository;

    private final CategoryRepository categoryRepository;

    @Autowired
    public BookService(BookRepository bookRepository, CategoryRepository categoryRepository) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
    }

    // Fetch a book by its ID
    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    // New: Fetch only the content (PDF bytes) of a book by its ID
    public byte[] getBookContentById(Long id) {
        // You might want to throw an exception if not found
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with ID " + id));

        return book.getContent(); // Could be null if no PDF was uploaded
    }

    // Create a new book
    public Book createBook(Book book) {
        if (book.getContent() != null) {
            book.setTotalPages(PdfUtils.getPageCount(book.getContent()));
        }
        return bookRepository.save(book);
    }

    // Delete a book by its ID
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    // Update an existing book
    public Book updateBook(Book book) {
        if (book.getContent() != null) {
            book.setTotalPages(PdfUtils.getPageCount(book.getContent()));
        }
        return bookRepository.save(book);
    }

    // Fetch the latest 12 books
    public List<BookDTO> getLast12Books() {
        return bookRepository.findLast12Books()
                .stream()
                .map(BookMapper::toDTO)
                .toList();
    }

    // Fetch book content by ID, returning it as byte[]
    // (We have a separate method above now: getBookContentById)

    // Fetch all books with pagination
    public Page<BookDTO> getAllBooks(int page, int size) {
        Page<Book> bookPage = bookRepository.findAll(PageRequest.of(page, size));
        return bookPage.map(BookMapper::toDTO);
    }

    // Assign a category to a book
    public boolean assignCategory(Long bookId, Long categoryId) {
        Optional<Book> bookOptional = bookRepository.findById(bookId);
        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);

        if (bookOptional.isPresent() && categoryOptional.isPresent()) {
            Book book = bookOptional.get();
            if (book.getCategory() == null) {
                book.setCategory(categoryOptional.get());
                bookRepository.save(book);
                return true;
            }
        }
        return false;
    }

    // Recalculate total pages for all books
    public void recalculateTotalPages() {
        List<Book> books = bookRepository.findAll();
        for (Book book : books) {
            if (book.getContent() != null && (book.getTotalPages() == null || book.getTotalPages() == 0)) {
                book.setTotalPages(PdfUtils.getPageCount(book.getContent()));
                bookRepository.save(book);
            }
        }
    }

    // Search books by title with pagination
    public Page<BookDTO> searchBooksByTitle(String title, org.springframework.data.domain.Pageable pageable) {
        return bookRepository.findByTitleContainingIgnoreCase(title, pageable)
                .map(BookMapper::toDTO);
    }

    // Search books by author with pagination
    public Page<BookDTO> searchBooksByAuthor(String author, org.springframework.data.domain.Pageable pageable) {
        return bookRepository.findByAuthorContainingIgnoreCase(author, pageable)
                .map(BookMapper::toDTO);
    }

    // Search books by category with pagination
    public Page<BookDTO> searchBooksByCategory(String categoryName, org.springframework.data.domain.Pageable pageable) {
        return bookRepository.findByCategoryNameContainingIgnoreCase(categoryName, pageable)
                .map(BookMapper::toDTO);
    }

    public List<Book> getBooksByCategoryId(Long categoryId) {
        return bookRepository.findByCategory_Id(categoryId);
    }
}
