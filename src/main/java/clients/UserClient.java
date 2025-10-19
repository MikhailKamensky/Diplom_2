package clients;

import endpoint.EndPoint;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import models.LoginUserRequest;
import models.LoginUserResponse;
import models.UserCreateRequest;

import static endpoint.EndPoint.*;
import static io.restassured.RestAssured.given;

public class UserClient {
    public static RequestSpecification requestSpecification() {
        return given().log().all()
                .contentType(ContentType.JSON)
                .baseUri(EndPoint.BASE_URL);
    }
    @Step("Сreate new user ")
    public ValidatableResponse userCreate(UserCreateRequest userCreateRequest) {
        return requestSpecification()
                .body(userCreateRequest)
                .post(CREATE_USER)
                .then();
    }
    @Step("User authorization")
    public ValidatableResponse userLogin(LoginUserRequest loginUserRequest) {
        return requestSpecification()
                .body(loginUserRequest)
                .post(LOGIN_USER)
                .then();
    }
    @Step("Edit user data without authorization")
    public ValidatableResponse userEdit(UserCreateRequest userCreateRequest) {
        return requestSpecification()
                .body(userCreateRequest)
                .patch(GET_OR_UPDATE_USER_DATA)
                .then();
    }
    @Step("Edit user data after authorization")
    public ValidatableResponse userEditAfterLogin(LoginUserRequest loginUserRequest, UserCreateRequest userCreateRequest) {
        ValidatableResponse loginResponse = userLogin(loginUserRequest);
        String accessToken = loginResponse.extract().path("accessToken");

        if (accessToken == null) {
            throw new IllegalStateException("Authorization failed, no access token received. Response: " +
                    loginResponse.extract().asString());
        }

        return userEditWithToken(accessToken, userCreateRequest);
    }

    @Step("Edit user data with token")
    public ValidatableResponse userEditWithToken(String accessToken, UserCreateRequest userCreateRequest) {
        return requestSpecification()
                .header("Authorization", accessToken)
                .body(userCreateRequest)
                .patch(GET_OR_UPDATE_USER_DATA)
                .then();
    }

    @Step("Delete user data without authorization")
    public ValidatableResponse userDelete(String accessToken) {
        return requestSpecification()
                .header("Authorization", accessToken)
                .delete(GET_OR_UPDATE_USER_DATA)
                .then();
    }
    @Step("Delete user data after authorization")
    public ValidatableResponse userDeleteAfterLogin(LoginUserRequest loginUserRequest) {
        ValidatableResponse loginResponse = userLogin(loginUserRequest);
        String accessToken = loginResponse.extract().path("accessToken");

        if (accessToken == null) {
            throw new IllegalStateException("Authorization failed, no access token received. Response: " +
                    loginResponse.extract().asString());
        }

        return userDelete(accessToken);
    }
}
