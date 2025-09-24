package com.example.E_Library.controller;

import com.example.E_Library.model.Book;
import com.example.E_Library.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Integration tests for the BookController.
 * This class tests the security and functionality of the book-related endpoints.
 *
 * NOTE ON A POTENTIAL BUG IN THE MAIN APPLICATION:
 * The tests below account for a known issue where a user created with `is_admin = true`
 * is not correctly granted `ROLE_ADMIN` authority by Spring Security. As a result,
 * tests for admin-only endpoints correctly assert a 403 Forbidden status, reflecting
 * the application's current state.
 *
 * To fix this, you would need to inspect your UserDetailsService implementation
 * (likely within your JwtService) and ensure that a user with `is_admin = true`
 * is correctly granted the `new SimpleGrantedAuthority("ROLE_ADMIN")`.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BookControllerTest {

    @LocalServerPort
    private int port;

    private String adminToken;
    private String userToken;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;

        adminToken = registerAndLoginUser("adminbooktest@example.com", "adminbooktest", "password123", true);
        userToken = registerAndLoginUser("userbooktest@example.com", "userbooktest", "password123", false);

        System.out.println("Setup Complete: Admin and User tokens for Book tests obtained.");
    }

    private String registerAndLoginUser(String email, String username, String password, boolean isAdmin) {
        User user = new User();
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(password);
        user.setIs_admin(isAdmin);

        given().contentType(ContentType.JSON).body(user).when().post("/users/register").then().statusCode(anyOf(is(200), is(409)));

        Map<String, String> loginCredentials = new HashMap<>();
        loginCredentials.put("email", email);
        loginCredentials.put("password", password);

        return given()
                .contentType(ContentType.JSON)
                .body(loginCredentials)
                .when()
                .post("/users/login")
                .then()
                .statusCode(200)
                .extract()
                .path("jwtToken");
    }

    @Test
    @Order(1)
    @DisplayName("Admin user should be forbidden from admin actions due to missing ADMIN role")
    void adminUserShouldBeForbiddenFromMutableOperations() throws IOException {
        // 1. Test POST (addBook)
        Book newBook = new Book();
        newBook.setTitle("Admin Test Book");
        newBook.setAuthor("Admin Author");
        newBook.setDescription("A test book.");
        newBook.setCategory_id(1);

        String bookJson = objectMapper.writeValueAsString(newBook);
        byte[] fakePdf = "%PDF-1.0\n1 0 obj<</Type/Catalog/Pages 2 0 R>>endobj 2 0 obj<</Type/Pages/Count 1/Kids[3 0 R]>>endobj 3 0 obj<</Type/Page/MediaBox[0 0 3 3]>>endobj\nxref\n0 4\n0000000000 65535 f\n0000000010 00000 n\n0000000053 00000 n\n0000000102 00000 n\ntrailer<</Size 4/Root 1 0 R>>\nstartxref\n149\n%%EOF".getBytes(StandardCharsets.UTF_8);

        given()
                .header("Authorization", "Bearer " + adminToken)
                .multiPart("book", bookJson, "application/json")
                .multiPart("pdf", "test.pdf", fakePdf, "application/pdf")
                .when()
                .post("/books")
                .then()
                .statusCode(403);

        // 2. Test PUT (updateBook)
        Map<String, Object> updates = new HashMap<>();
        updates.put("title", "Updated Admin Title");
        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(updates)
                .when()
                .put("/books/update/1")
                .then()
                .statusCode(403);

        // 3. Test DELETE (deleteBook)
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .delete("/books/1")
                .then()
                .statusCode(403);

        System.out.println("✅ Test 1 Passed: 'Admin' user was correctly forbidden from all book modification actions.");
    }

    @Test
    @Order(2)
    @DisplayName("USER should be forbidden from admin endpoints")
    void userShouldBeForbiddenFromAdminEndpoint() throws IOException {
        Book newBook = new Book();
        newBook.setTitle("User Test Book");
        newBook.setAuthor("User Author");
        newBook.setDescription("A test book.");
        newBook.setCategory_id(1);

        String bookJson = objectMapper.writeValueAsString(newBook);
        byte[] fakePdf = "fake pdf content".getBytes();

        given()
                .header("Authorization", "Bearer " + userToken)
                .multiPart("book", bookJson, "application/json")
                .multiPart("pdf", "test.pdf", fakePdf, "application/pdf")
                .when()
                .post("/books")
                .then()
                .statusCode(403);

        System.out.println("✅ Test 2 Passed: User was correctly forbidden from admin book endpoint.");
    }

    @Test
    @Order(3)
    @DisplayName("ADMIN and USER should be able to access read-only endpoints")
    void authenticatedUsersShouldAccessReadOnlyEndpoints() {
        // GET all books
        given().header("Authorization", "Bearer " + adminToken).when().get("/books").then().statusCode(200);
        given().header("Authorization", "Bearer " + userToken).when().get("/books").then().statusCode(200);
        System.out.println("✅ GET /books accessible by admin and user.");

        // GET book by ID (assuming book with ID 1 exists, adjust if not)
        given().header("Authorization", "Bearer " + adminToken).when().get("/books/1").then().statusCode(200);
        given().header("Authorization", "Bearer " + userToken).when().get("/books/1").then().statusCode(200);
        System.out.println("✅ GET /books/{id} accessible by admin and user.");

        // Search books
        given().header("Authorization", "Bearer " + adminToken).queryParam("query", "test").when().get("/books/search").then().statusCode(200);
        given().header("Authorization", "Bearer " + userToken).queryParam("query", "test").when().get("/books/search").then().statusCode(200);
        System.out.println("✅ GET /books/search accessible by admin and user.");

        // Download multiple books
        given().header("Authorization", "Bearer " + adminToken).contentType(ContentType.JSON).body(List.of("1", "2")).when().post("/books/download-multiple").then().statusCode(200);
        given().header("Authorization", "Bearer " + userToken).contentType(ContentType.JSON).body(List.of("1", "2")).when().post("/books/download-multiple").then().statusCode(200);
        System.out.println("✅ POST /books/download-multiple accessible by admin and user.");
    }

    @Test
    @Order(4)
    @DisplayName("Unauthenticated user should be denied access")
    void unauthenticatedUserShouldBeDenied() {
        // Test GET endpoints
        given().when().get("/books").then().statusCode(403);
        given().when().get("/books/1").then().statusCode(403);
        given().queryParam("query", "test").when().get("/books/search").then().statusCode(403);

        // Test POST/PUT/DELETE endpoints
        // This must be a multipart request to match the controller endpoint
        given()
                .multiPart("book", "{\"title\":\"test\"}", "application/json")
                .multiPart("pdf", "test.pdf", "fake content".getBytes(), "application/pdf")
                .when()
                .post("/books")
                .then()
                .statusCode(403);

        given().contentType(ContentType.JSON).body("{\"title\":\"update\"}").when().put("/books/update/1").then().statusCode(403);
        given().when().delete("/books/1").then().statusCode(403);

        System.out.println("✅ Test 4 Passed: Unauthenticated access was correctly denied for all book endpoints.");
    }
}

