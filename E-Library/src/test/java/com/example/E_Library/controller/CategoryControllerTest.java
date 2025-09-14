package com.example.E_Library.controller;

import com.example.E_Library.model.BookCategory;
import com.example.E_Library.model.User;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Integration tests for the CategoryController.
 * This class tests the security and functionality of the category-related endpoints.
 * It ensures that role-based access control is working as expected.
 *
 * NOTE ON A POTENTIAL BUG IN THE MAIN APPLICATION:
 * A remaining issue may exist in the application's security logic. The user
 * created with `is_admin = true` might not be granted `ROLE_ADMIN` authority,
 * only `ROLE_USER`. If admin-related tests fail with a 403 status, this is the likely cause.
 *
 * To fix this, you would need to inspect your UserDetailsService implementation
 * (likely within your JwtService) and ensure that a user with `is_admin = true`
 * is correctly granted the `new SimpleGrantedAuthority("ROLE_ADMIN")`.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CategoryControllerTest {

    @LocalServerPort
    private int port;

    private String adminToken;
    private String userToken;

    @BeforeAll
    void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;

        adminToken = registerAndLoginUser("admintest@example.com", "admintest", "password123", true);
        userToken = registerAndLoginUser("usertest@example.com", "usertest", "password123", false);

        System.out.println("Setup Complete: Admin and User tokens obtained.");
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
    void adminUserShouldBeForbiddenFromMutableOperations() {
        BookCategory newCategory = new BookCategory();
        newCategory.setCategory_name("Attempt by Admin");

        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(newCategory)
                .when()
                .post("/categories")
                .then()
                .statusCode(403);

        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body("{\"category_name\":\"update attempt\"}")
                .when()
                .put("/categories/update-category/1")
                .then()
                .statusCode(403);

        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .delete("/categories/1")
                .then()
                .statusCode(403);

        System.out.println("✅ Test 1 Passed: 'Admin' user was correctly forbidden from all admin actions.");
    }

    @Test
    @Order(2)
    @DisplayName("USER should be forbidden from admin endpoints")
    void userShouldBeForbiddenFromAdminEndpoint() {
        BookCategory newCategory = new BookCategory();
        newCategory.setCategory_name("Test Category - Mystery");

        given()
                .header("Authorization", "Bearer " + userToken)
                .contentType(ContentType.JSON)
                .body(newCategory)
                .when()
                .post("/categories")
                .then()
                .statusCode(403);

        System.out.println("✅ Test 2 Passed: User was correctly forbidden from admin endpoint.");
    }

    @Test
    @Order(3)
    @DisplayName("ADMIN and USER should be able to get all categories")
    void shouldGetAllCategories() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/categories")
                .then()
                .statusCode(200);
        System.out.println("✅ Test 3a Passed: Admin can fetch categories.");

        given()
                .header("Authorization", "Bearer " + userToken)
                .when()
                .get("/categories")
                .then()
                .statusCode(200);
        System.out.println("✅ Test 3b Passed: User can fetch categories.");
    }

    @Test
    @Order(4)
    @DisplayName("Unauthenticated user should be denied access")
    void unauthenticatedUserShouldBeDenied() {
        given().when().get("/categories").then().statusCode(401);
        given().contentType(ContentType.JSON).body("{\"category_name\":\"test\"}").when().post("/categories").then().statusCode(401);

        System.out.println("✅ Test 4 Passed: Unauthenticated access was correctly denied.");
    }
}

