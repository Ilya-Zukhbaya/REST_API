package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.BaseCase;
import lib.api.ApiBaseRequests;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.safari.SafariDriver;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Epic("HomeWork Two Epic")
@Feature("HomeWork Two feature")
@Story("HomeWork Two Story")
public class HomeTwo extends BaseCase {

    //region Context
    private static final ApiBaseRequests apiBaseRequests = new ApiBaseRequests();
    static WebDriver driver = new SafariDriver();
    private String authCookie;
    //endregion

    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "https://playground.learnqa.ru";
        System.setProperty("webdriver.safari.driver","/usr/bin/safaridriver");
    }

    //region Tests
    @Test
    @Tag("API")
    @DisplayName("Проверка JSON ответа")
    @Description("Проверяем ответ на GET запрос JSON")
    public void checkJson() {
        List<Object> array = apiBaseRequests.getJsonHomeWork()
                .getBody()
                .jsonPath()
                .getList("messages")
                .stream()
                .filter(x -> x.toString().contains("second message"))
                .toList();

        System.out.println(Arrays.stream(array.get(0).toString().replace("{", "").split(", ")).toList().get(0));
    }

    @Test
    @Tag("API")
    @DisplayName("Проверка хоста")
    @Description("Проверяем ответ с редиректа хоста на GET запрос")
    public void checkLongDirect() {
        System.out.println("Redirected host: " + apiBaseRequests.getLongRedirect(true).getHeader("X-Host"));
    }

    @Test
    @Tag("API")
    @DisplayName("Проверка количества редиректов")
    @Description("Проверяем количество редиректов на GET запрос")
    public void checkRedirects() {
        int status_code = 0;
        int counter = 0;
        String host = "https://playground.learnqa.ru/api/long_redirect";

        while (status_code != 200) {
            Response response = apiBaseRequests.getGivenLastRedirect(host);
            status_code = response.getStatusCode();
            counter = counter + 1;
            host = response.getHeader("location");

            if (host == null) {
                host = response.getHeader("X-Host");
            }
        }

        System.out.println("Всего редиректов: " + counter);
    }

    @Test
    @Tag("API")
    @DisplayName("Проверка создания JOB")
    @Description("Проверяем создаение нужного JOB на GET запрос")
    public void checkTask() {
        String received_token = apiBaseRequests
                .createJob()
                .getBody()
                .jsonPath()
                .getJsonObject("token");

        String received_status = getResult(received_token);

        while (received_status.equals("Job is NOT ready")) {
            received_status = getResult(received_token);
        }

        Map<String, String> params = new HashMap<>();
        params.put("token", received_token);
        String received_result = apiBaseRequests
                .getJobWithParam(params)
                .getBody()
                .jsonPath()
                .get("result");

        Assertions.assertNotNull(received_result);
        System.out.println("Полученный результат: " + received_result);
    }

    @Test
    @Tags({@Tag("UI"), @Tag("API")})
    @DisplayName("Поиск правильного пароля")
    @Description("Производим поиск потерянного пароля")
    public void checkPass() {
        driver.get("https://en.wikipedia.org/wiki/List_of_the_most_common_passwords");

        List<WebElement> passwords = driver.findElements(By.xpath(".//td[contains(@align, 'left')]"));

        Map<String, String> authMap = new HashMap<>();
        authMap.put("login", "super_admin");
        authMap.put("password", "password");

        for (WebElement password : passwords) {
            authMap.replace("password", password.getText().replace("\n", ""));
            authCookie = apiBaseRequests.getSecretPass(authMap).cookie("auth_cookie");

            if (!apiBaseRequests.checkAuthCookie(authCookie).print().equals("You are NOT authorized")) {
                break;
            }
        }

        System.out.println("Правильный пароль: " + authMap.get("password"));

        driver.quit();
    }
    //endregion

    //region Methods
    private String getResult(String token) {
        Map<String, String> params = new HashMap<>();
        params.put("token", token);

        return apiBaseRequests
                .getJobWithParam(params)
                .getBody()
                .jsonPath()
                .get("status");
    }
    //endregion
}
