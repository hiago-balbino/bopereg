package br.com.wes.integrationtests.controllers.withJson;

import br.com.wes.integrationtests.AbstractIntegrationTest;
import br.com.wes.integrationtests.TestConstants;
import br.com.wes.integrationtests.vo.PersonVOIntegrationTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PersonControllerJsonIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    @Test
    @Order(1)
    public void shouldPerformPostRequestToPersonResourceWithSuccess() throws JsonProcessingException {
        var person = mockPersonVOIntegrationTest();
        RequestSpecification specification = new RequestSpecBuilder()
                .setPort(TestConstants.SERVER_PORT)
                .setBasePath("/person/v1")
                .addHeader(TestConstants.HEADER_PARAM_ORIGIN, "https://thewes.com.br")
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();
        var contentBody = given().spec(specification)
                .contentType(TestConstants.CONTENT_TYPE_JSON)
                .body(person)
                .when().post()
                .then().statusCode(200)
                .extract().body().asString();

        PersonVOIntegrationTest personCreated = mapper.readValue(contentBody, PersonVOIntegrationTest.class);

        assertNotNull(personCreated);
        assertNotNull(personCreated.getId());
        assertNotNull(personCreated.getFirstName());
        assertNotNull(personCreated.getLastName());
        assertNotNull(personCreated.getGender());
        assertNotNull(personCreated.getAddress());
        assertTrue(personCreated.getId() > 0);
        assertEquals("Wes", personCreated.getFirstName());
        assertEquals("B.", personCreated.getLastName());
        assertEquals("Male", personCreated.getGender());
        assertEquals("Anywhere", personCreated.getAddress());
    }

    private PersonVOIntegrationTest mockPersonVOIntegrationTest() {
        PersonVOIntegrationTest person = new PersonVOIntegrationTest();
        person.setFirstName("Wes");
        person.setLastName("B.");
        person.setGender("Male");
        person.setAddress("Anywhere");
        return person;
    }
}
