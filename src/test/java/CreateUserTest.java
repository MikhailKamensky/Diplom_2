import clients.UserClient;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import models.LoginUserRequest;
import models.UserCreateRequest;
import org.junit.After;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.equalTo;



public class CreateUserTest {
    public static String email = "asfd" + System.currentTimeMillis() + "@yandex.ru";
    public static String password = "somepass";
    public static String name = "Михаил";
    private boolean skipDeleteUser = false;
    
    
    @Test
    @DisplayName("Create uniq user")
    @Description("Checking creating new user")
    public void createNewUserTest() {
        UserCreateRequest userCreateRequest = new UserCreateRequest(email, password, name);
        LoginUserRequest loginUserRequest = new LoginUserRequest(email, password);
        UserClient userClient = new UserClient();
        userClient.userCreate(userCreateRequest)
                .assertThat().statusCode(200)
                .and()
                .body("success", equalTo(true));
    }
    @Test
    @DisplayName("Create registered user")
    @Description("Checking creating already existing user")
    public void createDuplicateUserTest() {
        UserCreateRequest userCreateRequest = new UserCreateRequest(email, password, name);
        LoginUserRequest loginUserRequest = new LoginUserRequest(email, password);
        UserClient userClient = new UserClient();
        userClient.userCreate(userCreateRequest);
        userClient.userCreate(userCreateRequest)
                .assertThat().statusCode(403)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("User already exists"));
    }
    @Test
    @DisplayName("Create user without email")
    @Description("Checking creating user without email")
    public void createUserWithoutEmailTest() {
        skipDeleteUser = true;
        UserCreateRequest userCreateRequest = new UserCreateRequest(null, password, name);
        UserClient userClient = new UserClient();
        userClient.userCreate(userCreateRequest)
                .assertThat().statusCode(403)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("Email, password and name are required fields"));
    }
    @Test
    @DisplayName("Create user without password")
    @Description("Checking creating user without password")
    public void createUserWithoutPasswordTest() {
        skipDeleteUser = true;
        UserCreateRequest userCreateRequest = new UserCreateRequest(email, null, name);
        UserClient userClient = new UserClient();
        userClient.userCreate(userCreateRequest)
                .assertThat().statusCode(403)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("Email, password and name are required fields"));

    }
    @Test
    @DisplayName("Create user without name")
    @Description("Checking block for creating user without name")
    public void createUserWithoutNameTest() {
        skipDeleteUser = true;
        UserCreateRequest userCreateRequest = new UserCreateRequest(email, password, null);
        UserClient userClient = new UserClient();
        userClient.userCreate(userCreateRequest)
                .assertThat().statusCode(403)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @After
    public  void deleteUser() {
        if (!skipDeleteUser) {
            UserClient userClient = new UserClient();
            LoginUserRequest loginUserRequest = new LoginUserRequest(email, password);
            userClient.userDeleteAfterLogin(loginUserRequest);
        }
    }
}
