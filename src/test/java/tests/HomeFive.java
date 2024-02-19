package tests;

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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.*;
import java.util.stream.Stream;

@Epic("HomeWork Five Epic")
@Feature("HomeWork Five feature")
@Story("HomeWork Five Story")
public class HomeFive extends BaseCase {

    //region Context
    private static final ApiBaseRequests apiBaseRequests = new ApiBaseRequests();
    private static final DGRandomString randomString = new DGRandomString();
    private static final Map<String, String> authTokens  = new HashMap<>();
    private static Map<String, String> authMap = new HashMap<>();
    private static String email;
    private static String password;

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

        email = "vinkotov@example.com";
        password = "1234";
    }

    //region Tests
    @Test
    public void checkRegistrationWithWrongEmail() {
        authMap.put("email", randomString.generateRandomString(10));
        authMap.put("password", password);

        HardAssertions.assertText(apiBaseRequests.postUserWithParams(authMap), "Invalid username/password supplied");
    }

    @ParameterizedTest
    @MethodSource("getUserArgs")
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
    public void checkUserCreationWithShortName() {
        authMap.put("username", randomString.generateRandomString(1));
        authMap = randomString.generateAuthData(authMap);

        HardAssertions.assertText(apiBaseRequests.postUserWithBody(authMap), "The value of 'username' field is too short");
    }

    @Test
    public void checkUserCreationWithLongName() {
        authMap.put("username", randomString.generateRandomString(251));
        authMap = randomString.generateAuthData(authMap);

        HardAssertions.assertText(apiBaseRequests.postUserWithBody(authMap), "The value of 'username' field is too long");
    }

    @Test
    public void getUserDataPositive() {
        authMap.put("email", email);
        authMap.put("password", password);

        Response tokens = apiBaseRequests.postUserWithParams(authMap);

        authTokens.put("user_id", getValue(tokens, "user_id").toString());
        authTokens.put("auth_sid", getCookie(tokens, "auth_sid"));
        authTokens.put("x-csrf-token", getHeader(tokens, "x-csrf-token"));

        Response response = apiBaseRequests.getUser("1", authTokens.get("auth_sid"), authTokens.get("x-csrf-token"));

        HardAssertions.assertStatus(response, 200);
        HardAssertions.assertBodyKeysNotPresent(response, new String[]{"id", "firstName", "lastName", "email"});
        HardAssertions.assertBodyKeyPresent(response, "username");
    }

    @Test
    public void checkPutUser() {
        //FIRST TEST
        String newName = "testtest";
        Map<String, String> replacedMap = new HashMap<>();
        replacedMap.put("username", newName);

        HardAssertions.assertText(apiBaseRequests.updateUserAndGetResponse("1", "", "", replacedMap), "Auth token not supplied");
        //ENDREGION

        //SECOND TEST
        Map<String, String> userMap = apiBaseRequests.createUserWithRandomData();
        authMap.put("email", userMap.get("email"));
        authMap.put("password", userMap.get("password"));

        Response tokens = apiBaseRequests.postUserWithParams(authMap);

        authTokens.put("user_id", getValue(tokens, "user_id").toString());
        authTokens.put("auth_sid", getCookie(tokens, "auth_sid"));
        authTokens.put("x-csrf-token", getHeader(tokens, "x-csrf-token"));

        HardAssertions.assertText(apiBaseRequests.updateUserAndGetResponse("92414", authTokens.get("auth_sid"), authTokens.get("x-csrf-token"), replacedMap), "");

        replacedMap.clear();
        //ENDREGION

        //THIRD TEST
        replacedMap.put("email", "wrongemail");

        userMap = apiBaseRequests.createUserWithRandomData();
        authMap.put("email", userMap.get("email"));
        authMap.put("password", userMap.get("password"));

        Response login = apiBaseRequests.postUserLoginWithBody(authMap);

        authTokens.put("auth_sid", getCookie(login, "auth_sid"));
        authTokens.put("x-csrf-token", getHeader(login, "x-csrf-token"));

        HardAssertions.assertText(apiBaseRequests.updateUserAndGetResponse(userMap.get("userId"), authTokens.get("auth_sid"), authTokens.get("x-csrf-token"), replacedMap), "Invalid email format");

        replacedMap.clear();
        replacedMap.put("firstName", "a");
        HardAssertions.assertText(apiBaseRequests.updateUserAndGetResponse(userMap.get("userId"), authTokens.get("auth_sid"), authTokens.get("x-csrf-token"), replacedMap), "{\"error\":\"Too short value for field firstName\"}");
        //ENDREGION
    }
    //endregion
}
