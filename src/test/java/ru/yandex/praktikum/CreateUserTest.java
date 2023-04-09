package ru.yandex.praktikum;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.praktikum.model.UserStellar;
import ru.yandex.praktikum.user.UserClient;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static java.net.HttpURLConnection.*;
import static org.hamcrest.CoreMatchers.*;

public class CreateUserTest {
    private UserClient userClient;


    @Before
    public void setUp(){
        userClient = new UserClient();
    }

    @After
    @Step("Постусловие.Удаление пользователя")
    public void clearData(){
        try {
            UserStellar userStellar = new UserStellar(GeneratorStellar.LOGIN, GeneratorStellar.PASSWORD, GeneratorStellar.NAME);
            ValidatableResponse responseLogin = userClient.loginUser(userStellar);
            String accessTokenWithBearer = responseLogin.extract().path("accessToken");
            String accessToken = accessTokenWithBearer.replace("Bearer ","");

            ValidatableResponse responseDelete = userClient.deleteUser(accessToken);
            System.out.println("удален");
        } catch (Exception e){
            System.out.println("Пользователь не удалился. Возможно ошибка при создании");
        }
    }


    @Test
    @DisplayName("Создать уникального пользователя. Ответ 200 ОК")
    @Description("Post запрос на ручку /api/v1/courier")
    @Step("Основной шаг - создание пользователя")
    public void CreateUniqueUserTest(){
        UserStellar userStellar = new UserStellar(GeneratorStellar.LOGIN, GeneratorStellar.PASSWORD, GeneratorStellar.NAME);
        ValidatableResponse response = userClient.create(userStellar)
                .assertThat().statusCode(HTTP_OK);
    }

    @Test
    @DisplayName("Создать уникального пользователя. Проверка body")
    @Description("Post запрос на ручку /api/v1/courier")
    @Step("Основной шаг - создание пользователя")
    public void CreateUniqueUserCheckBodyTest(){
        UserStellar userStellar = new UserStellar(GeneratorStellar.LOGIN, GeneratorStellar.PASSWORD, GeneratorStellar.NAME);
        ValidatableResponse response = userClient.create(userStellar)
                .assertThat().body("user.email",equalTo(GeneratorStellar.LOGIN))
                .and()
                .assertThat().body("user.name",equalTo(GeneratorStellar.NAME));
        response.assertThat().body("accessToken",startsWith("Bearer "));
        response.assertThat().body("refreshToken",notNullValue());
        response.assertThat().body("success",equalTo(true));
    }

    @Test
    @DisplayName("Создать пользователя, который уже зарегистрирован. Ответ 403 Forbidden")
    @Description("Post запрос на ручку /api/v1/courier")
    @Step("Основной шаг - создание пользователя")
    public void CreateRegisteredUserTest(){
        UserStellar userStellar = new UserStellar(GeneratorStellar.LOGIN, GeneratorStellar.PASSWORD, GeneratorStellar.NAME);
        ValidatableResponse response = userClient.create(userStellar);
        ValidatableResponse responseTwo = userClient.create(userStellar)
                .assertThat().statusCode(HTTP_FORBIDDEN);
    }


    @Test
    @DisplayName("Создать пользователя, который уже зарегистрирован. Проверка body")
    @Description("Post запрос на ручку /api/v1/courier")
    @Step("Основной шаг - создание пользователя")
    public void CreateRegisteredUserCheckBodyTest(){
        UserStellar userStellar = new UserStellar(GeneratorStellar.LOGIN, GeneratorStellar.PASSWORD, GeneratorStellar.NAME);
        ValidatableResponse response = userClient.create(userStellar);
        ValidatableResponse responseTwo = userClient.create(userStellar)
                .assertThat().body("success",equalTo(false))
                .and()
                .body("message",equalTo("User already exists"));
    }


    @Test
    @DisplayName("создать пользователя и не заполнить пароль. Ответ 403")
    @Description("Post запрос на ручку /api/v1/courier")
    @Step("Основной шаг - создание пользователя")
    public void CreateUserWithoutPasswordTest(){
        UserStellar userStellar = new UserStellar(GeneratorStellar.LOGIN,null, GeneratorStellar.NAME);
        ValidatableResponse response = userClient.create(userStellar)
                .assertThat().statusCode(HTTP_FORBIDDEN);
        response.assertThat().body("success",equalTo(false))
                .and().body("message",equalTo("Email, password and name are required fields"));
    }
    @Test
    @DisplayName("создать пользователя и не заполнить имя почты. Ответ 403")
    @Description("Post запрос на ручку /api/v1/courier")
    @Step("Основной шаг - создание пользователя")
    public void CreateUserWithoutEmailTest(){
        UserStellar userStellar = new UserStellar(null, GeneratorStellar.PASSWORD, GeneratorStellar.NAME);
        ValidatableResponse response = userClient.create(userStellar)
                .assertThat().statusCode(HTTP_FORBIDDEN);
        response.assertThat().body("success",equalTo(false))
                .and().body("message",equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("создать пользователя и не заполнить имя пользователя. Ответ 403")
    @Description("Post запрос на ручку /api/v1/courier")
    @Step("Основной шаг - создание пользователя")
    public void CreateUserWithoutNameTest(){
        UserStellar userStellar = new UserStellar(GeneratorStellar.NAME, GeneratorStellar.PASSWORD,null);
        ValidatableResponse response = userClient.create(userStellar)
                .assertThat().statusCode(HTTP_FORBIDDEN);
        response.assertThat().body("success",equalTo(false))
                .and().body("message",equalTo("Email, password and name are required fields"));
    }

}
