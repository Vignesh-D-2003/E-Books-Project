package com.example.E_Library.controller;

import com.example.E_Library.model.Book;
import com.example.E_Library.service.SupabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/books")
public class BookController {

    @Autowired
    private SupabaseService supabaseService;

    @GetMapping
    public String getAllBooks() {
        return supabaseService.getAllBooks();
    }

    @GetMapping("/{book_id}")
    public String getBookById(@PathVariable Integer book_id) {
        return supabaseService.getBookById(book_id.toString());
    }

    @PostMapping
    public String addBook(@RequestBody Book book) {
        return supabaseService.addBook(book);
    }

    @PatchMapping("/{book_id}")
    public String updateBook(@PathVariable("book_id") String bookId,
            @RequestBody Map<String, Object> updates) {
        return supabaseService.updateBook(bookId, updates);
    }

    @DeleteMapping("/{book_id}")
    public String deleteBook(@PathVariable Integer book_id) {
        return supabaseService.deleteBook(book_id.toString());
    }

    @GetMapping("/search")
    public String searchBooks(@RequestParam("query") String query) {
        return supabaseService.searchBooks(query);
    }

}
