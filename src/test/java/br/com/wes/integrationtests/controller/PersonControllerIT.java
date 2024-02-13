package br.com.wes.integrationtests.controller;

import br.com.wes.integrationtests.AbstractIT;
import br.com.wes.integrationtests.TestConstants;
import br.com.wes.integrationtests.vo.PersonVOIT;
import br.com.wes.integrationtests.vo.wrapper.PersonVOITWrapper;
import br.com.wes.vo.v1.security.AccountCredentialsVO;
import br.com.wes.vo.v1.security.TokenVO;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PersonControllerIT extends AbstractIT {

    private static RequestSpecification specification;

    @Autowired
    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    @Test
    @Order(0)
    @DisplayName("Should authorize user to perform person requests on tests")
    public void shouldAuthorizeUserToPerformPersonRequestsOnTests() {
        var username = "usertest";
        var password = "test123";
        var credentials = new AccountCredentialsVO(username, password);

        var accessToken = given()
                .port(TestConstants.SERVER_PORT).basePath("/auth/signin")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(credentials)
                .when().post()
                .then().statusCode(HttpStatus.OK.value())
                .extract().body().as(TokenVO.class)
                .getAccessToken();

        specification = new RequestSpecBuilder()
                .setPort(TestConstants.SERVER_PORT).setBasePath("/api/person/v1")
                .addHeader(TestConstants.HEADER_PARAM_AUTHORIZATION, "Bearer " + accessToken)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();
    }

    @Test
    @Order(1)
    @DisplayName("Should return forbidden when try to perform person request and user not authorized")
    public void shouldReturnForbiddenWhenTryToPerformPersonRequestAndUserNotAuthorized() {
        var specificationWithoutToken = new RequestSpecBuilder()
                .setPort(TestConstants.SERVER_PORT).setBasePath("/api/person/v1")
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();

        given()
                .spec(specificationWithoutToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get()
                .then().statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @Order(2)
    @DisplayName("Should perform post request to person with success")
    public void shouldPerformPostRequestToPersonWithSuccess() throws JsonProcessingException {
        var person = mockPersonVOIntegrationTest();
        var contentBody = given().spec(specification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(TestConstants.HEADER_PARAM_ORIGIN, TestConstants.VALID_ORIGIN)
                .body(person)
                .when().post()
                .then().statusCode(HttpStatus.OK.value())
                .extract().body().asString();

        PersonVOIT personPersisted = mapper.readValue(contentBody, PersonVOIT.class);

        assertNotNull(personPersisted);
        assertNotNull(personPersisted.getId());
        assertNotNull(personPersisted.getFirstName());
        assertNotNull(personPersisted.getLastName());
        assertNotNull(personPersisted.getGender());
        assertNotNull(personPersisted.getAddress());
        assertNotNull(personPersisted.getEnabled());
        assertTrue(personPersisted.getId() > 0);
        assertTrue(personPersisted.getEnabled());
        assertEquals("Wes", personPersisted.getFirstName());
        assertEquals("B.", personPersisted.getLastName());
        assertEquals("Male", personPersisted.getGender());
        assertEquals("Anywhere", personPersisted.getAddress());
    }

    @Test
    @Order(3)
    @DisplayName("Should return invalid cors when performing post to person with invalid origin")
    public void shouldReturnInvalidCorsWhenPerformingPostToPersonWithInvalidOrigin() {
        var person = mockPersonVOIntegrationTest();
        var contentBody = given().spec(specification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(TestConstants.HEADER_PARAM_ORIGIN, TestConstants.INVALID_ORIGIN)
                .body(person)
                .when().post()
                .then().statusCode(HttpStatus.FORBIDDEN.value())
                .extract().body().asString();

        assertNotNull(contentBody);
        assertEquals("Invalid CORS request", contentBody);
    }

    @Test
    @Order(4)
    @DisplayName("Should perform get request to find person with success")
    public void shouldPerformGetRequestToFindPersonWithSuccess() throws JsonProcessingException {
        var contentBodyFindAll = given().spec(specification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(TestConstants.HEADER_PARAM_ORIGIN, TestConstants.VALID_ORIGIN)
                .queryParams("page", 0, "size", 10, "direction", "asc")
                .when().get()
                .then().statusCode(HttpStatus.OK.value())
                .extract().body().asString();

        PersonVOITWrapper wrapper = mapper.readValue(contentBodyFindAll, PersonVOITWrapper.class);
        List<PersonVOIT> people = wrapper.getEmbedded().getPeople();
        assertFalse(people.isEmpty());

        PersonVOIT personToFetch = people.getFirst();
        var contentBodyFindById = given().spec(specification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(TestConstants.HEADER_PARAM_ORIGIN, TestConstants.VALID_ORIGIN)
                .pathParam("id", personToFetch.getId())
                .when().get("{id}")
                .then().statusCode(HttpStatus.OK.value())
                .extract().body().asString();

        PersonVOIT personPersisted = mapper.readValue(contentBodyFindById, PersonVOIT.class);
        assertNotNull(personPersisted);
        assertNotNull(personPersisted.getId());
        assertNotNull(personPersisted.getFirstName());
        assertNotNull(personPersisted.getLastName());
        assertNotNull(personPersisted.getGender());
        assertNotNull(personPersisted.getAddress());
        assertNotNull(personPersisted.getEnabled());
        assertEquals(personToFetch, personPersisted);
    }

    @Test
    @Order(5)
    @DisplayName("Should return invalid cors when performing get to find person with invalid origin")
    public void shouldReturnInvalidCorsWhenPerformingGetToFindPersonWithInvalidOrigin() {
        var contentBody = given().spec(specification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(TestConstants.HEADER_PARAM_ORIGIN, TestConstants.INVALID_ORIGIN)
                .pathParam("id", 1)
                .when().get("{id}")
                .then().statusCode(HttpStatus.FORBIDDEN.value())
                .extract().body().asString();

        assertNotNull(contentBody);
        assertEquals("Invalid CORS request", contentBody);
    }

    @Test
    @Order(6)
    @DisplayName("Should perform delete request to remove person with success")
    public void shouldPerformDeleteRequestToRemovePersonWithSuccess() throws JsonProcessingException {
        var contentBodyFindAll = given().spec(specification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(TestConstants.HEADER_PARAM_ORIGIN, TestConstants.VALID_ORIGIN)
                .queryParams("page", 0, "size", 10, "direction", "asc")
                .when().get()
                .then().statusCode(HttpStatus.OK.value())
                .extract().body().asString();
        PersonVOITWrapper wrapper = mapper.readValue(contentBodyFindAll, PersonVOITWrapper.class);
        List<PersonVOIT> people = wrapper.getEmbedded().getPeople();
        assertFalse(people.isEmpty());

        PersonVOIT personToDelete = people.getFirst();
        given().spec(specification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(TestConstants.HEADER_PARAM_ORIGIN, TestConstants.VALID_ORIGIN)
                .pathParam("id", personToDelete.getId())
                .when().delete("{id}")
                .then().statusCode(HttpStatus.NO_CONTENT.value());
        given().spec(specification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(TestConstants.HEADER_PARAM_ORIGIN, TestConstants.VALID_ORIGIN)
                .pathParam("id", personToDelete.getId())
                .when().get("{id}")
                .then().statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @Order(7)
    @DisplayName("Should return invalid cors when performing delete to remove person with invalid origin")
    public void shouldReturnInvalidCorsWhenPerformingDeleteToRemovePersonWithInvalidOrigin() {
        var contentBody = given().spec(specification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(TestConstants.HEADER_PARAM_ORIGIN, TestConstants.INVALID_ORIGIN)
                .pathParam("id", 1)
                .when().delete("{id}")
                .then().statusCode(HttpStatus.FORBIDDEN.value())
                .extract().body().asString();

        assertNotNull(contentBody);
        assertEquals("Invalid CORS request", contentBody);
    }

    @Test
    @Order(8)
    @DisplayName("Should perform patch request to disable person by id with success")
    public void shouldPerformPatchRequestToDisablePersonByIDWithSuccess() throws JsonProcessingException {
        var contentBodyFindAll = given().spec(specification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(TestConstants.HEADER_PARAM_ORIGIN, TestConstants.VALID_ORIGIN)
                .queryParams("page", 0, "size", 10, "direction", "asc")
                .when().get()
                .then().statusCode(HttpStatus.OK.value())
                .extract().body().asString();
        PersonVOITWrapper wrapper = mapper.readValue(contentBodyFindAll, PersonVOITWrapper.class);
        List<PersonVOIT> people = wrapper.getEmbedded().getPeople();
        assertFalse(people.isEmpty());

        var personToDisable = people.getFirst();
        assertTrue(personToDisable.getEnabled());

        var bodyPersonDisabled = given().spec(specification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(TestConstants.HEADER_PARAM_ORIGIN, TestConstants.VALID_ORIGIN)
                .pathParam("id", personToDisable.getId())
                .when().patch("{id}")
                .then().statusCode(HttpStatus.OK.value())
                .extract().body().asString();
        var personDisabled = mapper.readValue(bodyPersonDisabled, PersonVOIT.class);
        assertFalse(personDisabled.getEnabled());
    }

    @Test
    @Order(9)
    @DisplayName("Should return invalid cors when performing patch to disable person with invalid origin")
    public void shouldReturnInvalidCorsWhenPerformingPatchToDisablePersonWithInvalidOrigin() {
        var contentBody = given().spec(specification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(TestConstants.HEADER_PARAM_ORIGIN, TestConstants.INVALID_ORIGIN)
                .pathParam("id", 1)
                .when().patch("{id}")
                .then().statusCode(HttpStatus.FORBIDDEN.value())
                .extract().body().asString();

        assertNotNull(contentBody);
        assertEquals("Invalid CORS request", contentBody);
    }

    @Test
    @Order(10)
    @DisplayName("Should return people with success when find by name")
    public void shouldReturnPeopleWithSuccessWhenFindByName() throws JsonProcessingException {
        var contentBody = given().spec(specification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(TestConstants.HEADER_PARAM_ORIGIN, TestConstants.VALID_ORIGIN)
                .pathParam("firstName", "Ayr")
                .queryParams("page", 0, "size", 6, "direction", "asc")
                .when().get("/findPeopleByName/{firstName}")
                .then().statusCode(HttpStatus.OK.value())
                .extract().body().asString();

        PersonVOITWrapper wrapper = mapper.readValue(contentBody, PersonVOITWrapper.class);
        var people = wrapper.getEmbedded().getPeople();
        assertFalse(people.isEmpty());

        var person = people.getFirst();
        assertEquals("Ayrton", person.getFirstName());
    }

    @Test
    @Order(11)
    @DisplayName("Should return invalid cors when find people by name with invalid origin")
    public void shouldReturnInvalidCorsWhenFindPeopleByNameWithInvalidOrigin() {
        var contentBody = given().spec(specification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(TestConstants.HEADER_PARAM_ORIGIN, TestConstants.INVALID_ORIGIN)
                .pathParam("firstName", "Ayr")
                .queryParams("page", 0, "size", 6, "direction", "asc")
                .when().get("/findPeopleByName/{firstName}")
                .then().statusCode(HttpStatus.FORBIDDEN.value())
                .extract().body().asString();

        assertNotNull(contentBody);
        assertEquals("Invalid CORS request", contentBody);
    }

    @Test
    @Order(12)
    @DisplayName("Should perform put request to update a person with success")
    public void shouldPerformPutRequestToUpdatePersonWithSuccess() throws JsonProcessingException {
        var contentBodyFindAll = given().spec(specification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(TestConstants.HEADER_PARAM_ORIGIN, TestConstants.VALID_ORIGIN)
                .queryParams("page", 0, "size", 10, "direction", "asc")
                .when().get()
                .then().statusCode(HttpStatus.OK.value())
                .extract().body().asString();
        PersonVOITWrapper wrapper = mapper.readValue(contentBodyFindAll, PersonVOITWrapper.class);
        List<PersonVOIT> people = wrapper.getEmbedded().getPeople();
        assertFalse(people.isEmpty());

        PersonVOIT personToUpdate = people.getFirst();
        personToUpdate.setFirstName("Updated Person");
        given().spec(specification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(TestConstants.HEADER_PARAM_ORIGIN, TestConstants.VALID_ORIGIN)
                .body(personToUpdate)
                .when().put()
                .then().statusCode(HttpStatus.OK.value());

        var contentBodyFindById = given().spec(specification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("id", personToUpdate.getId())
                .when().get("{id}")
                .then().statusCode(HttpStatus.OK.value())
                .extract().body().asString();
        PersonVOIT personUpdated = mapper.readValue(contentBodyFindById, PersonVOIT.class);

        assertEquals(personToUpdate.getFirstName(), personUpdated.getFirstName());
    }

    @Test
    @Order(13)
    @DisplayName("Should return invalid cors when performing put to update person with invalid origin")
    public void shouldReturnInvalidCorsWhenPerformingPutToUpdatePersonWithInvalidOrigin() {
        PersonVOIT person = mockPersonVOIntegrationTest();
        var contentBody = given().spec(specification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(TestConstants.HEADER_PARAM_ORIGIN, TestConstants.INVALID_ORIGIN)
                .body(person)
                .when().put()
                .then().statusCode(HttpStatus.FORBIDDEN.value())
                .extract().body().asString();

        assertNotNull(contentBody);
        assertEquals("Invalid CORS request", contentBody);
    }

    private PersonVOIT mockPersonVOIntegrationTest() {
        PersonVOIT person = new PersonVOIT();
        person.setFirstName("Wes");
        person.setLastName("B.");
        person.setGender("Male");
        person.setAddress("Anywhere");
        person.setEnabled(Boolean.TRUE);
        return person;
    }
}
