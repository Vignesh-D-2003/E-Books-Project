package com.example.E_Library.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
	
	private Integer user_id;
    private String username;
    private String email;
    private String password;
    private String created_at;
    private String updated_at;
    private Boolean is_admin;
}



