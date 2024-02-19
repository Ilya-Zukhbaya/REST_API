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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Epic("HomeWork Four Epic")
@Feature("HomeWork Four feature")
@Story("HomeWork Four Story")
public class HomeFour extends BaseCase {

    //region Context
    private static final Map<String, String> userMap = new HashMap<>();
    private static final Map<String, String> authMap = new HashMap<>();
    private static final Map<String, String> authTokens  = new HashMap<>();
    private static final DGRandomString randomString = new DGRandomString();
    private static final ApiBaseRequests apiBaseRequests = new ApiBaseRequests();
    //endregion

    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "https://playground.learnqa.ru";

        userMap.put("username", "learnQa");
        userMap.put("firstName", "learnQa");
        userMap.put("lastName", "Ili");
        userMap.put("password", "1234");

        authMap.put("email", "vinkotov@example.com");
        authMap.put("password", "1234");
    }

    //region Tests
    @Test
    @DisplayName("Негативная проверка создания пользователя")
    @Description("Проверяем невозможность создать пользователя с неправильным email")
    @Tag("API")
    public void createUserNegative() {
        userMap.put("email", "vinkotov@example.com");

        Response response = apiBaseRequests.postUserWithBody(userMap);

        HardAssertions.assertStatus(response, 400);
        HardAssertions.assertText(response, "Users with email '" + userMap.get("email") + "' already exists");
    }

    @Test
    @DisplayName("Позитивная проверка создания пользователя")
    @Description("Проверяем возможность создать пользователя с правильным email")
    @Tag("API")
    public void createUserPositive() {
        String email = randomString.generateRandomString(10) + "@test.com";
        userMap.put("email", email);

        Response response = apiBaseRequests.postUserWithBody(userMap);

        HardAssertions.assertStatus(response, 200);
        HardAssertions.assertBodyKeyPresent(response, "id");
    }

    @Test
    @DisplayName("Негативная проверка получения данных несуществующего пользователя")
    @Description("Проверяем невозможность получения данных несуществующего пользователя")
    @Tag("API")
    public void getUserDataNegative() {
        int min = 100;
        int max = 300;
        int id = new Random().nextInt(max - min) + min;

        Response response = apiBaseRequests.getUserId(id);

        HardAssertions.assertStatus(response, 404);
        HardAssertions.assertText(response, "User not found");
        HardAssertions.assertBodyKeysNotPresent(response, new String[]{"id", "username", "firstName", "lastName", "email"});
    }

    @Test
    @DisplayName("Позитивная проверка получения данных существующего пользователя")
    @Description("Проверяем возможность получения данных существующего пользователя")
    @Tag("API")
    public void getUserDataPositive() {
        Response tokens = apiBaseRequests.postUserLoginWithBody(authMap);

        authTokens.put("user_id", getValue(tokens, "user_id").toString());
        authTokens.put("auth_sid", getCookie(tokens, "auth_sid"));
        authTokens.put("x-csrf-token", getHeader(tokens, "x-csrf-token"));

        Response response = apiBaseRequests.getUser(authTokens.get("user_id"), authTokens.get("auth_sid"), authTokens.get("x-csrf-token"));

        HardAssertions.assertStatus(response, 200);
        HardAssertions.assertBodyKeysPresent(response, new String[]{"id", "username", "firstName", "lastName", "email"});
    }

    @Test
    @DisplayName("CRUD Пользователя")
    @Description("Проверяем возможность создать/получить/обновить/удалить данные пользователя")
    @Tag("API")
    public void checkPutUser() {
        //region CreateUser
        Map<String, String> authRandomMap = randomString.generateAuthData();

        Response response = apiBaseRequests.postUserWithBody(authRandomMap);

        HardAssertions.assertBodyKeyPresent(response, "id");
        String userId = response.jsonPath().getString("id");
        //endregion

        //region Login
        Map<String, String> loginMap = new HashMap<>();
        loginMap.put("email", authRandomMap.get("email"));
        loginMap.put("password", authRandomMap.get("password"));

        Response login = apiBaseRequests.postUserLoginWithBody(loginMap);

        authTokens.put("auth_sid", getCookie(login, "auth_sid"));
        authTokens.put("x-csrf-token", getHeader(login, "x-csrf-token"));
        //endregion

        //region UpdateUser
        String newName = "testtest";
        Map<String, String> replacedMap = new HashMap<>();
        replacedMap.put("lastName", newName);

        apiBaseRequests.updateUser(userId, authTokens.get("auth_sid"), authTokens.get("x-csrf-token"), replacedMap);
        //endregion

        //region UserGet
        Response getUser = getUser(userId);

        HardAssertions.assertByName(getUser, "lastName", newName);
        //endregion

        //region DeleteUser
        apiBaseRequests.deleteUser(userId, authTokens.get("auth_sid"), authTokens.get("x-csrf-token"));

        HardAssertions.assertStatus(getUser(userId), 404);
        HardAssertions.assertText(getUser(userId), "User not found");
        getUser.print();
        //endregion
    }
    //endregion

    //region Methods
    private Response getUser(String userId) {
        return apiBaseRequests.getUser(userId, authTokens.get("auth_sid"), authTokens.get("x-csrf-token"));
    }
    //endregion
}
