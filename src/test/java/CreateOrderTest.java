import clients.OrderClient;
import clients.UserClient;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import models.CreateOrderRequest;
import models.LoginUserRequest;
import models.LoginUserResponse;
import models.UserCreateRequest;
import org.junit.After;
import org.junit.Before;
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
    private  UserCreateRequest userCreateRequest;
    private String accessToken;

    @Before
    public void createUser() {
        userCreateRequest = new UserCreateRequest(email, password, name);
        UserClient userClient = new UserClient();
        userClient.userCreate(userCreateRequest);

        // Login and store the access token
        LoginUserRequest loginUserRequest = new LoginUserRequest(email, password);
        LoginUserResponse loginResponse = userClient.userLogin(loginUserRequest)
                .extract().as(LoginUserResponse.class);
        this.accessToken = loginResponse.getAccessToken();
    }

    @Test
    @DisplayName("Create new order after authorization")
    @Description("Checking creating order after authorization")
    public void orderCreateWithAuthorizationTest() {
        LoginUserRequest loginUserRequest = new LoginUserRequest(email, password);
        ingredients.add("61c0c5a71d1f82001bdaaa6d");
        CreateOrderRequest createOrderRequest = new CreateOrderRequest(ingredients);
        UserClient userClient = new UserClient();
        OrderClient orderClient = new OrderClient();
        userClient.userCreate(userCreateRequest);
        orderClient.orderCreateAfterLogin(loginUserRequest, createOrderRequest)
                .assertThat().statusCode(200)
                .and()
                .body("success", equalTo(true))
                .and()
                .assertThat().body("order.owner.email", equalTo(email));
    }
    @Test
    @DisplayName("Create new order without authorization")
    @Description("Checking creating order without authorization")
    public void orderCreateWithoutAuthorizationTest() {
        ingredients.add("61c0c5a71d1f82001bdaaa6d");
        CreateOrderRequest createOrderRequest = new CreateOrderRequest(ingredients);
        UserClient userClient = new UserClient();
        OrderClient orderClient = new OrderClient();
        userClient.userCreate(userCreateRequest);
        orderClient.orderCreate(createOrderRequest)
                .assertThat().statusCode(200)
                .and()
                .body("success", equalTo(true))
                .and()
                .assertThat().body("order.number", isA(Integer.class));
    }
    @Test
    @DisplayName("Create new order after authorization without ingredients")
    @Description("Checking creating order after authorization without ingredients")
    public void orderCreateWithAuthorizationWithoutIngredientsTest() {
        skipDeleteUser = true;
        LoginUserRequest loginUserRequest = new LoginUserRequest(email, password);
        CreateOrderRequest createOrderRequest = new CreateOrderRequest(ingredients);
        UserClient userClient = new UserClient();
        OrderClient orderClient = new OrderClient();
        userClient.userCreate(userCreateRequest);
        orderClient.orderCreateAfterLogin(loginUserRequest, createOrderRequest)
                .assertThat()
                .statusCode(400)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("Ingredient ids must be provided"));
    }
    @Test
    @DisplayName("Create new order after authorization with invalid ingredient")
    @Description("Checking of block for creating order after authorization without ingredients")
    public void orderCreateWithAuthorizationWithWrongIngredientsTest() {
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

    @After
    public void cleanUp() {
        if (!skipDeleteUser && accessToken != null) {
            UserClient userClient = new UserClient();
            userClient.userDelete(accessToken);
        }
        ingredients.clear();

    }

}
