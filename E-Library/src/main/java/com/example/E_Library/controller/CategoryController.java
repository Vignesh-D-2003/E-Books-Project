package com.example.E_Library.controller;

import com.example.E_Library.model.BookCategory;
import com.example.E_Library.service.SupabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private SupabaseService supabaseService;

    @GetMapping
    public String getAllCategories() {
        return supabaseService.getAllCategories();
    }

    @PostMapping
    public String addCategory(@RequestBody BookCategory category) {
        return supabaseService.addCategory(category);
    }

    @PutMapping("/update-category/{id}")
    public String updateCategory(@PathVariable String id, @RequestBody Map<String, Object> updates) {
        return supabaseService.updateCategory(id, updates);
    }

    @DeleteMapping("/{category_id}")
    public String deleteCategory(@PathVariable Integer category_id) {
        return supabaseService.deleteCategory(category_id.toString());
    }
}
