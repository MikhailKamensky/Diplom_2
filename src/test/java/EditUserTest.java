import clients.UserClient;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import models.LoginUserRequest;
import models.UserCreateRequest;
import org.junit.After;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.equalTo;


public class EditUserTest {

    public static String email = "asfd" + System.currentTimeMillis() + "@yandex.ru";
    public static String password = "somepass";
    public static String name = "Михаил";


    public static String newEmail = "zxcvb1234@yandex.ru";
    public static String newPassword = "zxcvb123";
    public static String newName = "Марк";


    @After
    public  void deleteUser() {
        UserClient userClient = new UserClient();
        LoginUserRequest loginUserRequest = new LoginUserRequest(email, password);
        userClient.userDeleteAfterLogin(loginUserRequest);
    }
    @Test
    @DisplayName("Обновление email с авторизацией")
    @Description("Проверка возможности обновления поля email с авторизацией")
    public void userEditEmailWithAuthorization() {
        UserCreateRequest userCreateAndEditRequest = new UserCreateRequest(email, password, name);
        UserCreateRequest userEditRequest = new UserCreateRequest(newEmail, password, name);
        LoginUserRequest loginUserRequest = new LoginUserRequest(email, password);
        LoginUserRequest userNewLoginRequest = new LoginUserRequest(newEmail, password);
        UserClient userClient = new UserClient();
        userClient.userCreate(userCreateAndEditRequest);
        userClient.userEditAfterLogin(loginUserRequest, userEditRequest)
                .assertThat().body("success", equalTo(true))
                .and()
                .assertThat().body("user.email", equalTo(newEmail))
                .and()
                .statusCode(200);
    }
    @Test
    @DisplayName("Обновление email без авторизации")
    @Description("Проверка не возможности обновления поля email без авторизации")
    public void userEditEmailWithoutAuthorization() {
        UserCreateRequest userCreateAndEditRequest = new UserCreateRequest(email, password, name);
        UserCreateRequest userEditRequest = new UserCreateRequest(newEmail, password, name);
        LoginUserRequest loginUserRequest = new LoginUserRequest(email, password);
        UserClient userClient = new UserClient();
        userClient.userCreate(userCreateAndEditRequest);
        userClient.userEdit(userEditRequest)
                .assertThat().body("success", equalTo(false))
                .and()
                .statusCode(401);
    }
    @Test
    @DisplayName("Обновление password с авторизацией")
    @Description("Проверка возможности обновления поля password с авторизацией")
    public void userEditPasswordWithAuthorization() {
        UserCreateRequest userCreateAndEditRequest = new UserCreateRequest(email, password, name);
        UserCreateRequest userEditRequest = new UserCreateRequest(email, newPassword, name);
        LoginUserRequest loginUserRequest = new LoginUserRequest(email, password);
        LoginUserRequest userNewLoginRequest = new LoginUserRequest(email, newPassword);
        UserClient userClient = new UserClient();
        userClient.userCreate(userCreateAndEditRequest);
        userClient.userEditAfterLogin(loginUserRequest, userEditRequest)
                .assertThat().body("success", equalTo(true))
                .and()
                .statusCode(200);
        userClient.userLogin(userNewLoginRequest)
                .assertThat().body("success", equalTo(true))
                .and()
                .statusCode(200);
    }
    @Test
    @DisplayName("Обновление password без авторизации")
    @Description("Проверка не возможности обновления поля password без авторизации")
    public void userEditPasswordWithoutAuthorization() {
        UserCreateRequest userCreateAndEditRequest = new UserCreateRequest(email, password, name);
        UserCreateRequest userEditRequest = new UserCreateRequest(email, newPassword, name);
        LoginUserRequest loginUserRequest = new LoginUserRequest(email, password);
        UserClient userClient = new UserClient();
        userClient.userCreate(userCreateAndEditRequest);
        userClient.userEdit(userEditRequest)
                .assertThat().body("success", equalTo(false))
                .and()
                .statusCode(401);
    }
    @Test
    @DisplayName("Обновление name с авторизацией")
    @Description("Проверка возможности обновления поля name с авторизацией")
    public void userEditNameWithAuthorization() {
        UserCreateRequest userCreateAndEditRequest = new UserCreateRequest(email, password, name);
        UserCreateRequest userEditRequest = new UserCreateRequest(email, password, newName);
        LoginUserRequest loginUserRequest = new LoginUserRequest(email, password);
        UserClient userClient = new UserClient();
        userClient.userCreate(userCreateAndEditRequest);
        userClient.userEditAfterLogin(loginUserRequest, userEditRequest)
                .assertThat().body("success", equalTo(true))
                .and()
                .assertThat().body("user.name", equalTo(newName))
                .and()
                .statusCode(200);
    }
    @Test
    @DisplayName("Обновление name без авторизации")
    @Description("Проверка не возможности обновления поля name без авторизации")
    public void userEditNameWithoutAuthorization() {
        UserCreateRequest userCreateAndEditRequest = new UserCreateRequest(email, password, name);
        UserCreateRequest userEditRequest = new UserCreateRequest(email, password, newName);
        LoginUserRequest loginUserRequest = new LoginUserRequest(email, password);
        UserClient userClient = new UserClient();
        userClient.userCreate(userCreateAndEditRequest);
        userClient.userEdit(userEditRequest)
                .assertThat().body("success", equalTo(false))
                .and()
                .statusCode(401);
    }


}
