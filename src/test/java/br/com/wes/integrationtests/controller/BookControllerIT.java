package br.com.wes.integrationtests.controller;

import br.com.wes.integrationtests.AbstractIT;
import br.com.wes.integrationtests.TestConstants;
import br.com.wes.integrationtests.vo.BookVOIT;
import br.com.wes.integrationtests.vo.wrapper.BookVOITWrapper;
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

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookControllerIT extends AbstractIT {

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
                .setPort(TestConstants.SERVER_PORT).setBasePath("/api/book/v1")
                .addHeader(TestConstants.HEADER_PARAM_AUTHORIZATION, "Bearer " + accessToken)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();
    }

    @Test
    @Order(1)
    @DisplayName("Should return forbidden when try to perform book request and user not authorized")
    public void shouldReturnForbiddenWhenTryToPerformBookRequestAndUserNotAuthorized() {
        var specificationWithoutToken = new RequestSpecBuilder()
                .setPort(TestConstants.SERVER_PORT).setBasePath("/api/book/v1")
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
    @DisplayName("Should perform post request to book with success")
    public void shouldPerformPostRequestToBookWithSuccess() throws JsonProcessingException {
        var book = mockBookVOIntegrationTest();
        var contentBody = given().spec(specification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(TestConstants.HEADER_PARAM_ORIGIN, TestConstants.VALID_ORIGIN)
                .body(book)
                .when().post()
                .then().statusCode(HttpStatus.OK.value())
                .extract().body().asString();

        BookVOIT bookPersisted = mapper.readValue(contentBody, BookVOIT.class);

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
    @Order(3)
    @DisplayName("Should return invalid cors when performing post to book with invalid origin")
    public void shouldReturnInvalidCorsWhenPerformingPostToBookWithInvalidOrigin() {
        var book = mockBookVOIntegrationTest();
        var contentBody = given().spec(specification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(TestConstants.HEADER_PARAM_ORIGIN, TestConstants.INVALID_ORIGIN)
                .body(book)
                .when().post()
                .then().statusCode(HttpStatus.FORBIDDEN.value())
                .extract().body().asString();

        assertNotNull(contentBody);
        assertEquals("Invalid CORS request", contentBody);
    }

    @Test
    @Order(4)
    @DisplayName("Should perform get request to find book with success")
    public void shouldPerformGetRequestToFindBookWithSuccess() throws JsonProcessingException {
        var contentBodyFindAll = given().spec(specification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(TestConstants.HEADER_PARAM_ORIGIN, TestConstants.VALID_ORIGIN)
                .queryParams("page", 0, "size", 10, "direction", "asc")
                .when().get()
                .then().statusCode(HttpStatus.OK.value())
                .extract().body().asString();

        BookVOITWrapper wrapper = mapper.readValue(contentBodyFindAll, BookVOITWrapper.class);
        List<BookVOIT> books = wrapper.getEmbedded().getBooks();
        assertFalse(books.isEmpty());

        BookVOIT bookToFetch = books.getFirst();
        var contentBodyFindById = given().spec(specification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("id", bookToFetch.getId())
                .when().get("{id}")
                .then().statusCode(HttpStatus.OK.value())
                .extract().body().asString();

        BookVOIT bookPersisted = mapper.readValue(contentBodyFindById, BookVOIT.class);
        assertNotNull(bookPersisted);
        assertNotNull(bookPersisted.getId());
        assertNotNull(bookPersisted.getAuthor());
        assertNotNull(bookPersisted.getTitle());
        assertNotNull(bookPersisted.getPrice());
        assertNotNull(bookPersisted.getLaunchDate());
        assertEquals(bookToFetch, bookPersisted);
    }

    @Test
    @Order(5)
    @DisplayName("Should return invalid cors when performing get to find book with invalid origin")
    public void shouldReturnInvalidCorsWhenPerformingGetToFindBookWithInvalidOrigin() {
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
    @DisplayName("Should return books with success when find books by title")
    public void shouldReturnBooksWithSuccessWhenFindByTitle() throws JsonProcessingException {
        var contentBody = given().spec(specification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(TestConstants.HEADER_PARAM_ORIGIN, TestConstants.VALID_ORIGIN)
                .pathParam("title", "legacy code")
                .queryParams("page", 0, "size", 10, "direction", "asc")
                .when().get("/findBooksByTitle/{title}")
                .then().statusCode(HttpStatus.OK.value())
                .extract().body().asString();

        BookVOITWrapper wrapper = mapper.readValue(contentBody, BookVOITWrapper.class);
        var books = wrapper.getEmbedded().getBooks();
        assertFalse(books.isEmpty());

        var person = books.getFirst();
        assertEquals("Working effectively with legacy code", person.getTitle());
    }

    @Test
    @Order(7)
    @DisplayName("Should return invalid cors when find books by title with invalid origin")
    public void shouldReturnInvalidCorsWhenFindBooksByTitleWithInvalidOrigin() {
        var contentBody = given().spec(specification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(TestConstants.HEADER_PARAM_ORIGIN, TestConstants.INVALID_ORIGIN)
                .pathParam("title", "legacy code")
                .queryParams("page", 0, "size", 10, "direction", "asc")
                .when().get("/findBooksByTitle/{title}")
                .then().statusCode(HttpStatus.FORBIDDEN.value())
                .extract().body().asString();

        assertNotNull(contentBody);
        assertEquals("Invalid CORS request", contentBody);
    }

    @Test
    @Order(8)
    @DisplayName("Should perform put request to update a book with success")
    public void shouldPerformPutRequestToUpdateBookWithSuccess() throws JsonProcessingException {
        var contentBodyFindAll = given().spec(specification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(TestConstants.HEADER_PARAM_ORIGIN, TestConstants.VALID_ORIGIN)
                .queryParams("page", 0, "size", 10, "direction", "asc")
                .when().get()
                .then().statusCode(HttpStatus.OK.value())
                .extract().body().asString();
        BookVOITWrapper wrapper = mapper.readValue(contentBodyFindAll, BookVOITWrapper.class);
        List<BookVOIT> books = wrapper.getEmbedded().getBooks();
        assertFalse(books.isEmpty());

        BookVOIT bookToUpdate = books.getFirst();
        bookToUpdate.setPrice(99.9);
        given().spec(specification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(TestConstants.HEADER_PARAM_ORIGIN, TestConstants.VALID_ORIGIN)
                .body(bookToUpdate)
                .when().put()
                .then().statusCode(HttpStatus.OK.value());

        var contentBodyFindById = given().spec(specification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("id", bookToUpdate.getId())
                .when().get("{id}")
                .then().statusCode(HttpStatus.OK.value())
                .extract().body().asString();
        BookVOIT bookUpdated = mapper.readValue(contentBodyFindById, BookVOIT.class);

        assertEquals(bookToUpdate.getPrice(), bookUpdated.getPrice());
    }

    @Test
    @Order(9)
    @DisplayName("Should return invalid cors when performing put to update book with invalid origin")
    public void shouldReturnInvalidCorsWhenPerformingPutToUpdateBookWithInvalidOrigin() {
        var book = mockBookVOIntegrationTest();
        var contentBody = given().spec(specification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(TestConstants.HEADER_PARAM_ORIGIN, TestConstants.INVALID_ORIGIN)
                .body(book)
                .when().put()
                .then().statusCode(HttpStatus.FORBIDDEN.value())
                .extract().body().asString();

        assertNotNull(contentBody);
        assertEquals("Invalid CORS request", contentBody);
    }

    @Test
    @Order(10)
    @DisplayName("Should perform delete request to remove book with success")
    public void shouldPerformDeleteRequestToRemoveBookWithSuccess() throws JsonProcessingException {
        var contentBodyFindAll = given().spec(specification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(TestConstants.HEADER_PARAM_ORIGIN, TestConstants.VALID_ORIGIN)
                .queryParams("page", 0, "size", 10, "direction", "asc")
                .when().get()
                .then().statusCode(HttpStatus.OK.value())
                .extract().body().asString();

        BookVOITWrapper wrapper = mapper.readValue(contentBodyFindAll, BookVOITWrapper.class);
        List<BookVOIT> books = wrapper.getEmbedded().getBooks();
        assertFalse(books.isEmpty());

        BookVOIT bookToDelete = books.getFirst();
        given().spec(specification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(TestConstants.HEADER_PARAM_ORIGIN, TestConstants.VALID_ORIGIN)
                .pathParam("id", bookToDelete.getId())
                .when().delete("{id}")
                .then().statusCode(HttpStatus.NO_CONTENT.value());
        given().spec(specification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(TestConstants.HEADER_PARAM_ORIGIN, TestConstants.VALID_ORIGIN)
                .pathParam("id", bookToDelete.getId())
                .when().get("{id}")
                .then().statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @Order(11)
    @DisplayName("Should return invalid cors when performing delete to remove book with invalid origin")
    public void shouldReturnInvalidCorsWhenPerformingDeleteToRemoveBookWithInvalidOrigin() {
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

    private BookVOIT mockBookVOIntegrationTest() {
        BookVOIT book = new BookVOIT();
        book.setAuthor("Wes");
        book.setTitle("The Life of Wes");
        book.setPrice(100.00);
        book.setLaunchDate(LocalDate.of(2023, Month.DECEMBER, 1));
        return book;
    }
}
