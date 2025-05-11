package clients;

import endPoint.EndPoint;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import models.LoginUserRequest;
import models.LoginUserResponse;
import models.UserCreateRequest;

import static endPoint.EndPoint.*;
import static io.restassured.RestAssured.given;

public class UserClient {
    public static RequestSpecification requestSpecification() {
        return given().log().all()
                .contentType(ContentType.JSON)
                .baseUri(EndPoint.BASE_URL);
    }
    @Step("Создание нового пользователя")
    public ValidatableResponse userCreate(UserCreateRequest userCreateRequest) {
        return requestSpecification()
                .body(userCreateRequest)
                .post(CREATE_USER)
                .then();
    }
    @Step("Авторизация пользователя")
    public ValidatableResponse userLogin(LoginUserRequest loginUserRequest) {
        return requestSpecification()
                .body(loginUserRequest)
                .post(LOGIN_USER)
                .then();
    }
    @Step("Изменение данных пользователя без авторизации")
    public ValidatableResponse userEdit(UserCreateRequest userCreateRequest) {
        return requestSpecification()
                .body(userCreateRequest)
                .patch(GET_OR_UPDATE_USER_DATA)
                .then();
    }
    @Step("Изменение данных пользователя после авторизации")
    public ValidatableResponse userEditAfterLogin(LoginUserRequest loginUserRequest, UserCreateRequest userCreateRequest) {
        Response response = userLogin(loginUserRequest)
                .extract().response();
        LoginUserResponse loginUserResponse = response.as(LoginUserResponse.class);
        String accessToken = loginUserResponse.getAccessToken();
        return requestSpecification()
                .header("Authorization", accessToken)
                .body(userCreateRequest)
                .patch(GET_OR_UPDATE_USER_DATA)
                .then();
    }
    @Step("Удаление пользователя без авторизации")
    public ValidatableResponse userDelete(String accessToken) {
        return requestSpecification()
                .header("Authorization", accessToken)
                .delete(GET_OR_UPDATE_USER_DATA)
                .then();
    }
    @Step("Удаление пользователя после авторизации")
    public ValidatableResponse userDeleteAfterLogin(LoginUserRequest loginUserRequest) {
        Response response = userLogin(loginUserRequest)
                .extract().response();
        LoginUserResponse userLoginResponse = response.as(LoginUserResponse.class);
        String accessToken = userLoginResponse.getAccessToken();
        return userDelete(accessToken);
    }

}
