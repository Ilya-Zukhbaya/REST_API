package tests;

import io.restassured.RestAssured;
import lib.BaseCase;
import lib.api.ApiBaseRequests;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class HomeFive extends BaseCase {

    private static final ApiBaseRequests apiBaseRequests = new ApiBaseRequests();

    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "https://playground.learnqa.ru";
    }

    @Test
    public void checkRegistration() {
    }
}
