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

import static java.net.HttpURLConnection.*;
import static org.hamcrest.CoreMatchers.equalTo;

public class UpdatingUserDataTest {
    private UserClient userClient;

    @Before
    @Step("Предусловие.Создание пользователя")
    public void setUp() {
        userClient = new UserClient();
        UserStellar userStellar = new UserStellar(GeneratorStellar.LOGIN, GeneratorStellar.PASSWORD, GeneratorStellar.NAME);
        ValidatableResponse response = userClient.createUser(userStellar);
    }

    @After
    @Step("Постусловие.Удаление пользователя")
    public void clearData() {
        try {
            UserStellar userStellarTwo = new UserStellar(GeneratorStellar.LOGIN_TWO, GeneratorStellar.PASSWORD_TWO, GeneratorStellar.NAME_TWO);
            ValidatableResponse responseLogin = userClient.loginUser(userStellarTwo);
            String accessTokenWithBearer = responseLogin.extract().path("accessToken");
            String accessToken = accessTokenWithBearer.replace("Bearer ", "");
            ValidatableResponse responseDelete = userClient.deleteUser(accessToken);
            System.out.println("удален");
        } catch (Exception e) {
            System.out.println("Пользователь не удалился через постусловие");
        }
    }

    @Test
    @DisplayName("Изменение информации о пользвателе с авторизацией. Ответ 200")
    @Description("Patch запрос на ручку /api/auth/user")
    @Step("Основной шаг - Изменение информации")
    public void updateUserWithAuth() {
        UserStellar userStellar = new UserStellar(GeneratorStellar.LOGIN, GeneratorStellar.PASSWORD, GeneratorStellar.NAME);
        ValidatableResponse responseLogin = userClient.loginUser(userStellar);
        String accessTokenWithBearer = responseLogin.extract().path("accessToken");
        String accessToken = accessTokenWithBearer.replace("Bearer ", "");
        UserStellar userStellarTwo = new UserStellar(GeneratorStellar.LOGIN_TWO, GeneratorStellar.PASSWORD_TWO, GeneratorStellar.NAME_TWO);
        ValidatableResponse responseUpdate = userClient.updateUser(accessToken, userStellarTwo)
                .assertThat().statusCode(HTTP_OK);
    }

    @Test
    @DisplayName("Изменение информации о пользвателе с авторизацией. Проверка body")
    @Description("Patch запрос на ручку /api/auth/user")
    @Step("Основной шаг - Изменение информации")
    public void updateUserWithAuthCheckBody() {
        UserStellar userStellar = new UserStellar(GeneratorStellar.LOGIN, GeneratorStellar.PASSWORD, GeneratorStellar.NAME);
        ValidatableResponse responseLogin = userClient.loginUser(userStellar);
        String accessTokenWithBearer = responseLogin.extract().path("accessToken");
        String accessToken = accessTokenWithBearer.replace("Bearer ", "");
        UserStellar userStellarTwo = new UserStellar(GeneratorStellar.LOGIN_TWO, GeneratorStellar.PASSWORD_TWO, GeneratorStellar.NAME_TWO);
        ValidatableResponse responseUpdate = userClient.updateUser(accessToken, userStellarTwo)
                .assertThat().body("success", equalTo(true));
        responseUpdate.assertThat().body("user.email", equalTo(GeneratorStellar.LOGIN_TWO))
                .and()
                .body("user.name", equalTo(GeneratorStellar.NAME_TWO));
    }

    @Test
    @DisplayName("Изменение информации о пользвателе без авторизации. Ответ 401")
    @Description("Patch запрос на ручку /api/auth/user")
    @Step("Основной шаг - Изменение информации")
    public void updateUserWithoutAuth() {
        UserStellar userStellar = new UserStellar(GeneratorStellar.LOGIN, GeneratorStellar.PASSWORD, GeneratorStellar.NAME);
        ValidatableResponse responseLogin = userClient.loginUser(userStellar);
        String accessTokenWithBearer = responseLogin.extract().path("accessToken");
        String accessToken = accessTokenWithBearer.replace("Bearer ", "");
        UserStellar userStellarTwo = new UserStellar(GeneratorStellar.LOGIN_TWO, GeneratorStellar.PASSWORD_TWO, GeneratorStellar.NAME_TWO);
        ValidatableResponse responsePatch = userClient.updateUserNotAuth(userStellarTwo)
                .assertThat().statusCode(HTTP_UNAUTHORIZED);
        userClient.deleteUser(accessToken);
    }

    @Test
    @DisplayName("Изменение информации о пользователе с используемой почтой. Ответ 403")
    @Description("Patch запрос на ручку /api/auth/user")
    @Step("Основной шаг - Изменение информации")
    public void updateUserWithOldEmail() {
        UserStellar userStellar = new UserStellar(GeneratorStellar.LOGIN, GeneratorStellar.PASSWORD, GeneratorStellar.NAME);
        ValidatableResponse responseLogin = userClient.loginUser(userStellar);
        String accessTokenWithBearer = responseLogin.extract().path("accessToken");
        String accessToken = accessTokenWithBearer.replace("Bearer ", "");
        ValidatableResponse responsePatch = userClient.updateUser(accessToken, userStellar);
        responsePatch.assertThat().statusCode(HTTP_FORBIDDEN);
        userClient.deleteUser(accessToken);
    }
}
