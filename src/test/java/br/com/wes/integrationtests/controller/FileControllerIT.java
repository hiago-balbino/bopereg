package br.com.wes.integrationtests.controller;

import br.com.wes.config.FileStorageConfig;
import br.com.wes.integrationtests.AbstractIT;
import br.com.wes.integrationtests.TestConstants;
import br.com.wes.vo.v1.UploadFileResponseVO;
import br.com.wes.vo.v1.security.AccountCredentialsVO;
import br.com.wes.vo.v1.security.TokenVO;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(SpringExtension.class)
public class FileControllerIT extends AbstractIT {

    private static RequestSpecification specification;
    private static ApplicationContext applicationContext;

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        FileControllerIT.applicationContext = applicationContext;
    }

    @SuppressWarnings({"resource", "ResultOfMethodCallIgnored"})
    @AfterAll
    public static void afterEach() throws IOException {
        FileStorageConfig fileStorageConfig = applicationContext.getBean(FileStorageConfig.class);
        Files.walk(Paths.get(fileStorageConfig.getUploadDir()))
                .map(Path::toFile)
                .forEach(File::delete);
    }

    @Test
    @Order(0)
    public void shouldReturnForbiddenWhenTryToPerformFileRequestAndUserNotAuthorized() {
        var specificationWithoutToken = new RequestSpecBuilder()
                .setPort(TestConstants.SERVER_PORT).setBasePath("/api/file/v1")
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
    @Order(1)
    public void shouldAuthorizeUserToPerformFileUploadOnTests() {
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
                .setPort(TestConstants.SERVER_PORT).setBasePath("/api/file/v1")
                .addHeader(TestConstants.HEADER_PARAM_AUTHORIZATION, "Bearer " + accessToken)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();
    }

    @Test
    @Order(2)
    public void shouldUploadFileWithSuccess() {
        UploadFileResponseVO fileResponse = given().spec(specification)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .header(TestConstants.HEADER_PARAM_ORIGIN, TestConstants.VALID_ORIGIN)
                .multiPart("file", "file1.txt", "file content1".getBytes())
                .when().post("/uploadFile")
                .then().statusCode(HttpStatus.OK.value())
                .extract().body().as(UploadFileResponseVO.class);

        assertEquals("file1.txt", fileResponse.getFileName());
        assertEquals("http://localhost:8888/api/file/v1/downloadFile/file1.txt", fileResponse.getFileDownloadUri());
        assertEquals(MediaType.APPLICATION_OCTET_STREAM_VALUE, fileResponse.getFileType());
        assertEquals(13, fileResponse.getSize());
    }

    @Test
    @Order(2)
    public void shouldReturnErrorWhenUploadFileWithoutMultiPart() {
        String contentBody = given().spec(specification)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .header(TestConstants.HEADER_PARAM_ORIGIN, TestConstants.VALID_ORIGIN)
                .when().post("/uploadFile")
                .then().statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .extract().body().asString();

        assertTrue(contentBody.contains("Failed to parse multipart servlet request"));
    }

    @Test
    @Order(3)
    public void shouldReturnInvalidCorsWhenPerformingFileUploadWithInvalidOrigin() {
        String contentBody = given().spec(specification)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .header(TestConstants.HEADER_PARAM_ORIGIN, TestConstants.INVALID_ORIGIN)
                .multiPart("files", "file1.txt", "file content".getBytes())
                .when().post("/uploadFile")
                .then().statusCode(HttpStatus.FORBIDDEN.value())
                .extract().body().asString();

        assertNotNull(contentBody);
        assertEquals("Invalid CORS request", contentBody);
    }

    @Test
    @Order(4)
    public void shouldUploadManyFilesWithSuccess() {
        List<UploadFileResponseVO> filesResponse = Arrays.stream(given().spec(specification)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .header(TestConstants.HEADER_PARAM_ORIGIN, TestConstants.VALID_ORIGIN)
                .multiPart("files", "file1.txt", "file content1".getBytes())
                .multiPart("files", "file2.txt", "file content2".getBytes())
                .when().post("/uploadFiles")
                .then().statusCode(HttpStatus.OK.value())
                .extract().body().as(UploadFileResponseVO[].class)).toList();

        var firstFileResponse = filesResponse.get(0);
        assertEquals("file1.txt", firstFileResponse.getFileName());
        assertEquals("http://localhost:8888/api/file/v1/downloadFile/file1.txt", firstFileResponse.getFileDownloadUri());
        assertEquals(MediaType.APPLICATION_OCTET_STREAM_VALUE, firstFileResponse.getFileType());
        assertEquals(13, firstFileResponse.getSize());

        var secondFileResponse = filesResponse.get(1);
        assertEquals("file2.txt", secondFileResponse.getFileName());
        assertEquals("http://localhost:8888/api/file/v1/downloadFile/file2.txt", secondFileResponse.getFileDownloadUri());
        assertEquals(MediaType.APPLICATION_OCTET_STREAM_VALUE, secondFileResponse.getFileType());
        assertEquals(13, secondFileResponse.getSize());
    }

    @Test
    @Order(5)
    public void shouldReturnErrorWhenUploadManyFilesWithoutMultiPart() {
        String contentBody = given().spec(specification)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .header(TestConstants.HEADER_PARAM_ORIGIN, TestConstants.VALID_ORIGIN)
                .when().post("/uploadFiles")
                .then().statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .extract().body().asString();

        assertTrue(contentBody.contains("Failed to parse multipart servlet request"));
    }

    @Test
    @Order(6)
    public void shouldReturnInvalidCorsWhenPerformingManyFilesUploadWithInvalidOrigin() {
        String contentBody = given().spec(specification)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .header(TestConstants.HEADER_PARAM_ORIGIN, TestConstants.INVALID_ORIGIN)
                .multiPart("files", "file1.txt", "file content1".getBytes())
                .multiPart("files", "file2.txt", "file content2".getBytes())
                .when().post("/uploadFiles")
                .then().statusCode(HttpStatus.FORBIDDEN.value())
                .extract().body().asString();

        assertNotNull(contentBody);
        assertEquals("Invalid CORS request", contentBody);
    }

    @Test
    @Order(7)
    public void shouldDownloadFileWithSuccess() {
        byte[] fileContent = given().spec(specification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .header(TestConstants.HEADER_PARAM_ORIGIN, TestConstants.VALID_ORIGIN)
                .pathParam("filename", "file1.txt")
                .when().get("/downloadFile/{filename}")
                .then().statusCode(HttpStatus.OK.value())
                .extract().body().asByteArray();

        assertNotNull(fileContent);
        assertEquals("file content1", new String(fileContent));
    }

    @Test
    @Order(8)
    public void shouldReturnInvalidCorsWhenPerformingFileDownloadWithInvalidOrigin() {
        String contentBody = given().spec(specification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .header(TestConstants.HEADER_PARAM_ORIGIN, TestConstants.INVALID_ORIGIN)
                .pathParam("filename", "file1.txt")
                .when().get("/downloadFile/{filename}")
                .then().statusCode(HttpStatus.FORBIDDEN.value())
                .extract().body().asString();

        assertNotNull(contentBody);
        assertEquals("Invalid CORS request", contentBody);
    }
}
