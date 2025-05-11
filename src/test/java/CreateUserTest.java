import clients.UserClient;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import models.LoginUserRequest;
import models.UserCreateRequest;
import org.junit.After;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.equalTo;



public class CreateUserTest {
    public static String email = "asfd12312@yandex.ru";
    public static String password = "somepass";
    public static String name = "Михаил";
    private boolean skipDeleteUser = false;
    
    
    @Test
    @DisplayName("Создание уникального пользователя")
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
    @DisplayName("Создание пользователя, который уже зарегистрирован")
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
    @DisplayName("Создание пользователя без поля email")
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
    @DisplayName("Создание пользователя без поля password")
    @Description("Проверка не возможности создать пользователя без поля password")
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
    @DisplayName("Создание пользователя без поля name")
    @Description("Проверка не возможности создать пользователя без поля name")
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
