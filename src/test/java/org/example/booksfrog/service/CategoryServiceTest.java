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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category sampleCategory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sampleCategory = new Category();
        sampleCategory.setId(1L);
        sampleCategory.setName("Fiction");
    }

    @Test
    void testGetCategoryById() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(sampleCategory));

        Optional<Category> category = categoryService.getCategoryById(1L);

        assertTrue(category.isPresent());
        assertEquals("Fiction", category.get().getName());
        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    void testGetCategoryByName() {
        when(categoryRepository.findByName("Fiction")).thenReturn(sampleCategory);

        Category category = categoryService.getCategoryByName("Fiction");

        assertNotNull(category);
        assertEquals("Fiction", category.getName());
        verify(categoryRepository, times(1)).findByName("Fiction");
    }

    @Test
    void testCreateCategory() {
        when(categoryRepository.save(any(Category.class))).thenReturn(sampleCategory);

        Category category = new Category();
        category.setName("Science");

        Category createdCategory = categoryService.createCategory(category);

        assertNotNull(createdCategory);
        assertEquals("Fiction", createdCategory.getName());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void testDeleteCategory() {
        doNothing().when(categoryRepository).deleteById(1L);

        categoryService.deleteCategory(1L);

        verify(categoryRepository, times(1)).deleteById(1L);
    }

    @Test
    void testUpdateCategory() {
        when(categoryRepository.save(any(Category.class))).thenReturn(sampleCategory);

        Category category = new Category();
        category.setId(1L);
        category.setName("Updated Name");

        Category updatedCategory = categoryService.updateCategory(category);

        assertNotNull(updatedCategory);
        assertEquals("Fiction", updatedCategory.getName());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void testGetAllCategories() {
        List<Category> categories = Arrays.asList(sampleCategory, new Category());
        Page<Category> page = new PageImpl<>(categories);
        when(categoryRepository.findAll(any(PageRequest.class))).thenReturn(page);

        Page<Category> result = categoryService.getAllCategories(PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        verify(categoryRepository, times(1)).findAll(any(PageRequest.class));
    }
}
