import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.safari.SafariDriver;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeTwo {

    static WebDriver driver = new SafariDriver();

    private static String login;

    private String authCookie;

    @BeforeAll
    public static void setUp() {
        System.setProperty("webdriver.safari.driver","/usr/bin/safaridriver");

        login = "super_admin";
    }

    @Test
    public void checkJson() {
        Response response = RestAssured
                .when()
                .get("https://playground.learnqa.ru/api/get_json_homework")
                .then()
                .statusCode(200)
                .extract()
                .response();

        List<Object> array = response.getBody()
                .jsonPath()
                .getList("messages")
                .stream()
                .filter(x -> x.toString().contains("second message"))
                .toList();

        System.out.println(Arrays.stream(array.get(0).toString().replace("{", "").split(", ")).toList().get(0));
    }

    @Test
    public void checkLongDirect() {
        Response response = RestAssured
                .given()
                .redirects()
                .follow(true)
                .when()
                .get("https://playground.learnqa.ru/api/long_redirect")
                .then()
                .statusCode(200)
                .extract()
                .response();

        System.out.println("Redirected host: " + response.getHeader("X-Host"));
    }

    @Test
    public void checkRedirects() {
        int status_code = 0;
        int counter = 0;
        String host = "https://playground.learnqa.ru/api/long_redirect";

        while (status_code != 200) {
            Response response = RestAssured
                    .given()
                    .redirects()
                    .follow(false)
                    .get(host)
                    .then()
                    .extract()
                    .response();

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
    public void checkTask() {
        String received_token = RestAssured
                .when()
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .then()
                .statusCode(200)
                .extract()
                .response()
                .getBody()
                .jsonPath()
                .getJsonObject("token");

        String received_status = getResult(received_token);

        while (received_status.equals("Job is NOT ready")) {
            received_status = getResult(received_token);
        }

        String received_result = RestAssured
                .given()
                .queryParam("token", received_token)
                .when()
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .then()
                .statusCode(200)
                .extract()
                .response()
                .getBody()
                .jsonPath()
                .get("result");

        Assertions.assertNotNull(received_result);
        System.out.println("Полученный результат: " + received_result);
    }

    private String getResult(String token) {
        return RestAssured
                .given()
                .queryParam("token", token)
                .when()
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .then()
                .statusCode(200)
                .extract()
                .response()
                .getBody()
                .jsonPath()
                .get("status");
    }

    @Test
    public void checkPass() {
        driver.get("https://en.wikipedia.org/wiki/List_of_the_most_common_passwords");

        List<WebElement> passwords = driver.findElements(By.xpath(".//td[contains(@align, 'left')]"));
        Map<String, String> authMap = new HashMap<>();
        authMap.put("login", login);
        authMap.put("password", "password");

        for (WebElement password : passwords) {
            authMap.replace("password", password.getText().replace("\n", ""));

            Response response = RestAssured
                    .given()
                    .queryParams(authMap)
                    .when()
                    .post("https://playground.learnqa.ru/ajax/api/get_secret_password_homework")
                    .then()
                    .statusCode(200)
                    .extract()
                    .response();

            authCookie = response.cookie("auth_cookie");

            if (!checkAuthWithCookie(authCookie).equals("You are NOT authorized")) {
                break;
            }
        }

        System.out.println("Правильный пароль: " + authMap.get("password"));

        driver.quit();
    }

    private String checkAuthWithCookie(String authCookie) {
        return RestAssured
                .given()
                .cookie("auth_cookie", authCookie)
                .when()
                .get("https://playground.learnqa.ru/ajax/api/check_auth_cookie")
                .then()
                .statusCode(200)
                .extract()
                .response()
                .print();
    }
}
