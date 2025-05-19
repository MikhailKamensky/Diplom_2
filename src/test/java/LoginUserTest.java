import clients.UserClient;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import models.LoginUserRequest;
import models.UserCreateRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.equalTo;


public class LoginUserTest {

    public static String email = "asfd" + System.currentTimeMillis() + "@yandex.ru";
    public static String password = "somepass";
    public static String name = "Михаил";
    private UserCreateRequest userCreateRequest;

    @Before
    public void createUser() {
        this.userCreateRequest = new UserCreateRequest(email, password, name);
    }

    @Test
    @DisplayName("Login existing user")
    @Description("Check login existing user")
    public void userLoginTest() {
        LoginUserRequest userLoginRequest = new LoginUserRequest(email, password);
        UserClient userClient = new UserClient();
        userClient.userCreate(userCreateRequest);
        userClient.userLogin(userLoginRequest)
                .assertThat().statusCode(200)
                .and()
                .body("success", equalTo(true));
    }
    @Test
    @DisplayName("Login with invalid email")
    @Description("Check login with invalid email")
    public void userLoginWithWrongEmailTest() {
        LoginUserRequest userWrongLoginRequest = new LoginUserRequest("wrongEmail", password);
        LoginUserRequest userRightLoginRequest = new LoginUserRequest(email, password);
        UserClient userClient = new UserClient();
        userClient.userCreate(userCreateRequest);
        userClient.userLogin(userWrongLoginRequest)
                .assertThat().statusCode(401)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("email or password are incorrect"));
    }
    @Test
    @DisplayName("Login with invalid password")
    @Description("Check login with invalid password")
    public void userLoginWithWrongPasswordTest() {
        LoginUserRequest userWrongLoginRequest = new LoginUserRequest(email, "wrongPassword");
        LoginUserRequest userRightLoginRequest = new LoginUserRequest(email, password);
        UserClient userClient = new UserClient();
        userClient.userCreate(userCreateRequest);
        userClient.userLogin(userWrongLoginRequest)
                .assertThat().statusCode(401)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("email or password are incorrect"));
    }

    @After
    public  void deleteUser() {
        UserClient userClient = new UserClient();
        LoginUserRequest userLoginRequest = new LoginUserRequest(email, password);
        userClient.userDeleteAfterLogin(userLoginRequest);
    }

}
