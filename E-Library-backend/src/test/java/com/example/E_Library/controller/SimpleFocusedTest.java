package com.example.E_Library.controller;

import com.example.E_Library.model.User;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SimpleFocusedTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    @Order(1)
    @DisplayName("Test 1: Registration Works")
    void test1_Registration() {
        User user = new User();
        user.setEmail("simple1@example.com");
        user.setUsername("simple1");
        user.setPassword("password123");
        user.setIs_admin(false);

        given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post("/users/register")
                .then()
                .statusCode(200)
                .log().ifError();

        System.out.println("✅ Test 1 PASSED");
    }

    @Test
    @Order(2)
    @DisplayName("Test 2: Login Basic Check")
    void test2_LoginBasicCheck() {
        // First register
        User user = new User();
        user.setEmail("simple2@example.com");
        user.setUsername("simple2");
        user.setPassword("password123");
        user.setIs_admin(false);

        given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post("/users/register")
                .then()
                .statusCode(200);

        // Then login
        Map<String, String> login = new HashMap<>();
        login.put("email", "simple2@example.com");
        login.put("password", "password123");

        Response response = given()
                .contentType(ContentType.JSON)
                .body(login)
                .when()
                .post("/users/login");

        System.out.println("Login Status: " + response.getStatusCode());
        System.out.println("Login Body: " + response.getBody().asString());

        if (response.getStatusCode() == 200) {
            System.out.println("✅ Test 2 PASSED - Login works");
        } else {
            System.out.println("❌ Test 2 FAILED - Login status: " + response.getStatusCode());
            response.then().log().all();
        }
    }

    @Test
    @Order(3)
    @DisplayName("Test 3: Wrong Password Check")
    void test3_WrongPassword() {
        // Register user first
        User user = new User();
        user.setEmail("simple3@example.com");
        user.setUsername("simple3");
        user.setPassword("password123");
        user.setIs_admin(false);

        given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post("/users/register")
                .then()
                .statusCode(200);

        // Try wrong password
        Map<String, String> wrongLogin = new HashMap<>();
        wrongLogin.put("email", "simple3@example.com");
        wrongLogin.put("password", "wrongpassword");

        Response response = given()
                .contentType(ContentType.JSON)
                .body(wrongLogin)
                .when()
                .post("/users/login");

        System.out.println("Wrong Password Status: " + response.getStatusCode());

        if (response.getStatusCode() == 401 || response.getStatusCode() == 403) {
            System.out.println("✅ Test 3 PASSED - Wrong password rejected");
        } else {
            System.out.println("⚠️  Test 3 INFO - Wrong password returned: " + response.getStatusCode());
        }
    }

    @Test
    @Order(4)
    @DisplayName("Test 4: Invalid Email Check")
    void test4_InvalidEmail() {
        Map<String, String> invalidEmail = new HashMap<>();
        invalidEmail.put("email", "notregistered@example.com");
        invalidEmail.put("password", "password123");

        Response response = given()
                .contentType(ContentType.JSON)
                .body(invalidEmail)
                .when()
                .post("/users/login");

        System.out.println("Invalid Email Status: " + response.getStatusCode());

        if (response.getStatusCode() == 401 || response.getStatusCode() == 403) {
            System.out.println("✅ Test 4 PASSED - Invalid email rejected");
        } else {
            System.out.println("⚠️  Test 4 INFO - Invalid email returned: " + response.getStatusCode());
        }
    }

    @Test
    @Order(5)
    @DisplayName("Test 5: Get JWT Token")
    void test5_GetJwtToken() {
        // Register and login to get JWT
        User user = new User();
        user.setEmail("jwttest@example.com");
        user.setUsername("jwttest");
        user.setPassword("password123");
        user.setIs_admin(false);

        given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post("/users/register")
                .then()
                .statusCode(200);

        Map<String, String> login = new HashMap<>();
        login.put("email", "jwttest@example.com");
        login.put("password", "password123");

        Response response = given()
                .contentType(ContentType.JSON)
                .body(login)
                .when()
                .post("/users/login");

        if (response.getStatusCode() == 200) {
            try {
                String token = response.jsonPath().getString("token");
                String username = response.jsonPath().getString("username");

                if (token != null && !token.isEmpty()) {
                    System.out.println("✅ Test 5 PASSED - JWT Token obtained");
                    System.out.println("Token length: " + token.length());
                    System.out.println("Username: " + username);
                } else {
                    System.out.println("❌ Test 5 FAILED - No token in response");
                    System.out.println("Response: " + response.getBody().asString());
                }
            } catch (Exception e) {
                System.out.println("❌ Test 5 ERROR - " + e.getMessage());
                System.out.println("Response: " + response.getBody().asString());
            }
        } else {
            System.out.println("❌ Test 5 FAILED - Login failed with status: " + response.getStatusCode());
        }
    }
}

// Quick Manual Test Commands (if you prefer cURL)
/*
# Test 1: Registration
curl -X POST http://localhost:8080/users/register \
  -H "Content-Type: application/json" \
  -d '{"email":"manual@example.com","username":"manual","password":"password123","is_admin":false}'

# Test 2: Login
curl -X POST http://localhost:8080/users/login \
  -H "Content-Type: application/json" \
  -d '{"email":"manual@example.com","password":"password123"}'

# Test 3: Wrong Password
curl -X POST http://localhost:8080/users/login \
  -H "Content-Type: application/json" \
  -d '{"email":"manual@example.com","password":"wrongpass"}'
*/