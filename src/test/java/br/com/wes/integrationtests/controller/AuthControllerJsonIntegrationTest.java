package br.com.wes.integrationtests.controller;

import br.com.wes.integrationtests.AbstractIntegrationTest;
import br.com.wes.integrationtests.TestConstants;
import br.com.wes.integrationtests.vo.AccountCredentialsVOIntegrationTest;
import br.com.wes.integrationtests.vo.TokenVOIntegrationTest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthControllerJsonIntegrationTest extends AbstractIntegrationTest {

    @Test
    @Order(1)
    public void shouldSigninUserWithSuccess() {
        var username = System.getenv("USERNAME");
        var password = System.getenv("PASSWORD");
        var credentials = new AccountCredentialsVOIntegrationTest(username, password);

        var tokenVO = given()
                .port(TestConstants.SERVER_PORT).basePath("/auth/signin")
                .contentType(TestConstants.CONTENT_TYPE_JSON)
                .body(credentials)
                .when().post()
                .then().statusCode(HttpStatus.OK.value())
                .extract().body().as(TokenVOIntegrationTest.class);

        assertNotNull(tokenVO.getAccessToken());
        assertNotNull(tokenVO.getRefreshToken());
    }

    @Test
    @Order(2)
    public void shouldRefreshUserTokenWithSuccess() {
        var username = System.getenv("USERNAME");
        var password = System.getenv("PASSWORD");
        var credentials = new AccountCredentialsVOIntegrationTest(username, password);

        var tokenVO = given()
                .port(TestConstants.SERVER_PORT).basePath("/auth/signin")
                .contentType(TestConstants.CONTENT_TYPE_JSON)
                .body(credentials)
                .when().post()
                .then().statusCode(HttpStatus.OK.value())
                .extract().body().as(TokenVOIntegrationTest.class);
        assertNotNull(tokenVO.getAccessToken());
        assertNotNull(tokenVO.getRefreshToken());

        var refreshTokenVO = given()
                .port(TestConstants.SERVER_PORT).basePath("/auth/refresh")
                .contentType(TestConstants.CONTENT_TYPE_JSON)
                .pathParam("username", tokenVO.getUsername())
                .header(TestConstants.HEADER_PARAM_AUTHORIZATION, "Bearer " + tokenVO.getRefreshToken())
                .when().put("{username}")
                .then().statusCode(HttpStatus.OK.value())
                .extract().body().as(TokenVOIntegrationTest.class);

        assertNotNull(refreshTokenVO.getAccessToken());
        assertNotNull(refreshTokenVO.getRefreshToken());
    }
}
