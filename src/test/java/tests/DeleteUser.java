package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.BaseCase;
import lib.HardAssertions;
import lib.api.ApiBaseRequests;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class DeleteUser extends BaseCase {

    private static final ApiBaseRequests apiBaseRequests = new ApiBaseRequests();
    private static final Map<String, String> authTokens  = new HashMap<>();
    private static final Map<String, String> authMap = new HashMap<>();
    private static Map<String, String> userMap = new HashMap<>();

    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "https://playground.learnqa.ru";
    }

    @Test
    public void checkDeleteNegativeScenario() {
        Map<String, String> userMap = apiBaseRequests.createUserWithRandomData();
        authMap.put("email", "vinkotov@example.com");
        authMap.put("password", "1234");

        Response tokens = apiBaseRequests.postUserWithParams(authMap);

        authTokens.put("auth_sid", getCookie(tokens, "auth_sid"));
        authTokens.put("x-csrf-token", getHeader(tokens, "x-csrf-token"));

        HardAssertions.assertText(apiBaseRequests.deleteUserAndGetResponse(userMap.get("userId"), authTokens.get("auth_sid"), authTokens.get("x-csrf-token")), "Please, do not delete test users with ID 1, 2, 3, 4 or 5.");
    }

    @Test
    public void checkDeletePositiveScenario() {
        createAndLoginRandomUser();

        HardAssertions.assertStatus(apiBaseRequests.deleteUserAndGetResponse(userMap.get("userId"), authTokens.get("auth_sid"), authTokens.get("x-csrf-token")), 200);

        Response response = apiBaseRequests.getUserId(Integer.parseInt(userMap.get("userId")));

        HardAssertions.assertStatus(response, 404);
        HardAssertions.assertText(response, "User not found");

    }

    @Test
    public void checkDeleteOtherUser() {
        createAndLoginRandomUser();
        HardAssertions.assertStatus(apiBaseRequests.deleteUserAndGetResponse("92414", authTokens.get("auth_sid"), authTokens.get("x-csrf-token")), 200);
    }
}
