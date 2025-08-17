package com.example.E_Library;

import com.example.E_Library.model.Book;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class BookTest {

    private Book book;

    @BeforeEach
    void setUp() {
        book = new Book();
    }

    @Test
    void testNoArgsConstructor() {
        Book newBook = new Book();
        assertNull(newBook.getBook_id());
        assertNull(newBook.getTitle());
        assertNull(newBook.getAuthor());
        assertNull(newBook.getDescription());
        assertNull(newBook.getFile_url());
        assertNull(newBook.getUploaded_by());
        assertNull(newBook.getUploaded_at());
        assertNull(newBook.getCategory_id());
    }

    @Test
    void testAllArgsConstructor() {
        Book newBook = new Book(1, "Test Title", "Test Author", "Test Description",
                "http://test.com/file", 1, "2023-01-01", 1);
        assertEquals(1, newBook.getBook_id());
        assertEquals("Test Title", newBook.getTitle());
        assertEquals("Test Author", newBook.getAuthor());
        assertEquals("Test Description", newBook.getDescription());
        assertEquals("http://test.com/file", newBook.getFile_url());
        assertEquals(1, newBook.getUploaded_by());
        assertEquals("2023-01-01", newBook.getUploaded_at());
        assertEquals(1, newBook.getCategory_id());
    }

    @Test
    void testGetAndSetBookId() {
        book.setBook_id(1);
        assertEquals(1, book.getBook_id());
    }

    @Test
    void testGetAndSetTitle() {
        book.setTitle("Test Title");
        assertEquals("Test Title", book.getTitle());
    }

    @Test
    void testGetAndSetAuthor() {
        book.setAuthor("Test Author");
        assertEquals("Test Author", book.getAuthor());
    }

    @Test
    void testGetAndSetDescription() {
        book.setDescription("Test Description");
        assertEquals("Test Description", book.getDescription());
    }

    @Test
    void testGetAndSetFileUrl() {
        book.setFile_url("http://test.com/file");
        assertEquals("http://test.com/file", book.getFile_url());
    }

    @Test
    void testGetAndSetUploadedBy() {
        book.setUploaded_by(1);
        assertEquals(1, book.getUploaded_by());
    }

    @Test
    void testGetAndSetUploadedAt() {
        book.setUploaded_at("2023-01-01");
        assertEquals("2023-01-01", book.getUploaded_at());
    }

    @Test
    void testGetAndSetCategoryId() {
        book.setCategory_id(1);
        assertEquals(1, book.getCategory_id());
    }

    @Test
    void testEqualsAndHashCode() {
        Book book1 = new Book(1, "Title", "Author", "Desc", "url", 1, "2023-01-01", 1);
        Book book2 = new Book(1, "Title", "Author", "Desc", "url", 1, "2023-01-01", 1);
        Book book3 = new Book(2, "Different", "Author", "Desc", "url", 1, "2023-01-01", 1);

        assertEquals(book1, book2);
        assertNotEquals(book1, book3);
        assertEquals(book1.hashCode(), book2.hashCode());
    }
}