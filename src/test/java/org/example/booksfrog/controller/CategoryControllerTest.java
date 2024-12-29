package org.example.booksfrog.controller;

import org.example.booksfrog.dto.BookDTO;
import org.example.booksfrog.model.Category;
import org.example.booksfrog.service.BookService;
import org.example.booksfrog.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @Mock
    private BookService bookService;

    @InjectMocks
    private CategoryController categoryController;

    private Category category;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        category = Category.builder()
                .id(1L)
                .name("Test Category")
                .build();
    }

    @Test
    void testGetCategoryById_CategoryExists() {
        when(categoryService.getCategoryById(1L)).thenReturn(Optional.of(category));

        ResponseEntity<Category> response = categoryController.getCategoryById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(category.getId(), response.getBody().getId());

        verify(categoryService, times(1)).getCategoryById(1L);
    }

    @Test
    void testGetCategoryById_CategoryNotFound() {
        when(categoryService.getCategoryById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Category> response = categoryController.getCategoryById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

        verify(categoryService, times(1)).getCategoryById(1L);
    }

    @Test
    void testGetAllCategories() {
        Page<Category> categoryPage = new PageImpl<>(Collections.singletonList(category));
        when(categoryService.getAllCategories(PageRequest.of(0, 10))).thenReturn(categoryPage);

        ResponseEntity<Page<Category>> response = categoryController.getAllCategories(0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getContent().size());

        verify(categoryService, times(1)).getAllCategories(PageRequest.of(0, 10));
    }

    @Test
    void testCreateCategory() {
        when(categoryService.createCategory(any(Category.class))).thenReturn(category);

        ResponseEntity<Category> response = categoryController.createCategory(category);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(category.getName(), response.getBody().getName());

        verify(categoryService, times(1)).createCategory(any(Category.class));
    }

    @Test
    void testUpdateCategory_CategoryExists() {
        when(categoryService.getCategoryById(1L)).thenReturn(Optional.of(category));
        when(categoryService.updateCategory(any(Category.class))).thenReturn(category);

        ResponseEntity<Category> response = categoryController.updateCategory(1L, category);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(category.getName(), response.getBody().getName());

        verify(categoryService, times(1)).getCategoryById(1L);
        verify(categoryService, times(1)).updateCategory(any(Category.class));
    }

    @Test
    void testUpdateCategory_CategoryNotFound() {
        when(categoryService.getCategoryById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Category> response = categoryController.updateCategory(1L, category);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

        verify(categoryService, times(1)).getCategoryById(1L);
        verify(categoryService, never()).updateCategory(any(Category.class));
    }

    @Test
    void testDeleteCategory() {
        doNothing().when(categoryService).deleteCategory(1L);

        ResponseEntity<Void> response = categoryController.deleteCategory(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        verify(categoryService, times(1)).deleteCategory(1L);
    }




    @Test
    void testGetBooksByCategory_CategoryNotFound() {
        when(bookService.getBooksByCategoryId(1L)).thenReturn(Collections.emptyList());

        ResponseEntity<List<BookDTO>> response = categoryController.getBooksByCategory(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

        verify(bookService, times(1)).getBooksByCategoryId(1L);
    }
}
