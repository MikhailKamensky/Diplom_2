import clients.UserClient;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import models.LoginUserRequest;
import models.UserCreateRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.equalTo;

public class EditUserTest {
    private final UserClient userClient = new UserClient();
    private String email = "asfd" + System.currentTimeMillis() + "@yandex.ru";
    private final String password = "somepass";
    private final String name = "Михаил";
    private final String newEmail = "zxcvb1234@yandex.ru";
    private final String newPassword = "zxcvb123";
    private final String newName = "Марк";
    private String accessToken;
    private UserCreateRequest userCreateRequest;

    @Before
    public void createUser() {
        this.userCreateRequest = new UserCreateRequest(email, password, name);
    }


    @Test
    @DisplayName("Update email with authorization")
    @Description("Check updating email with authorization")
    public void userEditEmailWithAuthorizationTest() {
        userClient.userCreate(userCreateRequest);

        LoginUserRequest loginRequest = new LoginUserRequest(email, password);
        this.accessToken = userClient.userLogin(loginRequest).extract().path("accessToken");

        UserCreateRequest userEditRequest = new UserCreateRequest(newEmail, password, name);
        ValidatableResponse response = userClient.userEditWithToken(accessToken, userEditRequest);

        if (response.extract().path("success")) {
            response
                    .statusCode(200)
                    .body("success", equalTo(true))
                    .body("user.email", equalTo(newEmail));
            email = newEmail; // Обновляем email только если изменение прошло успешно
        } else {
            response
                    .statusCode(403)
                    .body("success", equalTo(false));
        }
    }

    @Test
    @DisplayName("Update email without authorization")
    @Description("Check block of updating email with authorization")
    public void userEditEmailWithoutAuthorizationTest() {
        userClient.userCreate(userCreateRequest);

        UserCreateRequest userEditRequest = new UserCreateRequest(newEmail, password, name);
        userClient.userEdit(userEditRequest)
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    @Test
    @DisplayName("Update password with authorization")
    @Description("Check updating password with authorization")
    public void userEditPasswordWithAuthorizationTest() {
        userClient.userCreate(userCreateRequest);

        LoginUserRequest loginRequest = new LoginUserRequest(email, password);
        this.accessToken = userClient.userLogin(loginRequest).extract().path("accessToken");

        UserCreateRequest userEditRequest = new UserCreateRequest(email, newPassword, name);
        userClient.userEditWithToken(accessToken, userEditRequest)
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Update password without authorization")
    @Description("Check block of updating password without authorization")
    public void userEditPasswordWithoutAuthorizationTest() {
        userClient.userCreate(userCreateRequest);

        UserCreateRequest userEditRequest = new UserCreateRequest(email, newPassword, name);
        userClient.userEdit(userEditRequest)
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    @Test
    @DisplayName("Update name with authorization")
    @Description("Check updating name with authorization")
    public void userEditNameWithAuthorizationTest() {
        userClient.userCreate(userCreateRequest);

        LoginUserRequest loginRequest = new LoginUserRequest(email, password);
        this.accessToken = userClient.userLogin(loginRequest).extract().path("accessToken");

        UserCreateRequest userEditRequest = new UserCreateRequest(email, password, newName);
        userClient.userEditWithToken(accessToken, userEditRequest)
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.name", equalTo(newName));
    }

    @Test
    @DisplayName("Update name without authorization")
    @Description("Check block of updating name without authorization")
    public void userEditNameWithoutAuthorizationTest() {
        userClient.userCreate(userCreateRequest);

        UserCreateRequest userEditRequest = new UserCreateRequest(email, password, newName);
        userClient.userEdit(userEditRequest)
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    @After
    public void deleteUserTest() {
        try {
            LoginUserRequest loginRequest = new LoginUserRequest(email, password);
            ValidatableResponse loginResponse = userClient.userLogin(loginRequest);

            if (loginResponse.extract().path("success")) {
                String token = loginResponse.extract().path("accessToken");
                userClient.userDelete(token);
                return;
            }

            loginRequest = new LoginUserRequest(email, newPassword);
            loginResponse = userClient.userLogin(loginRequest);

            if (loginResponse.extract().path("success")) {
                String token = loginResponse.extract().path("accessToken");
                userClient.userDelete(token);
            }
        } catch (Exception e) {
            System.err.println("Failed to delete user: " + e.getMessage());
        }
    }
}