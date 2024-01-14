package br.com.wes.integrationtests.controller;

import br.com.wes.integrationtests.AbstractIntegrationTest;
import br.com.wes.integrationtests.TestConstants;
import br.com.wes.integrationtests.vo.AccountCredentialsVOIntegrationTest;
import br.com.wes.integrationtests.vo.BookVOIntegrationTest;
import br.com.wes.integrationtests.vo.TokenVOIntegrationTest;
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

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookControllerJsonIntegrationTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;

    @Autowired
    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    @Test
    @Order(0)
    public void shouldAuthorizeUserToPerformPersonRequestsOnTests() {
        var username = System.getenv("USERNAME");
        var password = System.getenv("PASSWORD");
        var credentials = new AccountCredentialsVOIntegrationTest(username, password);

        var accessToken = given()
                .port(TestConstants.SERVER_PORT).basePath("/auth/signin")
                .contentType(TestConstants.CONTENT_TYPE_JSON)
                .body(credentials)
                .when().post()
                .then().statusCode(HttpStatus.OK.value())
                .extract().body().as(TokenVOIntegrationTest.class)
                .getAccessToken();

        specification = new RequestSpecBuilder()
                .setPort(TestConstants.SERVER_PORT).setBasePath("/api/book/v1")
                .addHeader(TestConstants.HEADER_PARAM_AUTHORIZATION, "Bearer " + accessToken)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();
    }

    @Test
    @Order(1)
    public void shouldPerformPostRequestToBookWithSuccess() throws JsonProcessingException {
        var book = mockBookVOIntegrationTest();
        var contentBody = given().spec(specification)
                .contentType(TestConstants.CONTENT_TYPE_JSON)
                .header(TestConstants.HEADER_PARAM_ORIGIN, TestConstants.VALID_ORIGIN)
                .body(book)
                .when().post()
                .then().statusCode(HttpStatus.OK.value())
                .extract().body().asString();

        BookVOIntegrationTest bookPersisted = mapper.readValue(contentBody, BookVOIntegrationTest.class);

        assertNotNull(bookPersisted);
        assertNotNull(bookPersisted.getId());
        assertNotNull(bookPersisted.getAuthor());
        assertNotNull(bookPersisted.getTitle());
        assertNotNull(bookPersisted.getPrice());
        assertNotNull(bookPersisted.getLaunchDate());
        assertTrue(bookPersisted.getId() > 0);
        assertEquals("Wes", bookPersisted.getAuthor());
        assertEquals("The Life of Wes", bookPersisted.getTitle());
        assertEquals(100.0, bookPersisted.getPrice());
        assertEquals(LocalDate.of(2023, Month.DECEMBER, 1), bookPersisted.getLaunchDate());
    }

    @Test
    @Order(2)
    public void shouldReturnInvalidCorsWhenPerformingPostToBookWithInvalidOrigin() {
        var book = mockBookVOIntegrationTest();
        var contentBody = given().spec(specification)
                .contentType(TestConstants.CONTENT_TYPE_JSON)
                .header(TestConstants.HEADER_PARAM_ORIGIN, TestConstants.INVALID_ORIGIN)
                .body(book)
                .when().post()
                .then().statusCode(HttpStatus.FORBIDDEN.value())
                .extract().body().asString();

        assertNotNull(contentBody);
        assertEquals("Invalid CORS request", contentBody);
    }

    @Test
    @Order(3)
    public void shouldPerformGetRequestToFindBookWithSuccess() throws JsonProcessingException {
        var contentBodyFindAll = given().spec(specification)
                .contentType(TestConstants.CONTENT_TYPE_JSON)
                .header(TestConstants.HEADER_PARAM_ORIGIN, TestConstants.VALID_ORIGIN)
                .when().get()
                .then().statusCode(HttpStatus.OK.value())
                .extract().body().asString();

        List<BookVOIntegrationTest> books = mapper.readValue(contentBodyFindAll, new TypeReference<>() {
        });
        assertFalse(books.isEmpty());

        BookVOIntegrationTest bookToFetch = books.get(0);
        var contentBodyFindById = given().spec(specification)
                .contentType(TestConstants.CONTENT_TYPE_JSON)
                .pathParam("id", bookToFetch.getId())
                .when().get("{id}")
                .then().statusCode(HttpStatus.OK.value())
                .extract().body().asString();

        BookVOIntegrationTest bookPersisted = mapper.readValue(contentBodyFindById, BookVOIntegrationTest.class);
        assertNotNull(bookPersisted);
        assertNotNull(bookPersisted.getId());
        assertNotNull(bookPersisted.getAuthor());
        assertNotNull(bookPersisted.getTitle());
        assertNotNull(bookPersisted.getPrice());
        assertNotNull(bookPersisted.getLaunchDate());
        assertEquals(bookToFetch, bookPersisted);
    }

    @Test
    @Order(4)
    public void shouldReturnInvalidCorsWhenPerformingGetToFindBookWithInvalidOrigin() {
        var contentBody = given().spec(specification)
                .contentType(TestConstants.CONTENT_TYPE_JSON)
                .header(TestConstants.HEADER_PARAM_ORIGIN, TestConstants.INVALID_ORIGIN)
                .pathParam("id", 1)
                .when().get("{id}")
                .then().statusCode(HttpStatus.FORBIDDEN.value())
                .extract().body().asString();

        assertNotNull(contentBody);
        assertEquals("Invalid CORS request", contentBody);
    }

    @Test
    @Order(5)
    public void shouldPerformDeleteRequestToRemoveBookWithSuccess() throws JsonProcessingException {
        var contentBodyFindAll = given().spec(specification)
                .contentType(TestConstants.CONTENT_TYPE_JSON)
                .header(TestConstants.HEADER_PARAM_ORIGIN, TestConstants.VALID_ORIGIN)
                .when().get()
                .then().statusCode(HttpStatus.OK.value())
                .extract().body().asString();
        List<BookVOIntegrationTest> books = mapper.readValue(contentBodyFindAll, new TypeReference<>() {
        });
        assertFalse(books.isEmpty());

        BookVOIntegrationTest bookToDelete = books.get(0);
        given().spec(specification)
                .contentType(TestConstants.CONTENT_TYPE_JSON)
                .header(TestConstants.HEADER_PARAM_ORIGIN, TestConstants.VALID_ORIGIN)
                .pathParam("id", bookToDelete.getId())
                .when().delete("{id}")
                .then().statusCode(HttpStatus.NO_CONTENT.value());
        given().spec(specification)
                .contentType(TestConstants.CONTENT_TYPE_JSON)
                .header(TestConstants.HEADER_PARAM_ORIGIN, TestConstants.VALID_ORIGIN)
                .pathParam("id", bookToDelete.getId())
                .when().get("{id}")
                .then().statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @Order(6)
    public void shouldReturnInvalidCorsWhenPerformingDeleteToRemoveBookWithInvalidOrigin() {
        var contentBody = given().spec(specification)
                .contentType(TestConstants.CONTENT_TYPE_JSON)
                .header(TestConstants.HEADER_PARAM_ORIGIN, TestConstants.INVALID_ORIGIN)
                .pathParam("id", 1)
                .when().delete("{id}")
                .then().statusCode(HttpStatus.FORBIDDEN.value())
                .extract().body().asString();

        assertNotNull(contentBody);
        assertEquals("Invalid CORS request", contentBody);
    }

    private BookVOIntegrationTest mockBookVOIntegrationTest() {
        BookVOIntegrationTest book = new BookVOIntegrationTest();
        book.setAuthor("Wes");
        book.setTitle("The Life of Wes");
        book.setPrice(100.00);
        book.setLaunchDate(LocalDate.of(2023, Month.DECEMBER, 1));
        return book;
    }
}
