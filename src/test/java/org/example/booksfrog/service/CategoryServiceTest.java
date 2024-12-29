package org.example.booksfrog.service;

import org.example.booksfrog.model.Category;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

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
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        Optional<Category> result = categoryService.getCategoryById(1L);

        assertTrue(result.isPresent());
        assertEquals(category.getId(), result.get().getId());
        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    void testGetCategoryById_CategoryNotFound() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Category> result = categoryService.getCategoryById(1L);

        assertFalse(result.isPresent());
        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    void testGetCategoryByName_CategoryExists() {
        when(categoryRepository.findByName("Test Category")).thenReturn(category);

        Category result = categoryService.getCategoryByName("Test Category");

        assertNotNull(result);
        assertEquals(category.getName(), result.getName());
        verify(categoryRepository, times(1)).findByName("Test Category");
    }

    @Test
    void testCreateCategory() {
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        Category createdCategory = categoryService.createCategory(category);

        assertNotNull(createdCategory);
        assertEquals(category.getName(), createdCategory.getName());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void testUpdateCategory() {
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        Category updatedCategory = categoryService.updateCategory(category);

        assertNotNull(updatedCategory);
        assertEquals(category.getName(), updatedCategory.getName());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void testDeleteCategory() {
        doNothing().when(categoryRepository).deleteById(1L);

        categoryService.deleteCategory(1L);

        verify(categoryRepository, times(1)).deleteById(1L);
    }

    @Test
    void testGetAllCategories() {
        Page<Category> categoryPage = new PageImpl<>(Collections.singletonList(category));
        when(categoryRepository.findAll(PageRequest.of(0, 10))).thenReturn(categoryPage);

        Page<Category> result = categoryService.getAllCategories(PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(category.getName(), result.getContent().get(0).getName());
        verify(categoryRepository, times(1)).findAll(PageRequest.of(0, 10));
    }
}
