package ru.yandex.praktikum.order;

import io.restassured.response.ValidatableResponse;
import ru.yandex.praktikum.model.OrderStellar;

import static io.restassured.RestAssured.given;

public class OrderClient extends OrderRestClient {
    private static final String USER_URI = BASE_URI + "/api";

    public ValidatableResponse orderWithoutAuth(OrderStellar orderStellar) {
        return given()
                .spec(getBaseReqSpec())
                .body(orderStellar)
                .post(USER_URI + "/orders")
                .then();

    }

    public ValidatableResponse orderWithAuth(String accessToken, OrderStellar orderStellar) {
        return given()
                .spec(getBaseReqSpec())
                .body(orderStellar)
                .auth().oauth2(accessToken)
                .post(USER_URI + "/orders")
                .then();
    }

    public ValidatableResponse getOrderUserAuth(String accessToken) {
        return given()
                .spec(getBaseReqSpec())
                .auth().oauth2(accessToken)
                .get(USER_URI + "/orders")
                .then();
    }

    public ValidatableResponse getOrderUserNotAuth() {
        return given()
                .spec(getBaseReqSpec())
                .get(USER_URI + "/orders")
                .then();
    }
}
