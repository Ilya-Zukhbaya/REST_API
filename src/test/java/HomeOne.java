import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.*;

public class HomeOne {

    private static String user;

    private static String pass;

    private static String login;

    private static String password;

    private static Map<String, Object> bodyMap;

    private static Map<String, String> queryMap;

    private String authCookie;

    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "https://playground.learnqa.ru";

        user = "Ilya";
        pass = "Wall";
        login = "secret_login";
        password = "secret_pass";

        bodyMap = new HashMap<>();
        bodyMap.put("name", user);
        bodyMap.put("pass", pass);

        queryMap = new HashMap<>();
        queryMap.put("login", login);
        queryMap.put("password", password);
    }

    @Test
    public void HelloApi() {
        JsonPath response = RestAssured
                .given()
                .queryParam("name", user)
                .when()
                .get("/api/hello")
                .then()
                .statusCode(200)
                .extract()
                .response()
                .jsonPath();

        String path = response.get("answer");

        Assertions.assertEquals("Hello, " + user, path);
    }

    @Test
    public void ReturnText() {
        RestAssured
                .given()
                .queryParam("pass", pass)
                .when()
                .get("/api/get_text")
                .then()
                .statusCode(200)
                .extract()
                .response()
                .print();
    }

    @Test
    public void checkResponse() {
        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(bodyMap)
                .when()
                .post("/api/check_type")
                .then()
                .statusCode(200)
                .extract()
                .response();

        response.print();
        System.out.println("Status code: " + response.getStatusCode());
    }

    @Test
    public void checkRedirect() {
        Response response = RestAssured
                .given()
                .redirects()
                .follow(false)
                .when()
                .get("/api/get_303")
                .andReturn();

       System.out.println(response.getHeaders());
       System.out.println("\nLocation redirect: " + response.getHeader("location"));
     }

    @Test
    public void checkAllHeaders() {
        Response response = RestAssured
                .when()
                .get("/api/show_all_headers")
                .then()
                .statusCode(200)
                .extract()
                .response();

        response.prettyPrint();
    }
    @Test
    public void checkAuthorization() {
        Response response = RestAssured
                .given()
                .body(queryMap)
                .post("/api/get_auth_cookie")
                .then()
                .statusCode(200)
                .extract()
                .response();

        System.out.println("\nDetailed Cookies: " + response.getDetailedCookies());
        System.out.println("\nCookies: " + response.getCookies());
        System.out.println("\nHeaders: " + response.getHeaders());
        System.out.println("\nBody: " + response.getBody());

        authCookie = response.cookie("auth_cookie");
    }

    @Test
    public void checkAuthCookie() {
        checkAuthorization();

        Response response = RestAssured
                .given()
                .cookie("auth_cookie", authCookie)
                .when()
                .get("/api/check_auth_cookie")
                .then()
                .statusCode(200)
                .extract()
                .response();

        Assertions.assertEquals("You are authorized", response.print());
    }
}