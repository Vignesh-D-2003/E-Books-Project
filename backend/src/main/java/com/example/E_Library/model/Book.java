package com.example.E_Library.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    private Integer book_id;
    private String title;
    private String author;
    private String description;
    private String file_url;
    private Integer uploaded_by;
    private String uploaded_at;
    private Integer category_id;
}
