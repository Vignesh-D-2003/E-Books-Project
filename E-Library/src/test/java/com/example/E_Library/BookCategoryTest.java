package com.example.E_Library;

import org.junit.jupiter.api.Test;

import com.example.E_Library.model.BookCategory;

import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class BookCategoryTest {
    
    private BookCategory bookCategory;
    
    @BeforeEach
    void setUp() {
        bookCategory = new BookCategory();
    }
    
    @Test
    void testNoArgsConstructor() {
        BookCategory category = new BookCategory();
        assertNull(category.getCategory_id());
        assertNull(category.getCategory_name());
    }
    
    @Test
    void testAllArgsConstructor() {
        BookCategory category = new BookCategory(1, "Fiction");
        assertEquals(1, category.getCategory_id());
        assertEquals("Fiction", category.getCategory_name());
    }
    
    @Test
    void testGetAndSetCategoryId() {
        bookCategory.setCategory_id(1);
        assertEquals(1, bookCategory.getCategory_id());
    }
    
    @Test
    void testGetAndSetCategoryName() {
        bookCategory.setCategory_name("Science Fiction");
        assertEquals("Science Fiction", bookCategory.getCategory_name());
    }
    
    @Test
    void testSetCategoryIdWithNull() {
        bookCategory.setCategory_id(null);
        assertNull(bookCategory.getCategory_id());
    }
    
    @Test
    void testSetCategoryNameWithNull() {
        bookCategory.setCategory_name(null);
        assertNull(bookCategory.getCategory_name());
    }
    
    @Test
    void testEqualsAndHashCode() {
        BookCategory category1 = new BookCategory(1, "Fiction");
        BookCategory category2 = new BookCategory(1, "Fiction");
        BookCategory category3 = new BookCategory(2, "Non-Fiction");
        
        assertEquals(category1, category2);
        assertNotEquals(category1, category3);
        assertEquals(category1.hashCode(), category2.hashCode());
    }
    
    @Test
    void testToString() {
        BookCategory category = new BookCategory(1, "Fiction");
        String toString = category.toString();
        assertTrue(toString.contains("1"));
        assertTrue(toString.contains("Fiction"));
    }
}