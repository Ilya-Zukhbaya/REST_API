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
import lib.utils.DGRandomString;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.Stream;

@Epic("HomeWork Three Epic")
@Feature("HomeWork Three Feature")
@Story("HomeWork Three Story")
public class HomeThree extends BaseCase {

    //region Context
    private static final ApiBaseRequests apiBaseRequests = new ApiBaseRequests();
    private static String email;
    private static String password;
    static Stream<Arguments> getUserMethod() {
        return Stream.of(
                Arguments.of("Mozilla/5.0 (Linux; U; Android 4.0.2; en-us; Galaxy Nexus Build/ICL53F) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30", "Mobile", "Chrome", "iOS"),
                Arguments.of("Mozilla/5.0 (iPad; CPU OS 13_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/91.0.4472.77 Mobile/15E148 Safari/604.1", "Mobile", "Chrome", "iOS"),
                Arguments.of("Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)","Googlebot", "Unknown", "Unknown"),
                Arguments.of("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36 Edg/91.0.100.0", "Web", "Chrome", "No"),
                Arguments.of("Mozilla/5.0 (iPad; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1", "Mobile", "No", "iPhone")
        );
    }
    //endregion

    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "https://playground.learnqa.ru";

        email = "vinkotov@example.com";
        password = "1234";
    }

    //region Tests
    @ParameterizedTest(name = "Test name: {0}")
    @ValueSource(strings = {"Ilya", "John"})
    @DisplayName("Проверка специфичного имени")
    @Description("Проверяем полученное имя на GET запрос")
    @Tag("API")
    public void checkName(String name) {
        Response response = apiBaseRequests.getHello(name);
        HardAssertions.assertByName(response, "answer", "Hello, " + name);
    }

    @Test
    @DisplayName("Проверка авторизации клиента")
    @Description("Проверяем авторизацию клиента и сверяем полученный user_id")
    @Tag("API")
    public void checkClientAuth() {
        Map<String, String> authMap = new HashMap<>();
        Map<String, String> authHeaderTokens = new HashMap<>();
        Map<String, String> authCookiesTokens = new HashMap<>();

        authMap.put("email", email);
        authMap.put("password", password);

        Response response = apiBaseRequests.postUserWithParams(authMap);

        authHeaderTokens.put("x-csrf-token", getHeader(response, "x-csrf-token"));
        authCookiesTokens.put("auth_sid", getCookie(response, "auth_sid"));
        String givenUserId = getValue(response, "user_id").toString();

        Response userId = apiBaseRequests.getUserIdWithParams(authHeaderTokens, authCookiesTokens);

        HardAssertions.assertByName(userId, "user_id", Integer.valueOf(givenUserId));
    }

    @Test
    @DisplayName("Проверка создания случайной длины строки")
    @Description("Проверяем создание случайной строки")
    @Tag("NONE")
    public void checkText() {
        int val = new Random().nextInt(100);
        DGRandomString dgRandomString = new DGRandomString();
        Assertions.assertEquals(dgRandomString.generateRandomString(val).length(), val, "Длина строки не совпадает с нужной");
    }

    @Test
    @DisplayName("Проверка полученной cookie")
    @Description("Проверяем получение cookie в ответ на GET запрос")
    @Tag("API")
    public void checkCookie() {
        HardAssertions.assertCookie(apiBaseRequests.getHomeworkCookie(), "Homework", "hw_value");
    }

    @Test
    @DisplayName("Проверка полученного header")
    @Description("Проверяем получение header в ответ на GET запрос")
    @Tag("API")
    public void checkHeader() {
        HardAssertions.assertHeader(apiBaseRequests.getHomeworkHeader(), "x-secret-homework-header", "Some secret value");
    }

    @ParameterizedTest(name = "User-Agent: {0}")
    @MethodSource("getUserMethod")
    @DisplayName("Проверка платформы пользователя")
    @Description("Проверяем получение платформы пользователя в ответ на GET запрос")
    @Tag("API")
    public void checkUserAgent(String userAgent, String expectedPlatform, String expectedBrowser, String expectedDevice) {
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", userAgent);

        Response response = apiBaseRequests.getUserAgentCheck(headers);

        HardAssertions.assertByName(response, "platform", expectedPlatform);
        HardAssertions.assertByName(response, "browser", expectedBrowser);
        HardAssertions.assertByName(response, "device", expectedDevice);
    }
    //endregion
}
