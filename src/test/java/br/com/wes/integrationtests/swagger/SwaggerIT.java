package br.com.wes.integrationtests.swagger;

import br.com.wes.integrationtests.AbstractIT;
import br.com.wes.integrationtests.TestConstants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class SwaggerIT extends AbstractIT {

    @Test
    @DisplayName("Should display swagger ui page")
    public void shouldDisplaySwaggerUiPage() {
        var content =
                given()
                        .port(TestConstants.SERVER_PORT).basePath("/swagger-ui/index.html")
                        .when().get()
                        .then().statusCode(HttpStatus.OK.value())
                        .extract().body().asString();
        assertTrue(content.contains("Swagger UI"));
    }
}