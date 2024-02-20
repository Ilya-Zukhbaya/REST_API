package lib.api;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class ApiBaseRequests {

    @Step("GET REQUEST to '/api/hello'")
    public Response getHello(String user) {
        return given()
                .filter(new AllureRestAssured())
                .queryParam("name", user)
                .when()
                .get("/api/hello")
                .then()
                .statusCode(200)
                .extract()
                .response();
    }

    @Step("GET REQUEST TO /api/get_text")
    public Response getText(String pass) {
        return given()
                .filter(new AllureRestAssured())
                .queryParam("pass", pass)
                .when()
                .get("/api/get_text")
                .then()
                .statusCode(200)
                .extract()
                .response();
    }

    @Step("POST REQUEST TO /api/check_type")
    public Response getType(Map<String, Object> bodyMap) {
        return given()
                .filter(new AllureRestAssured())
                .body(bodyMap)
                .when()
                .post("/api/check_type")
                .then()
                .statusCode(200)
                .extract()
                .response();
    }

    @Step("GET REQUEST TO /api/get_303")
    public Response getRedirect(Boolean redirect) {
        return given()
                .filter(new AllureRestAssured())
                .redirects()
                .follow(redirect)
                .when()
                .get("/api/get_303")
                .andReturn();
    }

    @Step("GET REQUEST TO /api/get_all_headers")
    public Response getAllHeaders() {
        return given()
                .filter(new AllureRestAssured())
                .when()
                .get("/api/show_all_headers")
                .then()
                .statusCode(200)
                .extract()
                .response();
    }

    @Step("POST REQUEST TO /api/get_auth_cookie")
    public Response getAuthCookie(Map<String, String> queryMap) {
        return given()
                .filter(new AllureRestAssured())
                .body(queryMap)
                .post("/api/get_auth_cookie")
                .then()
                .statusCode(200)
                .extract()
                .response();
    }

    @Step("GET REQUEST TO /api/check_auth_cookie")
    public Response checkAuthCookie(String authCookie) {
        return given()
                .filter(new AllureRestAssured())
                .cookie("auth_cookie", authCookie)
                .when()
                .get("/api/check_auth_cookie")
                .then()
                .statusCode(200)
                .extract()
                .response();
    }

    @Step("GET REQUEST TO /api/get_json_homework")
    public Response getJsonHomeWork() {
        return RestAssured
                .when()
                .get("/api/get_json_homework")
                .then()
                .statusCode(200)
                .extract()
                .response();
    }

    @Step("GET REQUEST TO /api/long_redirect")
    public Response getLongRedirect(Boolean follow) {
        return RestAssured
                .given()
                .redirects()
                .follow(follow)
                .when()
                .get("/api/long_redirect")
                .then()
                .statusCode(200)
                .extract()
                .response();
    }

    @Step("GET REQUEST TO LAST GIVEN REDIRECT")
    public Response getGivenLastRedirect(String url) {
        return RestAssured
                .given()
                .redirects()
                .follow(false)
                .get(url)
                .then()
                .extract()
                .response();
    }

    @Step("GET REQUEST TO /ajax/api/longtime_job")
    public Response createJob() {
        return RestAssured
                .when()
                .get("/ajax/api/longtime_job")
                .then()
                .statusCode(200)
                .extract()
                .response();
    }

    @Step("GET REQUEST WITH PARAM TO /ajax/api/longtime_job")
    public Response getJobWithParam(Map<String, String> params) {
        return RestAssured
                .given()
                .queryParams(params)
                .when()
                .get("/ajax/api/longtime_job")
                .then()
                .statusCode(200)
                .extract()
                .response();
    }

    @Step("POST REQUEST TO /ajax/api/get_secret_password_homework")
    public Response getSecretPass(Map<String, String> params) {
        return RestAssured
                .given()
                .queryParams(params)
                .when()
                .post("/ajax/api/get_secret_password_homework")
                .then()
                .statusCode(200)
                .extract()
                .response();
    }

    @Step("POST REQUEST TO /api/user/login")
    public Response postUserWithParams(Map<String, String> params) {
        return RestAssured
                .given()
                .queryParams(params)
                .when()
                .post("/api/user/login")
                .then()
                .extract()
                .response();
    }

    @Step("POST REQUEST TO /api/user/login")
    public Response postUserLoginWithBody(Map<String, String> body) {
        return RestAssured
                .given()
                .body(body)
                .when()
                .post("/api/user/login")
                .then()
                .statusCode(200)
                .extract()
                .response();
    }

    @Step("GET REQUEST TO /api/user/auth")
    public Response getUserIdWithParams(Map<String, String> headers, Map<String, String> cookies) {
        return RestAssured
                .given()
                .headers(headers)
                .cookies(cookies)
                .when()
                .get("/api/user/auth")
                .then()
                .statusCode(200)
                .extract()
                .response();
    }

    @Step("GET REQUEST /api/homework_cookie")
    public Response getHomeworkCookie() {
        return RestAssured
                .when()
                .get("/api/homework_cookie")
                .then()
                .statusCode(200)
                .extract()
                .response();
    }

    @Step("GET REQUEST /api/homework_header")
    public Response getHomeworkHeader() {
        return RestAssured
                .when()
                .get("/api/homework_header")
                .then()
                .statusCode(200)
                .extract()
                .response();
    }

    @Step("GET REQUEST /ajax/api/user_agent_check")
    public Response getUserAgentCheck(Map<String, String> headers) {
        return RestAssured
                .given()
                .headers(headers)
                .get("/ajax/api/user_agent_check")
                .then()
                .statusCode(200)
                .extract()
                .response();
    }

    @Step("POST REQUEST TO /api/user/")
    public Response postUserWithBody(Map<String, String> body) {
        return RestAssured
                .given()
                .body(body)
                .when()
                .post("/api/user/")
                .then()
                .extract()
                .response();
    }

    @Step("GET REQUEST TO /api/user/")
    public Response getUserId(int param) {
        return RestAssured
                .given()
                .pathParams("id", param)
                .when()
                .get("/api/user/{id}")
                .then()
                .extract()
                .response();
    }

    @Step("POST REQUEST TO /api/user/{id}")
    public Response getUser(String userId, String cookie, String header) {
        return RestAssured
                .given()
                .pathParam("id", userId)
                .cookie("auth_sid", cookie)
                .header("x-csrf-token", header)
                .when()
                .get("/api/user/{id}")
                .then()
                .extract()
                .response();
    }

    @Step("UPDATE REQUEST TO /api/user/{id}")
    public void updateUser(String userId, String cookie, String header, Map<String, String> body) {
        RestAssured
                .given()
                .pathParam("id", userId)
                .cookie("auth_sid", cookie)
                .header("x-csrf-token", header)
                .body(body)
                .when()
                .put("/api/user/{id}");
    }

    @Step("UPDATE REQUEST TO /api/user/{id}")
    public Response updateUserAndGetResponse(String userId, String cookie, String header, Map<String, String> body) {
        return RestAssured
                .given()
                .pathParam("id", userId)
                .cookie("auth_sid", cookie)
                .header("x-csrf-token", header)
                .body(body)
                .when()
                .put("/api/user/{id}")
                .then()
                .extract()
                .response();
    }

    @Step("DELETE REQUEST TO /api/user/{id}")
    public void deleteUser(String userId, String cookie, String header) {
        RestAssured
                .given()
                .pathParam("id", userId)
                .cookie("auth_sid", cookie)
                .header("x-csrf-token", header)
                .when()
                .delete("/api/user/{id}")
                .then()
                .statusCode(200);
    }

    @Step("DELETE REQUEST TO /api/user/{id}")
    public Response deleteUserAndGetResponse(String userId, String cookie, String header) {
        return RestAssured
                .given()
                .pathParam("id", userId)
                .cookie("auth_sid", cookie)
                .header("x-csrf-token", header)
                .when()
                .delete("/api/user/{id}")
                .then()
                .extract()
                .response();
    }
}
