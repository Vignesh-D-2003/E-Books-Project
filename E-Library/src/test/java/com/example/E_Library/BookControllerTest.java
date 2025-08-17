package com.example.E_Library;

import com.example.E_Library.controller.BookController;
import com.example.E_Library.model.Book;
import com.example.E_Library.service.SupabaseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SupabaseService supabaseService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllBooks() throws Exception {
        when(supabaseService.getAllBooks()).thenReturn("all books");

        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(content().string("all books"));
    }

    @Test
    void testGetBookById() throws Exception {
        when(supabaseService.getBookById("1")).thenReturn("book1");

        mockMvc.perform(get("/books/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("book1"));
    }

    @Test
    void testAddBook() throws Exception {
        Book book = new Book();
        when(supabaseService.addBook(any(Book.class))).thenReturn("book added");

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isOk())
                .andExpect(content().string("book added"));
    }

    @Test
    void testUpdateBook() throws Exception {
        Map<String, Object> updates = new HashMap<>();
        updates.put("title", "Updated Title");

        when(supabaseService.updateBook(eq("1"), any(Map.class))).thenReturn("book updated");

        mockMvc.perform(put("/books/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isOk())
                .andExpect(content().string("book updated"));
    }

    @Test
    void testDeleteBook() throws Exception {
        when(supabaseService.deleteBook("1")).thenReturn("book deleted");

        mockMvc.perform(delete("/books/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("book deleted"));
    }

    @Test
    void testSearchBooks() throws Exception {
        when(supabaseService.searchBooks("java")).thenReturn("search result");

        mockMvc.perform(get("/books/search").param("query", "java"))
                .andExpect(status().isOk())
                .andExpect(content().string("search result"));
    }
}
