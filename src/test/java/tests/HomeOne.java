package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.BaseCase;
import lib.HardAssertions;
import lib.api.ApiBaseRequests;
import org.junit.jupiter.api.*;

import java.util.*;

@Epic("HomeWork Epic")
@Feature("HomeWork feature")
@Story("HomeWork Story")
public class HomeOne extends BaseCase {

    //region Context
    private static String user;
    private static String pass;
    private static String login;
    private static String password;
    private static Map<String, Object> bodyMap;
    private static Map<String, String> queryMap;
    private String authCookie;
    private static final ApiBaseRequests apiBaseRequests = new ApiBaseRequests();
    //endregion

    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "https://playground.learnqa.ru";

        user = "Test";
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

    //region Tests
    @Test
    @Tag("API")
    @DisplayName("Проверка API")
    @Description("Проверяет получение ответа на GET запрос")
    public void HelloApi() {
        Assertions.assertEquals("Hello, " + user, apiBaseRequests.getHello(user).jsonPath().get("answer"));
    }

    @Test
    @Tag("API")
    @DisplayName("Проверка возвращаемого текста")
    @Description("Проверяет получение текста на GET запрос")
    public void ReturnText() {
        apiBaseRequests.getText(pass).print();
    }

    @Test
    @Tag("API")
    @DisplayName("Проверка типа кода ответа")
    @Description("Проверяет тип кода ответа на POST запрос")
    public void checkResponse() {
        System.out.println("Status code: " + apiBaseRequests.getType(bodyMap).getStatusCode());
    }

    @Test
    @Tag("API")
    @DisplayName("Проверка негативного редиректа")
    @Description("Проверяет API на статус 303 и редирект")
    public void checkRedirect() {
        System.out.println("\nLocation redirect: " + apiBaseRequests.getRedirect(false).getHeader("location"));
    }

    @Test
    @Tag("API")
    @DisplayName("Проверка всех заголовков")
    @Description("Проверяет все заголовки в ответ на GET запрос")
    public void checkAllHeaders() {
        apiBaseRequests.getAllHeaders().prettyPrint();
    }

    @Test
    @Tag("API")
    @DisplayName("Проверка на получение авторизационной куки")
    @Description("Проверяет что пользователь получает авторизационую куки в ответ на POST запрос")
    public void checkAuthorization() {
        Response response = apiBaseRequests.getAuthCookie(queryMap);
        authCookie = response.cookie("auth_cookie");

        System.out.println("\nDetailed Cookies: " + response.getDetailedCookies());
        System.out.println("\nCookies: " + response.getCookies());
        System.out.println("\nHeaders: " + response.getHeaders());
        System.out.println("\nBody: " + response.getBody());
    }

    @Test
    @Tag("API")
    @Description("Проверяет на корректность авторизацонную куки")
    @DisplayName("Проверка авторизационной куки")
    public void checkAuthCookie() {
        checkAuthorization();
        HardAssertions.assertText(apiBaseRequests.checkAuthCookie(authCookie), "You are authorized");
    }
    //endregion
}