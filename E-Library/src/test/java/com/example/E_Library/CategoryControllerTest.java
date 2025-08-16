package com.example.E_Library;

import com.example.E_Library.controller.CategoryController;
import com.example.E_Library.model.BookCategory;
import com.example.E_Library.service.SupabaseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SupabaseService supabaseService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllCategories() throws Exception {
        String expectedResponse = "[{\"category_id\":1,\"category_name\":\"Fiction\"}]";

        Mockito.when(supabaseService.getAllCategories()).thenReturn(expectedResponse);

        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponse));
    }

    @Test
    void testAddCategory() throws Exception {
        BookCategory category = new BookCategory(1, "Science");
        String expectedResponse = "Category added successfully";

        Mockito.when(supabaseService.addCategory(any(BookCategory.class)))
                .thenReturn(expectedResponse);

        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(category)))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponse));
    }

    @Test
    void testUpdateCategory() throws Exception {
        String categoryId = "1";
        Map<String, Object> updates = new HashMap<>();
        updates.put("category_name", "Updated Science");

        String expectedResponse = "Category updated successfully";

        Mockito.when(supabaseService.updateCategory(eq(categoryId), any(Map.class)))
                .thenReturn(expectedResponse);

        mockMvc.perform(put("/categories/update-category/{id}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponse));
    }

    @Test
    void testDeleteCategory() throws Exception {
        Integer categoryId = 1;
        String expectedResponse = "Category deleted successfully";

        Mockito.when(supabaseService.deleteCategory(categoryId.toString()))
                .thenReturn(expectedResponse);

        mockMvc.perform(delete("/categories/{category_id}", categoryId))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponse));
    }
}
