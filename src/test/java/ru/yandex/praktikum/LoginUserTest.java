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

import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.*;

public class LoginUserTest {
    private UserClient userClient;


    @Before
    @Step("Предусловие.Создание пользователя")
    public void setUp(){
        userClient = new UserClient();
        UserStellar userStellar = new UserStellar(GeneratorStellar.LOGIN, GeneratorStellar.PASSWORD, GeneratorStellar.NAME);
        ValidatableResponse response = userClient.create(userStellar);
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
    @DisplayName("Логин под существующим пользователем. Ответ 200")
    @Description("Post запрос на ручку /api/auth/login")
    @Step("Основной шаг - логин пользователя")
    public void loginWithUserTrue(){
        UserStellar userStellar = new UserStellar(GeneratorStellar.LOGIN, GeneratorStellar.PASSWORD, GeneratorStellar.NAME);
        ValidatableResponse responseLogin = userClient.loginUser(userStellar)
                .assertThat().statusCode(HTTP_OK);
    }

    @Test
    @DisplayName("Логин под существующим пользователем. Проверка body")
    @Description("Post запрос на ручку /api/auth/login")
    @Step("Основной шаг - логин пользователя")
    public void loginWithUserTrueCheckBody(){
        UserStellar userStellar = new UserStellar(GeneratorStellar.LOGIN, GeneratorStellar.PASSWORD, GeneratorStellar.NAME);
        ValidatableResponse responseLogin = userClient.loginUser(userStellar);
        responseLogin.assertThat().body("success",equalTo(true));
        responseLogin.assertThat().body("accessToken",startsWith("Bearer "))
                .and()
                .body("refreshToken",notNullValue());
        responseLogin.assertThat().body("user.email",equalTo(GeneratorStellar.LOGIN))
                .and()
                .body("user.name",equalTo(GeneratorStellar.NAME));
    }

    @Test
    @DisplayName("Логин под неверным именем почты. Ответ 401")
    @Description("Post запрос на ручку /api/auth/login")
    @Step("Основной шаг - логин пользователя")
    public void loginWithUserFalse(){
        UserStellar userStellar = new UserStellar(GeneratorStellar.LOGIN_TWO, GeneratorStellar.PASSWORD, GeneratorStellar.NAME);
        ValidatableResponse responseLogin = userClient.loginUser(userStellar)
                .assertThat().statusCode(HTTP_UNAUTHORIZED);
    }

    @Test
    @DisplayName("Логин под неверным именем почты. Проверка body")
    @Description("Post запрос на ручку /api/auth/login")
    @Step("Основной шаг - логин пользователя")
    public void loginWithUserFalseCheckBody(){
        UserStellar userStellar = new UserStellar(GeneratorStellar.LOGIN_TWO, GeneratorStellar.PASSWORD, GeneratorStellar.NAME);
        ValidatableResponse responseLogin = userClient.loginUser(userStellar)
                .assertThat().body("success",equalTo(false))
                .and()
                .body("message",equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Логин под неверным паролем. Ответ 401")
    @Description("Post запрос на ручку /api/auth/login")
    @Step("Основной шаг - логин пользователя")
    public void loginWithUserFalsePassword(){
        UserStellar userStellar = new UserStellar(GeneratorStellar.LOGIN, GeneratorStellar.PASSWORD_TWO, GeneratorStellar.NAME);
        ValidatableResponse responseLogin = userClient.loginUser(userStellar)
                .assertThat().statusCode(HTTP_UNAUTHORIZED);
    }

    @Test
    @DisplayName("Логин под неверным паролем. Проверка body")
    @Description("Post запрос на ручку /api/auth/login")
    @Step("Основной шаг - логин пользователя")
    public void loginWithUserFalsePasswordCheckBody(){
        UserStellar userStellar = new UserStellar(GeneratorStellar.LOGIN, GeneratorStellar.PASSWORD_TWO, GeneratorStellar.NAME);
        ValidatableResponse responseLogin = userClient.loginUser(userStellar)
                .assertThat().body("success",equalTo(false))
                .and()
                .body("message",equalTo("email or password are incorrect"));
    }




}
