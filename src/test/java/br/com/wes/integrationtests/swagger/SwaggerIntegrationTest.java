package br.com.wes.integrationtests.swagger;

import br.com.wes.integrationtests.AbstractIntegrationTest;
import br.com.wes.integrationtests.TestConstants;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class SwaggerIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void shouldDisplaySwaggerUiPage() {
        var content =
                given()
                        .port(TestConstants.SERVER_PORT)
                        .basePath("/swagger-ui/index.html")
                        .when().get()
                        .then().statusCode(200)
                        .extract().body().asString();
        assertTrue(content.contains("Swagger UI"));
    }
}