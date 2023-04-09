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
import ru.yandex.praktikum.user.UserClient;

import java.util.ArrayList;

import static java.net.HttpURLConnection.*;
import static org.hamcrest.CoreMatchers.equalTo;

public class CreateOrderTest {
    private UserClient userClient;
    @Before
    public void setUp(){
        userClient = new UserClient();
    }
    @Test
    @DisplayName("Создание заказа без авторизации. Ответ 200")
    @Description("Post запрос на ручку /api/orders")
    @Step("Создание заказа")
    public void createOrderWithoutAuth(){
        ArrayList<String> ingredients = new ArrayList<>();
        ingredients.add(GeneratorStellar.BUN);
        ingredients.add(GeneratorStellar.FILLING_ONE);
        ingredients.add(GeneratorStellar.FILLING_TWO);
        
        OrderStellar orderStellar = new OrderStellar(ingredients);
        ValidatableResponse response = userClient.orderWithoutAuth(orderStellar)
                .assertThat().statusCode(HTTP_OK);
    }
    @Test
    @DisplayName("Создание заказа без авторизации, c неверным хешем. Ответ 500")
    @Description("Post запрос на ручку /api/orders")
    @Step("Создание заказа")
    public void createOrderWithoutAuthErrorHash(){
        ArrayList<String> ingredients = new ArrayList<>();
        ingredients.add(GeneratorStellar.BAD_BUN);
        ingredients.add(GeneratorStellar.FILLING_ONE);

        OrderStellar orderStellar = new OrderStellar(ingredients);
        ValidatableResponse response = userClient.orderWithoutAuth(orderStellar)
                .assertThat().statusCode(HTTP_INTERNAL_ERROR);
    }

    @Test
    @DisplayName("Создание заказа без авторизации, без ингредиентов. Ответ 500")
    @Description("Post запрос на ручку /api/orders")
    @Step("Создание заказа")
    public void createOrderWithoutAuthNoIngredient(){

        OrderStellar orderStellar = new OrderStellar(null);
        ValidatableResponse response = userClient.orderWithoutAuth(orderStellar)
                .assertThat().statusCode(HTTP_BAD_REQUEST);
        response.assertThat().body("success",equalTo(false))
                .and()
                .body("message",equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с авторизацией. Ответ 200")
    @Description("Post запрос на ручку /api/orders")
    @Step("Создание заказа")
    public void createOrderWithAuth(){
        UserStellar userStellar = new UserStellar(GeneratorStellar.LOGIN, GeneratorStellar.PASSWORD, GeneratorStellar.NAME);
        ValidatableResponse responseCreate = userClient.create(userStellar).assertThat().statusCode(HTTP_OK);
        String accessTokenWithBearer = responseCreate.extract().path("accessToken");
        String accessToken = accessTokenWithBearer.replace("Bearer ","");

        ArrayList<String> ingredients = new ArrayList<>();
        ingredients.add(GeneratorStellar.BUN);
        ingredients.add(GeneratorStellar.FILLING_ONE);
        ingredients.add(GeneratorStellar.FILLING_TWO);

        OrderStellar orderStellar = new OrderStellar(ingredients);
        ValidatableResponse response = userClient.orderWithAuth(accessToken,orderStellar)
                .assertThat().statusCode(HTTP_OK);
        response.assertThat().body("order.owner.name",equalTo(GeneratorStellar.NAME))
                            .and()
                            .body("order.owner.email",equalTo(GeneratorStellar.LOGIN));
    }

    @Test
    @DisplayName("Создание заказа с авторизацией, без ингредиентов. Ответ 400")
    @Description("Post запрос на ручку /api/orders")
    @Step("Создание заказа")
    public void createOrderWithAuthNoIngredient(){
        UserStellar userStellar = new UserStellar(GeneratorStellar.LOGIN, GeneratorStellar.PASSWORD, GeneratorStellar.NAME);
        ValidatableResponse responseCreate = userClient.create(userStellar).assertThat().statusCode(HTTP_OK);
        String accessTokenWithBearer = responseCreate.extract().path("accessToken");
        String accessToken = accessTokenWithBearer.replace("Bearer ","");

        OrderStellar orderStellar = new OrderStellar(null);
        ValidatableResponse response = userClient.orderWithAuth(accessToken,orderStellar)
                .assertThat().statusCode(HTTP_BAD_REQUEST);
        response.assertThat().body("success",equalTo(false))
                .and()
                .body("message",equalTo("Ingredient ids must be provided"));


    }

    @Test
    @DisplayName("Создание заказа с авторизацией с неверным хешем")
    @Description("Post запрос на ручку /api/orders")
    @Step("Создание заказа")
    public void createOrderWithAuthErrorHash(){
        UserStellar userStellar = new UserStellar(GeneratorStellar.LOGIN, GeneratorStellar.PASSWORD, GeneratorStellar.NAME);
        ValidatableResponse responseCreate = userClient.create(userStellar).assertThat().statusCode(HTTP_OK);
        String accessTokenWithBearer = responseCreate.extract().path("accessToken");
        String accessToken = accessTokenWithBearer.replace("Bearer ","");

        ArrayList<String> ingredients = new ArrayList<>();
        ingredients.add(GeneratorStellar.BAD_BUN);
        ingredients.add(GeneratorStellar.FILLING_TWO);

        OrderStellar orderStellar = new OrderStellar(ingredients);
        ValidatableResponse response = userClient.orderWithAuth(accessToken,orderStellar)
                .assertThat().statusCode(HTTP_INTERNAL_ERROR);
    }

    @After
    public void clearData(){
        try{
        UserStellar userStellar = new UserStellar(GeneratorStellar.LOGIN, GeneratorStellar.PASSWORD, GeneratorStellar.NAME);
        ValidatableResponse responseLogin = userClient.loginUser(userStellar);
        String accessTokenWithBearer = responseLogin.extract().path("accessToken");
        String accessToken = accessTokenWithBearer.replace("Bearer ","");

        userClient.deleteUser(accessToken);
        }catch (Exception e){
            System.out.println("Завершилось без удаления");
        }
    }


}
