package lib;

import entities.BaseContext;
import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.api.ApiBaseRequests;
import lib.utils.DGRandomString;
import org.junit.jupiter.api.Assertions;

import java.util.Map;

import static java.util.Objects.nonNull;

public class BaseCase extends BaseContext {

    private ApiBaseRequests apiBaseRequests = new ApiBaseRequests();

    protected String getHeader(Response response, String name) {
        Headers headers = response.getHeaders();

        Assertions.assertTrue(headers.hasHeaderWithName(name), "There is no " + name + " in response headers");

        return headers.getValue(name);
    }

    protected String getCookie(Response response, String name) {
        Map<String, String> cookies = response.getCookies();

        Assertions.assertTrue(cookies.containsKey(name), "There is no " + name + " in response cookies");

        return cookies.get(name);
    }

    protected <T> T getValue(Response response, String name) {
        JsonPath jsonPath = response.jsonPath();

        Assertions.assertTrue(nonNull(jsonPath.get(name)), "There is no " + name + " in response body");

        return jsonPath.get(name);
    }

    protected void createAndLoginRandomUser() {
        setUserMap(createUserWithRandomData());
        getAuthMap().put("email", getUserMap().get("email"));
        getAuthMap().put("password", getUserMap().get("password"));

        Response tokens = apiBaseRequests.postUserWithParams(getAuthMap());

        getAuthTokens().put("auth_sid", getCookie(tokens, "auth_sid"));
        getAuthTokens().put("x-csrf-token", getHeader(tokens, "x-csrf-token"));
    }

    protected Map<String, String> createUserWithRandomData() {
        DGRandomString randomString = new DGRandomString();
        Map<String, String> authRandomMap = randomString.generateAuthData();

        Response response = apiBaseRequests.postUserWithBody(authRandomMap);
        authRandomMap.put("userId", response.jsonPath().getString("id"));

        return authRandomMap;
    }

    protected void loginInUser(String email, String password) {
        getAuthMap().put("email", email);
        getAuthMap().put("password", password);

        Response tokens = apiBaseRequests.postUserWithParams(getAuthMap());

        getAuthTokens().put("auth_sid", getCookie(tokens, "auth_sid"));
        getAuthTokens().put("x-csrf-token", getHeader(tokens, "x-csrf-token"));
    }
}
