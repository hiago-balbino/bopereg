[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Tests](https://github.com/hiago-balbino/bopereg/actions/workflows/tests.yaml/badge.svg?branch=main&event=push&logo=github&style=flat-square)](https://github.com/hiago-balbino/bopereg/actions/workflows/tests.yaml)

# Bopereg ✏️

This project serves an educational purpose, employing Java to manage the registration of books and people.

This application have some populated data using database migration.

Users to signin:
```
username=wes password=admin123
username=tsu password=user123
```

User to integration test:
```text
username=usertest password=test123
```

## Dependencies

* Maven
* SpringBoot 3.2
* Java 21
* JavaJWT
* ModelMapper
* JUnit
* Mockito
* TestContainers
* RestAssured
* Flyway
* MySQL
* Hateoas
* OpenAPI (Swagger)

## Environment variables

All these environment variables are required to run the application.

```
JWT_SECRET='53cr37'
DB_USERNAME='root'
DB_PASSWORD='admin123'
```

## Running the tests

*Unit tests*
```bash
make tests # or mvn test
```

*Integration tests*
```bash
make integration-tests # or mvn integration-test
```

*Unit and Integration tests*
```bash
make all-tests # or mvn test integration-test
```

## Running the application
#### Using Docker Compose

```bash
make docker-up # or docker compose up -d --build
```

```bash
make docker-down # or docker compose down -v --remove-orphans
```

```bash
make docker-ps # or docker compose ps
```

#### Without Docker Compose
You can also run the application without Docker Compose, but the databased need to be running on Docker.

```bash
make mysql # or docker run -d --name mysql -e MYSQL_ROOT_PASSWORD=admin123 -p 3306:3306 mysql:8.3.0
```

After the database is running, you can create the database schema called `bopereg` using the tools of your preference.

#### Building the application

```bash
make clean-pkg # or mvn clean package
```

#### Running the application

```bash
make run-pkg # or java -jar target/bopereg-*.jar
```

or

```bash
make run-boot # or mvn spring-boot:run
```

## cURL

#### Swagger
You can access the Swagger documentation at http://localhost:8080/swagger-ui/index.html

---

#### Authentication
*Signin*: You can replace the `<username>` and `<password>` with the values `wes` and `admin123`.
You will need to use the generated token in all requests replacing the `<token>` tag.

```bash
curl --request POST \
--url http://localhost:8080/auth/signin \
--header 'Content-Type: application/json' \
--data '{
"username":"<username>",
"password":"<password>"
}'
```

*Refresh Token*

```bash
curl --request PUT \
--url http://localhost:8080/auth/refresh/<username> \
--header 'Authorization: Bearer <token>' \
--header 'Content-Type: application/json'
```

---

#### Books

*Create*
```bash
curl --request POST \
  --url http://localhost:8080/api/book/v1 \
  --header 'Authorization: Bearer <token>' \
  --header 'Content-Type: application/json' \
  --header 'Origin: http://localhost:8080' \
  --data '{
	"author": "Wes",
	"title": "Working with Java properly",
	"price": 200.0,
	"launch_date": "2023-11-28"
}'
```

*Find By ID*: Replace `<id>` tag for the desired identifier.
```bash
curl --request GET \
  --url http://localhost:8080/api/book/v1/<id> \
  --header 'Authorization: Bearer <token>' \
  --header 'Origin: http://localhost:8080'
```

*Find All Paginated*
```bash
curl --request GET \
  --url 'http://localhost:8080/api/book/v1?page=0&size=10&direction=asc' \
  --header 'Authorization: Bearer <token>' \
  --header 'Origin: http://localhost:8080'
```

*Find By Title Paginated*: Replace `<title>` tag for the desired title.
```bash
  curl --request GET \
  --url 'http://localhost:8080/api/book/v1/findBooksByTitle/<title>?page=0&size=10&direction=asc' \
  --header 'Authorization: Bearer <token>' \
  --header 'Origin: http://localhost:8080'
```

*Update*: Replace `<id>` tag for the desired book identifier to be updated.
```bash
  curl --request PUT \
  --url http://localhost:8080/api/book/v1 \
  --header 'Authorization: Bearer <token>' \
  --header 'Content-Type: application/json' \
  --header 'Origin: http://localhost:8080' \
  --data '{
	"id": <id>,
	"author": "Wes",
	"title": "Working with Java properly v2",
	"price": 350.0,
	"launch_date": "2023-12-31"
}'
```

*Delete*: Replace `<id>` tag for the desired book identifier to be deleted.
```bash
curl --request DELETE \
  --url http://localhost:8080/api/book/v1/<id> \
  --header 'Authorization: Bearer <token>' \
  --header 'Origin: http://localhost:8080'
```

---

#### People

*Create*
```bash
curl --request POST \
  --url http://localhost:8080/api/person/v1 \
  --header 'Authorization: Bearer <token>' \
  --header 'Content-Type: application/json' \
  --header 'Origin: http://localhost:8080' \
  --data '{
	"first_name": "TheWes",
	"last_name": "A.",
	"address": "Wonderland",
	"gender": "Male",
	"enabled": true
}'
```

*Find By ID*: Replace `<id>` tag for the desired identifier.
```bash
curl --request GET \
  --url http://localhost:8080/api/person/v1/<id> \
  --header 'Authorization: Bearer <token>' \
  --header 'Origin: http://localhost:8080'
```

*Find All Paginated*
```bash
  curl --request GET \
  --url 'http://localhost:8080/api/person/v1?page=0&size=10&direction=desc' \
  --header 'Authorization: Bearer <token>' \
  --header 'Accept: application/json' \
  --header 'Origin: http://localhost:8080'
```

*Find By Name Paginated*: Replace `<name>` tag for the desired person.
```bash
curl --request GET \
  --url 'http://localhost:8080/api/person/v1/findPeopleByName/<name>?page=0&size=10&direction=desc' \
  --header 'Authorization: Bearer <token>' \
  --header 'Accept: application/json' \
  --header 'Origin: http://localhost:8080'
```

*Update*: Replace `<id>` tag for the desired person identifier to be updated.
```bash
curl --request PUT \
  --url http://localhost:8080/api/person/v1 \
  --header 'Authorization: Bearer <token>' \
  --header 'Content-Type: application/json' \
  --header 'Origin: http://localhost:8080' \
  --data '{
	"id": <id>,
	"first_name": "Wes",
	"last_name": "B.",
	"address": "Wonderland",
	"gender": "Male",
	"enabled": true
}'
```

*Disable*: Replace `<id>` tag for the desired person identifier to be disabled.
```bash
curl --request PATCH \
  --url http://localhost:8080/api/person/v1/<id> \
  --header 'Authorization: Bearer <token>' \
  --header 'Content-Type: application/json' \
  --header 'Origin: http://localhost:8080'
```

*Delete*: Replace `<id>` tag for the desired person identifier to be deleted.
```bash
curl --request DELETE \
  --url http://localhost:8080/api/person/v1/<id> \
  --header 'Authorization: Bearer <token>' \
  --header 'Origin: http://localhost:8080'
```

---

### Extra
#### File

*Upload a unique file*: Replace <dir/filename> tag with the directory and name file with extension of the file you want to upload.
```bash
curl --request POST \
  --url http://localhost:8080/api/file/v1/uploadFile \
  --header 'Authorization: Bearer <token>' \
  --header 'Content-Type: multipart/form-data' \
  --form file=@<dir/filename>
```

*Upload many files*: Replace <dir/filename> tag with the directories and name files with extension of the files you want to upload.
```bash
curl --request POST \
  --url http://localhost:8080/api/file/v1/uploadFiles \
  --header 'Authorization: Bearer <token>' \
  --header 'Content-Type: multipart/form-data' \
  --form files=@<dir/filename> \
  --form files=@<dir/filename>
```

*Download a file*: Replace the `<filename>` tag with the name of the file with extension that you want to download.
```bash
curl --request GET \
  --url http://localhost:8080/api/file/v1/downloadFile/<filename> \
  --header 'Authorization: Bearer <token>' \
  --header 'Accept: application/octet-stream' >> <filename>
```

## Generate encoded password
If you want to create a new encoded password you can use the code below.

```java
private static void generatePassword() {
    Pbkdf2PasswordEncoder pbkdf2PasswordEncoder = new Pbkdf2PasswordEncoder("", 8, 185000, Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA256);

    Map<String, PasswordEncoder> encoders = new HashMap<>();
    encoders.put("pbkdf2", pbkdf2PasswordEncoder);

    DelegatingPasswordEncoder passwordEncoder = new DelegatingPasswordEncoder("pbkdf2", encoders);
    passwordEncoder.setDefaultPasswordEncoderForMatches(pbkdf2PasswordEncoder);

    String encoded = passwordEncoder.encode("");
    System.out.printf(encoded);
}
```