package tests.user;

import io.qameta.allure.*;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.*;
import java.util.stream.Stream;

@Epic("Проверки UPDATE/CREATE USER Epic")
@Feature("Проверки UPDATE/CREATE USER Feature")
@Story("Проверки UPDATE/CREATE USER Story")
public class UpdateAndCreateUser extends BaseCase {

    //region Context
    private ApiBaseRequests apiBaseRequests = new ApiBaseRequests();
    private static final DGRandomString randomString = new DGRandomString();

    static Stream<List<String>> getUserArgs() {
        return Stream.of(
                Arrays.asList("", "Test", "Testov", "Test@test.com", "Wall"),
                Arrays.asList("Test", "", "Test", "Test@test.com", "Wall"),
                Arrays.asList("Test", "Test", "", "Test@test.com", "Wall"),
                Arrays.asList("Test", "Testog", "Testov", "", "Wall"),
                Arrays.asList("Test", "Testik", "Testov", "Test@test.com", "")
        );
    }
    //endregion

    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "https://playground.learnqa.ru";
    }

    //region Tests
    @Test
    @DisplayName("Негативная проверка создания пользователя с неправильным email")
    @Description("Проверяем невозможность создать пользователя с неправильным email")
    @Tag("API")
    public void checkRegistrationWithWrongEmail() {
        getAuthMap().put("email", randomString.generateRandomString(10));
        getAuthMap().put("password", "1234");

        HardAssertions.assertText(apiBaseRequests.postUserWithParams(getAuthMap()), "Invalid username/password supplied");
    }

    @ParameterizedTest
    @MethodSource("getUserArgs")
    @DisplayName("Негативная проверка создания пользователя с одним неверным параметром")
    @Description("Проверяем невозможность создать пользователя с одним неверным параметром")
    @Tag("API")
    public void checkRegWithWrongData(List<String> authData) {
        Map<String, String> authMap = new HashMap<>();
        List<String> nullMap = new ArrayList<>();
        String[] keys = {"username", "firstName", "lastName", "email", "password"};

        for (String key : keys) {
            authMap.put(key, null);
        }

        for (int i = 0; i < authData.size(); i++) {
            String data = authData.get(i);
            if (!data.isEmpty()) {
                authMap.put(keys[i], data);
            } else {
                nullMap.add(keys[i]);
                authMap.remove(keys[i]);
            }
        }

        HardAssertions.assertText(apiBaseRequests.postUserWithBody(authMap), "The following required params are missed: " + nullMap.get(0));
    }

    @Test
    @DisplayName("Негативная проверка создания пользователя с коротким username")
    @Description("Проверяем невозможность создать пользователя с коротким username")
    @Tag("API")
    public void checkUserCreationWithShortName() {
        getAuthMap().put("username", randomString.generateRandomString(1));
        setAuthMap(randomString.generateAuthData(getAuthMap()));

        HardAssertions.assertText(apiBaseRequests.postUserWithBody(getAuthMap()), "The value of 'username' field is too short");
    }

    @Test
    @DisplayName("Негативная проверка создания пользователя с длинным username")
    @Description("Проверяем невозможность создать пользователя с длинным username")
    @Tag("API")
    public void checkUserCreationWithLongName() {
        getAuthMap().put("username", randomString.generateRandomString(251));
        setAuthMap(randomString.generateAuthData(getAuthMap()));

        HardAssertions.assertText(apiBaseRequests.postUserWithBody(getAuthMap()), "The value of 'username' field is too long");
    }

    @Test
    @DisplayName("Позитивная проверка получения данных другого пользователя")
    @Description("Проверяем возможность получения ограниченных данных другого пользователя")
    @Tag("API")
    public void getUserDataPositive() {
        loginInUser("vinkotov@example.com", "1234");
        Response response = apiBaseRequests.getUser("1", getAuthTokens().get("auth_sid"), getAuthTokens().get("x-csrf-token"));

        HardAssertions.assertStatus(response, 200);
        HardAssertions.assertBodyKeysNotPresent(response, new String[]{"id", "firstName", "lastName", "email"});
        HardAssertions.assertBodyKeyPresent(response, "username");
    }

    @Test
    @DisplayName("Проверки на редактирование данных пользователей")
    @Description("Проверяем возможность редактирования данных пользователей")
    @Tag("API")
    public void checkPutUser() {
        Map<String, String> replacedMap = new HashMap<>();

        //FIRST TEST
        updateWithoutToken(replacedMap);
        //ENDREGION

        //SECOND TEST
        updateOtherUser(replacedMap);
        //ENDREGION

        //THIRD TEST
        replacedMap.clear();
        updateUserWithWrongEmail(replacedMap);
        //ENDREGION

        //FOURTH TEST
        replacedMap.clear();
        updateUserWithShortFirstName(replacedMap);
        //endregion
    }
    //endregion

    //region Methods
    @Step("Редактирование без auth token")
    private void updateWithoutToken(Map<String, String> replacedMap) {
        replacedMap.put("username", "testtest");
        HardAssertions.assertText(apiBaseRequests.updateUserAndGetResponse("1", "", "", replacedMap), "Auth token not supplied");
    }

    @Step("Редактирование другого пользователя с auth token")
    private void updateOtherUser(Map<String, String> replacedMap) {
        createAndLoginRandomUser();
        HardAssertions.assertText(apiBaseRequests.updateUserAndGetResponse("92414", getAuthTokens().get("auth_sid"), getAuthTokens().get("x-csrf-token"), replacedMap), "");
    }

    @Step("Редактирование пользователя с неправильным email")
    private void updateUserWithWrongEmail(Map<String, String> replacedMap) {
        replacedMap.put("email", "wrongemail");
        createAndLoginRandomUser();
        HardAssertions.assertText(apiBaseRequests.updateUserAndGetResponse(getUserMap().get("userId"), getAuthTokens().get("auth_sid"), getAuthTokens().get("x-csrf-token"), replacedMap), "Invalid email format");
    }

    @Step("Редактирование пользователя с коротким firstName")
    private void updateUserWithShortFirstName(Map<String, String> replacedMap) {
        replacedMap.put("firstName", "a");
        createAndLoginRandomUser();
        HardAssertions.assertText(apiBaseRequests.updateUserAndGetResponse(getUserMap().get("userId"), getAuthTokens().get("auth_sid"), getAuthTokens().get("x-csrf-token"), replacedMap), "{\"error\":\"Too short value for field firstName\"}");
    }
    //endregion
}
