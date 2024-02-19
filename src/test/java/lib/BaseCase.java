package lib;

import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.api.ApiBaseRequests;
import org.junit.jupiter.api.Assertions;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.nonNull;

public class BaseCase {

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
        userMap = apiBaseRequests.createUserWithRandomData();
        authMap.put("email", userMap.get("email"));
        authMap.put("password", userMap.get("password"));

        Response tokens = apiBaseRequests.postUserWithParams(authMap);

        authTokens.put("auth_sid", getCookie(tokens, "auth_sid"));
        authTokens.put("x-csrf-token", getHeader(tokens, "x-csrf-token"));
    }
}
