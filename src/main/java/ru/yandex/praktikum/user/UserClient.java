package ru.yandex.praktikum.user;

import io.restassured.response.ValidatableResponse;
import ru.yandex.praktikum.model.UserStellar;

import static io.restassured.RestAssured.given;

public class UserClient extends UserRestClient {

    private static final String USER_URI = BASE_URI + "/api";

    public ValidatableResponse createUser(UserStellar userStellar) {
        return given()
                .spec(getBaseReqSpec())
                .body(userStellar)
                .when()
                .post(USER_URI + "/auth/register")
                .then();
    }

    public ValidatableResponse loginUser(UserStellar userStellar) {
        return given()
                .spec(getBaseReqSpec())
                .body(userStellar)
                .when()
                .post(USER_URI + "/auth/login")
                .then();
    }

    public ValidatableResponse deleteUser(String accessToken) {
        return given()
                .spec(getBaseReqSpec())
                .auth().oauth2(accessToken)
                .delete(USER_URI + "/auth/user")
                .then();
    }

    public ValidatableResponse updateUser(String accessToken, UserStellar userStellar) {
        return given()
                .spec(getBaseReqSpec())
                .body(userStellar)
                .auth().oauth2(accessToken)
                .patch(USER_URI + "/auth/user")
                .then();
    }

    public ValidatableResponse updateUserNotAuth(UserStellar userStellar) {
        return given()
                .spec(getBaseReqSpec())
                .body(userStellar)
                .patch(USER_URI + "/auth/user")
                .then();
    }

}
