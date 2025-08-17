package com.example.E_Library.controller;

import com.example.E_Library.model.BookCategory;
import com.example.E_Library.service.SupabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;

// Import the SLF4J Logger classes
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@RestController
@RequestMapping("/categories")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class CategoryController {

    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);

    @Autowired
    private SupabaseService supabaseService;

    @GetMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public String getAllCategories() {

        logger.info("Request received to get all categories.");
        return supabaseService.getAllCategories();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String addCategory(@RequestBody BookCategory category) {

        logger.info("Request received to add a new category with name: '{}'", category.getCategory_name());
        return supabaseService.addCategory(category);
    }

    @PutMapping("/update-category/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateCategory(@PathVariable String id, @RequestBody Map<String, Object> updates) {
        logger.info("Request received to update category with ID: {}", id);
        return supabaseService.updateCategory(id, updates);
    }

    @DeleteMapping("/{category_id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteCategory(@PathVariable Integer category_id) {
        logger.info("Request received to delete category with ID: {}", category_id);
        return supabaseService.deleteCategory(category_id.toString());
    }
}
