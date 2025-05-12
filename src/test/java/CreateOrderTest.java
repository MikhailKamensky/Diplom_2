import clients.OrderClient;
import clients.UserClient;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import models.CreateOrderRequest;
import models.LoginUserRequest;
import models.UserCreateRequest;
import org.junit.After;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.isA;

public class CreateOrderTest {

    public static String email = "asfd" + System.currentTimeMillis() + "@yandex.ru";
    public static String password = "somepass";
    public static String name = "Михаил";
    public static List<String> ingredients = new ArrayList<>();
    private boolean skipDeleteUser = false;

    @After
    public  void deleteUser() {
        UserClient userClient = new UserClient();
        LoginUserRequest loginUserRequest = new LoginUserRequest(email, password);
        userClient.userDeleteAfterLogin(loginUserRequest);
    }
    @After
    public void ingredientsClean() {
        if (!skipDeleteUser) {
            ingredients.clear();
        }
    }
    @Test
    @DisplayName("Create new order after authorization")
    @Description("Проверка возможности создания заказа после авторизации")
    public void orderCreateWithAuthorization() {
        UserCreateRequest userCreateRequest = new UserCreateRequest(email, password, name);
        LoginUserRequest loginUserRequest = new LoginUserRequest(email, password);
        ingredients.add("61c0c5a71d1f82001bdaaa6d");
        CreateOrderRequest createOrderRequest = new CreateOrderRequest(ingredients);
        UserClient userClient = new UserClient();
        OrderClient orderClient = new OrderClient();
        userClient.userCreate(userCreateRequest);
        orderClient.orderCreateAfterLogin(loginUserRequest, createOrderRequest)
                .assertThat().body("success", equalTo(true))
                .and()
                .assertThat().body("order.owner.email", equalTo(email))
                .and()
                .statusCode(200);;
    }
    @Test
    @DisplayName("Create new order without authorization")
    @Description("Проверка возможности создания заказа без авторизации")
    public void orderCreateWithoutAuthorization() {
        UserCreateRequest userCreateRequest = new UserCreateRequest(email, password, name);
        ingredients.add("61c0c5a71d1f82001bdaaa6d");
        CreateOrderRequest createOrderRequest = new CreateOrderRequest(ingredients);
        UserClient userClient = new UserClient();
        OrderClient orderClient = new OrderClient();
        userClient.userCreate(userCreateRequest);
        orderClient.orderCreate(createOrderRequest)
                .assertThat().body("success", equalTo(true))
                .and()
                .assertThat().body("order.number", isA(Integer.class))
                .and()
                .statusCode(200);;
    }
    @Test
    @DisplayName("Create new order after authorization without ingredients")
    @Description("Проверка не возможности создания заказа после авторизации без ингредиентов")
    public void orderCreateWithAuthorizationWithoutIngredients() {
        skipDeleteUser = true;
        UserCreateRequest userCreateRequest = new UserCreateRequest(email, password, name);
        LoginUserRequest loginUserRequest = new LoginUserRequest(email, password);
        CreateOrderRequest createOrderRequest = new CreateOrderRequest(ingredients);
        UserClient userClient = new UserClient();
        OrderClient orderClient = new OrderClient();
        userClient.userCreate(userCreateRequest);
        orderClient.orderCreateAfterLogin(loginUserRequest, createOrderRequest)
                .assertThat().body("success", equalTo(false))
                .and()
                .statusCode(400);;
    }
    @Test
    @DisplayName("Create new order after authorization with invalid ingredient")
    @Description("Проверка не возможности создания заказа после авторизации без ингредиентов")
    public void orderCreateWithAuthorizationWithWrongIngredients() {
        UserCreateRequest userCreateRequest = new UserCreateRequest(email, password, name);
        LoginUserRequest loginUserRequest = new LoginUserRequest(email, password);
        ingredients.add("61c0c5a71d1f82001bdaaa6d");
        ingredients.add("wrongIngredients");
        CreateOrderRequest createOrderRequest = new CreateOrderRequest(ingredients);
        UserClient userClient = new UserClient();
        OrderClient orderClient = new OrderClient();
        userClient.userCreate(userCreateRequest);
        orderClient.orderCreateAfterLogin(loginUserRequest, createOrderRequest)
                .assertThat().statusCode(500);
    }

}
