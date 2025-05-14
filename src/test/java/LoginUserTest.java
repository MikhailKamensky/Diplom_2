import clients.UserClient;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import models.LoginUserRequest;
import models.UserCreateRequest;
import org.junit.After;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.equalTo;


public class LoginUserTest {

    public static String email = "asfd" + System.currentTimeMillis() + "@yandex.ru";
    public static String password = "somepass";
    public static String name = "Михаил";

    @Test
    @DisplayName("Login existing user")
    @Description("Check login existing user")
    public void userLogin() {
        UserCreateRequest userCreateRequest = new UserCreateRequest(email, password, name);
        LoginUserRequest userLoginRequest = new LoginUserRequest(email, password);
        UserClient userClient = new UserClient();
        userClient.userCreate(userCreateRequest);
        userClient.userLogin(userLoginRequest)
                .assertThat().body("success", equalTo(true))
                .and()
                .statusCode(200);
    }
    @Test
    @DisplayName("Login with invalid email")
    @Description("Check login with invalid email")
    public void userLoginWithWrongEmail() {
        UserCreateRequest userCreateRequest = new UserCreateRequest(email, password, name);
        LoginUserRequest userWrongLoginRequest = new LoginUserRequest("wrongEmail", password);
        LoginUserRequest userRightLoginRequest = new LoginUserRequest(email, password);
        UserClient userClient = new UserClient();
        userClient.userCreate(userCreateRequest);
        userClient.userLogin(userWrongLoginRequest)
                .assertThat().body("success", equalTo(false))
                .and()
                .statusCode(401);
    }
    @Test
    @DisplayName("Login with invalid password")
    @Description("Check login with invalid password")
    public void userLoginWithWrongPassword() {
        UserCreateRequest userCreateRequest = new UserCreateRequest(email, password, name);
        LoginUserRequest userWrongLoginRequest = new LoginUserRequest(email, "wrongPassword");
        LoginUserRequest userRightLoginRequest = new LoginUserRequest(email, password);
        UserClient userClient = new UserClient();
        userClient.userCreate(userCreateRequest);
        userClient.userLogin(userWrongLoginRequest)
                .assertThat().body("success", equalTo(false))
                .and()
                .statusCode(401);
    }

    @After
    public  void deleteUser() {
        UserClient userClient = new UserClient();
        LoginUserRequest userLoginRequest = new LoginUserRequest(email, password);
        userClient.userDeleteAfterLogin(userLoginRequest);
    }

}
