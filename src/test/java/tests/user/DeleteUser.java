package tests.user;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.BaseCase;
import lib.HardAssertions;
import lib.api.ApiBaseRequests;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Map;

@Epic("Проверки DELETE USER Epic")
@Feature("Проверки DELETE USER Feature")
@Story("Проверки DELETE USER Story")
public class DeleteUser extends BaseCase {

    //region Context
    private ApiBaseRequests apiBaseRequests = new ApiBaseRequests();
    //endregion

    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "https://playground.learnqa.ru";
    }

    //region Tests
    @Test
    @DisplayName("Негативная проверка удаления superuser")
    @Description("Проверяем невозможность удалить superuser")
    @Tag("API")
    public void checkDeleteNegativeScenario() {
        Map<String, String> userMap = createUserWithRandomData();
        getAuthMap().put("email", "vinkotov@example.com");
        getAuthMap().put("password", "1234");

        Response tokens = apiBaseRequests.postUserWithParams(getAuthMap());

        getAuthTokens().put("auth_sid", getCookie(tokens, "auth_sid"));
        getAuthTokens().put("x-csrf-token", getHeader(tokens, "x-csrf-token"));

        HardAssertions.assertText(apiBaseRequests.deleteUserAndGetResponse(userMap.get("userId"), getAuthTokens().get("auth_sid"), getAuthTokens().get("x-csrf-token")), "Please, do not delete test users with ID 1, 2, 3, 4 or 5.");
    }

    @Test
    @DisplayName("Позитивная проверка удаления пользователя, которого мы создали")
    @Description("Проверяем возможность удалить пользователя, которого мы создали")
    @Tag("API")
    public void checkDeletePositiveScenario() {
        createAndLoginRandomUser();

        HardAssertions.assertStatus(apiBaseRequests.deleteUserAndGetResponse(getUserMap().get("userId"), getAuthTokens().get("auth_sid"), getAuthTokens().get("x-csrf-token")), 200);

        Response response = apiBaseRequests.getUserId(Integer.parseInt(getUserMap().get("userId")));

        HardAssertions.assertStatus(response, 404);
        HardAssertions.assertText(response, "User not found");

    }

    @Test
    @DisplayName("Негативная проверка удаления пользователя, которого мы создали ранее")
    @Description("Проверяем невозможность удалить другого пользователя, которого мы создали ранее")
    @Tag("API")
    public void checkDeleteOtherUser() {
        createAndLoginRandomUser();
        HardAssertions.assertStatus(apiBaseRequests.deleteUserAndGetResponse("92414", getAuthTokens().get("auth_sid"), getAuthTokens().get("x-csrf-token")), 200);
    }
    //endregion
}
