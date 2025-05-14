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
    @Description("Проверка возможности создать нового уникального пользователя")
    public void createNewUser() {
        UserCreateRequest userCreateRequest = new UserCreateRequest(email, password, name);
        LoginUserRequest loginUserRequest = new LoginUserRequest(email, password);
        UserClient userClient = new UserClient();
        userClient.userCreate(userCreateRequest)
                .assertThat().body("success", equalTo(true))
                .and()
                .statusCode(200);
    }
    @Test
    @DisplayName("Create registered user")
    @Description("Проверка не возможности создать пользователя, который уже зарегистрирован")
    public void createDuplicateUser() {
        UserCreateRequest userCreateRequest = new UserCreateRequest(email, password, name);
        LoginUserRequest loginUserRequest = new LoginUserRequest(email, password);
        UserClient userClient = new UserClient();
        userClient.userCreate(userCreateRequest);
        userClient.userCreate(userCreateRequest)
                .assertThat().body("success", equalTo(false))
                .and()
                .statusCode(403);
    }
    @Test
    @DisplayName("Create user without email")
    @Description("Проверка не возможности создать пользователя без поля email")
    public void createUserWithoutEmail() {
        skipDeleteUser = true;
        UserCreateRequest userCreateRequest = new UserCreateRequest(null, password, name);
        UserClient userClient = new UserClient();
        userClient.userCreate(userCreateRequest)
                .assertThat().body("success", equalTo(false))
                .and()
                .statusCode(403);
    }
    @Test
    @DisplayName("Create user without password")
    @Description("Checking creating user without password")
    public void createUserWithoutPassword() {
        skipDeleteUser = true;
        UserCreateRequest userCreateRequest = new UserCreateRequest(email, null, name);
        UserClient userClient = new UserClient();
        userClient.userCreate(userCreateRequest)
                .assertThat().body("success", equalTo(false))
                .and()
                .statusCode(403);
    }
    @Test
    @DisplayName("Create user without name")
    @Description("Checking block for creating user without password")
    public void createUserWithoutName() {
        skipDeleteUser = true;
        UserCreateRequest userCreateRequest = new UserCreateRequest(email, password, null);
        UserClient userClient = new UserClient();
        userClient.userCreate(userCreateRequest)
                .assertThat().body("success", equalTo(false))
                .and()
                .statusCode(403);
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
