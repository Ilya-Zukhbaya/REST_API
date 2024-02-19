package lib;

import io.restassured.response.Response;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HardAssertions {

    public static <T> void assertByName(Response response, String path, T expectedValue) {
        response.then().assertThat().body("$", hasKey(path));

        assertEquals(expectedValue, response.jsonPath().get(path));
    }

    public static void assertCookie(Response response, String path, String expectedValue) {
        response.then().assertThat().cookie(path);

        assertEquals(expectedValue, response.getCookie(path));
    }

    public static void assertHeader(Response response, String path, String expectedValue) {
        assertEquals(expectedValue, response.getHeader(path));
    }

    public static void assertStatus(Response response, int expectedValue) {
        assertEquals(expectedValue, response.getStatusCode());
    }

    public static void assertText(Response response, String expectedText) {
        assertEquals(expectedText, response.asString());
    }

    public static void assertBodyKeyPresent(Response response, String path) {
        response.then().assertThat().body("$", hasKey(path));
    }

    public static void assertBodyKeyNotPresent(Response response, String path) {
        response.then().assertThat().body("$", not(hasKey(path)));
    }

    public static void assertBodyKeysNotPresent(Response response, String[] paths) {
        for (String path : paths) {
           assertBodyKeyNotPresent(response, path);
        }
    }

    public static void assertBodyKeysPresent(Response response, String[] paths) {
        for (String path : paths) {
            assertBodyKeyPresent(response, path);
        }
    }
}
