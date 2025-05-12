package clients;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import models.CreateOrderRequest;
import models.LoginUserRequest;
import models.LoginUserResponse;

import static clients.UserClient.requestSpecification;
import static endPoint.EndPoint.*;


public class OrderClient {

    @Step("Create new order without authorization")
    public ValidatableResponse orderCreate(CreateOrderRequest createOrderRequest) {
        return requestSpecification()
                .body(createOrderRequest)
                .post(GET_USER_ORDERS)
                .then();
    }
    @Step("Create new order after authorization")
    public ValidatableResponse orderCreateAfterLogin(LoginUserRequest loginUserRequest, CreateOrderRequest createOrderRequest) {
        UserClient userSteps = new UserClient();
        Response response = userSteps.userLogin(loginUserRequest)
                .extract().response();
        LoginUserResponse userLoginResponse = response.as(LoginUserResponse.class);
        String accessToken = userLoginResponse.getAccessToken();
        return requestSpecification()
                .header("Authorization", accessToken)
                .body(createOrderRequest)
                .post(GET_USER_ORDERS)
                .then();
    }
    @Step("Get orders without authorization")
    public ValidatableResponse orderList() {
        return requestSpecification()
                .get(GET_USER_ORDERS)
                .then();
    }
    @Step("Get orders after authorization")
    public ValidatableResponse orderListAfterLogin(LoginUserRequest loginUserRequest) {
        UserClient userSteps = new UserClient();
        Response response = userSteps.userLogin(loginUserRequest)
                .extract().response();
        LoginUserResponse userLoginResponse = response.as(LoginUserResponse.class);
        String accessToken = userLoginResponse.getAccessToken();
        return requestSpecification()
                .header("Authorization", accessToken)
                .get(GET_USER_ORDERS)
                .then();
    }


}
