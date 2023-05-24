package ru.yandex.praktikum;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.praktikum.model.OrderStellar;
import ru.yandex.praktikum.model.UserStellar;
import ru.yandex.praktikum.order.OrderClient;
import ru.yandex.praktikum.user.UserClient;

import java.util.ArrayList;

import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class GetOrdersUserTest {
    private UserClient userClient;
    private OrderClient orderClient;

    @Before
    public void setUp() {
        userClient = new UserClient();
        orderClient = new OrderClient();
    }

    @Test
    @DisplayName("Получение заказов авторизованного пользователя. Ответ 200")
    @Description("Get запрос на ручку api/orders")
    @Step("Получение заказов")
    public void getOrderAuthUser() {
        UserStellar userStellar = new UserStellar(GeneratorStellar.LOGIN, GeneratorStellar.PASSWORD, GeneratorStellar.NAME);
        ValidatableResponse responseCreate = userClient.createUser(userStellar).assertThat().statusCode(HTTP_OK);
        String accessTokenWithBearer = responseCreate.extract().path("accessToken");
        String accessToken = accessTokenWithBearer.replace("Bearer ", "");
        ArrayList<String> ingredients = new ArrayList<>();
        ingredients.add(GeneratorStellar.BUN);
        ingredients.add(GeneratorStellar.FILLING_ONE);
        ingredients.add(GeneratorStellar.FILLING_TWO);
        OrderStellar orderStellar = new OrderStellar(ingredients);
        ValidatableResponse response = orderClient.orderWithAuth(accessToken, orderStellar)
                .assertThat().statusCode(HTTP_OK);
        ValidatableResponse responseGetOrders = orderClient.getOrderUserAuth(accessToken)
                .assertThat().statusCode(HTTP_OK);
        responseGetOrders.assertThat().body("success", equalTo(true))
                .and()
                .body("orders", notNullValue());
    }

    @Test
    @DisplayName("Получение заказов не авторизованного пользователя. Ответ 401")
    @Description("Get запрос на ручку api/orders")
    @Step("Получение заказов")
    public void getOrderNotAuthUser() {
        UserStellar userStellar = new UserStellar(GeneratorStellar.LOGIN, GeneratorStellar.PASSWORD, GeneratorStellar.NAME);
        ValidatableResponse responseCreate = userClient.createUser(userStellar).assertThat().statusCode(HTTP_OK);
        String accessTokenWithBearer = responseCreate.extract().path("accessToken");
        String accessToken = accessTokenWithBearer.replace("Bearer ", "");
        ArrayList<String> ingredients = new ArrayList<>();
        ingredients.add(GeneratorStellar.BUN);
        ingredients.add(GeneratorStellar.FILLING_ONE);
        ingredients.add(GeneratorStellar.FILLING_TWO);
        OrderStellar orderStellar = new OrderStellar(ingredients);
        ValidatableResponse response = orderClient.orderWithAuth(accessToken, orderStellar)
                .assertThat().statusCode(HTTP_OK);
        ValidatableResponse responseGetOrders = orderClient.getOrderUserNotAuth()
                .assertThat().statusCode(HTTP_UNAUTHORIZED);
        responseGetOrders.assertThat().body("success", equalTo(false))
                .and()
                .body("message", equalTo("You should be authorised"));
    }

    @After
    public void clearData() {
        try {
            UserStellar userStellar = new UserStellar(GeneratorStellar.LOGIN, GeneratorStellar.PASSWORD, GeneratorStellar.NAME);
            ValidatableResponse responseLogin = userClient.loginUser(userStellar);
            String accessTokenWithBearer = responseLogin.extract().path("accessToken");
            String accessToken = accessTokenWithBearer.replace("Bearer ", "");
            userClient.deleteUser(accessToken);
        } catch (Exception e) {
            System.out.println("Завершилось без удаления");
        }
    }
}
