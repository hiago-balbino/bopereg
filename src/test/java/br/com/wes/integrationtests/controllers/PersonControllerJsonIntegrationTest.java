package br.com.wes.integrationtests.controllers;

import br.com.wes.integrationtests.AbstractIntegrationTest;
import br.com.wes.integrationtests.TestConstants;
import br.com.wes.integrationtests.vo.PersonVOIntegrationTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
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
import org.springframework.http.HttpStatus;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PersonControllerJsonIntegrationTest extends AbstractIntegrationTest {

    private RequestSpecification specificationValidOrigin;
    private RequestSpecification specificationInvalidOrigin;
    @Autowired
    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        specificationValidOrigin = new RequestSpecBuilder()
                .setPort(TestConstants.SERVER_PORT)
                .setBasePath("/api/person/v1")
                .addHeader(TestConstants.HEADER_PARAM_ORIGIN, TestConstants.VALID_ORIGIN)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();
        specificationInvalidOrigin = new RequestSpecBuilder()
                .setPort(TestConstants.SERVER_PORT)
                .setBasePath("/api/person/v1")
                .addHeader(TestConstants.HEADER_PARAM_ORIGIN, TestConstants.INVALID_ORIGIN)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();

        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    @Test
    @Order(1)
    public void shouldPerformPostRequestToPersonWithSuccess() throws JsonProcessingException {
        var person = mockPersonVOIntegrationTest();
        var contentBody = given().spec(specificationValidOrigin)
                .contentType(TestConstants.CONTENT_TYPE_JSON)
                .body(person)
                .when().post()
                .then().statusCode(HttpStatus.OK.value())
                .extract().body().asString();

        PersonVOIntegrationTest personPersisted = mapper.readValue(contentBody, PersonVOIntegrationTest.class);

        assertNotNull(personPersisted);
        assertNotNull(personPersisted.getId());
        assertNotNull(personPersisted.getFirstName());
        assertNotNull(personPersisted.getLastName());
        assertNotNull(personPersisted.getGender());
        assertNotNull(personPersisted.getAddress());
        assertTrue(personPersisted.getId() > 0);
        assertEquals("Wes", personPersisted.getFirstName());
        assertEquals("B.", personPersisted.getLastName());
        assertEquals("Male", personPersisted.getGender());
        assertEquals("Anywhere", personPersisted.getAddress());
    }

    @Test
    @Order(2)
    public void shouldReturnInvalidCorsWhenPerformingPostToPersonWithInvalidOrigin() {
        var person = mockPersonVOIntegrationTest();
        var contentBody = given().spec(specificationInvalidOrigin)
                .contentType(TestConstants.CONTENT_TYPE_JSON)
                .body(person)
                .when().post()
                .then().statusCode(HttpStatus.FORBIDDEN.value())
                .extract().body().asString();

        assertNotNull(contentBody);
        assertEquals("Invalid CORS request", contentBody);
    }

    @Test
    @Order(3)
    public void shouldPerformGetRequestToFindPersonWithSuccess() throws JsonProcessingException {
        var contentBodyFindAll = given().spec(specificationValidOrigin)
                .contentType(TestConstants.CONTENT_TYPE_JSON)
                .when().get()
                .then().statusCode(HttpStatus.OK.value())
                .extract().body().asString();

        List<PersonVOIntegrationTest> people = mapper.readValue(contentBodyFindAll, new TypeReference<>() {
        });
        assertFalse(people.isEmpty());

        PersonVOIntegrationTest personToFetch = people.get(0);
        var contentBodyFindById = given().spec(specificationValidOrigin)
                .contentType(TestConstants.CONTENT_TYPE_JSON)
                .pathParam("id", personToFetch.getId())
                .when().get("{id}")
                .then().statusCode(HttpStatus.OK.value())
                .extract().body().asString();

        PersonVOIntegrationTest personPersisted = mapper.readValue(contentBodyFindById, PersonVOIntegrationTest.class);
        assertNotNull(personPersisted);
        assertNotNull(personPersisted.getId());
        assertNotNull(personPersisted.getFirstName());
        assertNotNull(personPersisted.getLastName());
        assertNotNull(personPersisted.getGender());
        assertNotNull(personPersisted.getAddress());
        assertEquals(personToFetch, personPersisted);
    }

    @Test
    @Order(4)
    public void shouldReturnInvalidCorsWhenPerformingGetToFindPersonWithInvalidOrigin() {
        var contentBody = given().spec(specificationInvalidOrigin)
                .contentType(TestConstants.CONTENT_TYPE_JSON)
                .pathParam("id", 1)
                .when().get("{id}")
                .then().statusCode(HttpStatus.FORBIDDEN.value())
                .extract().body().asString();

        assertNotNull(contentBody);
        assertEquals("Invalid CORS request", contentBody);
    }

    @Test
    @Order(5)
    public void shouldPerformDeleteRequestToRemovePersonWithSuccess() throws JsonProcessingException {
        var contentBodyFindAll = given().spec(specificationValidOrigin)
                .contentType(TestConstants.CONTENT_TYPE_JSON)
                .when().get()
                .then().statusCode(HttpStatus.OK.value())
                .extract().body().asString();
        List<PersonVOIntegrationTest> people = mapper.readValue(contentBodyFindAll, new TypeReference<>() {
        });
        assertFalse(people.isEmpty());

        PersonVOIntegrationTest personToDelete = people.get(0);
        given().spec(specificationValidOrigin)
                .contentType(TestConstants.CONTENT_TYPE_JSON)
                .pathParam("id", personToDelete.getId())
                .when().delete("{id}")
                .then().statusCode(HttpStatus.NO_CONTENT.value());
        given().spec(specificationValidOrigin)
                .contentType(TestConstants.CONTENT_TYPE_JSON)
                .pathParam("id", personToDelete.getId())
                .when().get("{id}")
                .then().statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @Order(6)
    public void shouldReturnInvalidCorsWhenPerformingDeleteToRemovePersonWithInvalidOrigin() {
        var contentBody = given().spec(specificationInvalidOrigin)
                .contentType(TestConstants.CONTENT_TYPE_JSON)
                .pathParam("id", 1)
                .when().delete("{id}")
                .then().statusCode(HttpStatus.FORBIDDEN.value())
                .extract().body().asString();

        assertNotNull(contentBody);
        assertEquals("Invalid CORS request", contentBody);
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
