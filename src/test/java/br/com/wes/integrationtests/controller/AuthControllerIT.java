package br.com.wes.integrationtests.controller;

import br.com.wes.integrationtests.AbstractIT;
import br.com.wes.integrationtests.TestConstants;
import br.com.wes.vo.v1.security.AccountCredentialsVO;
import br.com.wes.vo.v1.security.TokenVO;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthControllerIT extends AbstractIT {

    @Test
    @Order(0)
    @DisplayName("Should return forbidden when user not authorized to signin")
    public void shouldReturnForbiddenWhenUserNotAuthorizedToSignin() {
        var credentials = new AccountCredentialsVO();

        given()
                .port(TestConstants.SERVER_PORT).basePath("/auth/signin")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(credentials)
                .when().post()
                .then().statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @Order(1)
    @DisplayName("Should signin user with success")
    public void shouldSigninUserWithSuccess() {
        var username = "usertest";
        var password = "test123";
        var credentials = new AccountCredentialsVO(username, password);

        var tokenVO = given()
                .port(TestConstants.SERVER_PORT).basePath("/auth/signin")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(credentials)
                .when().post()
                .then().statusCode(HttpStatus.OK.value())
                .extract().body().as(TokenVO.class);

        assertNotNull(tokenVO.getAccessToken());
        assertNotNull(tokenVO.getRefreshToken());
    }

    @Test
    @Order(2)
    @DisplayName("Should refresh user token with success")
    public void shouldRefreshUserTokenWithSuccess() {
        var username = "usertest";
        var password = "test123";
        var credentials = new AccountCredentialsVO(username, password);

        var tokenVO = given()
                .port(TestConstants.SERVER_PORT).basePath("/auth/signin")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(credentials)
                .when().post()
                .then().statusCode(HttpStatus.OK.value())
                .extract().body().as(TokenVO.class);
        assertNotNull(tokenVO.getAccessToken());
        assertNotNull(tokenVO.getRefreshToken());

        var refreshTokenVO = given()
                .port(TestConstants.SERVER_PORT).basePath("/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(TestConstants.HEADER_PARAM_AUTHORIZATION, "Bearer " + tokenVO.getRefreshToken())
                .pathParam("username", tokenVO.getUsername())
                .when().put("{username}")
                .then().statusCode(HttpStatus.OK.value())
                .extract().body().as(TokenVO.class);

        assertNotNull(refreshTokenVO.getAccessToken());
        assertNotNull(refreshTokenVO.getRefreshToken());
    }
}
