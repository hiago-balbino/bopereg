.PHONY: clean-pkg tests integration-tests all-tests mysql run-pkg run-boot docker-up docker-ps docker-down

clean-pkg:
	mvn clean package

tests:
	mvn test

integration-tests:
	mvn integration-test

all-tests: tests integration-tests

mysql:
	docker run -d --name mysql -e MYSQL_ROOT_PASSWORD=admin123 -p 3306:3306 mysql:8.3.0

run-pkg: clean-pkg
	java -jar target/bopereg-*.jar

run-boot:
	mvn spring-boot:run

docker-up: clean-pkg
	docker compose up -d --build

docker-down:
	docker compose down -v --remove-orphans

docker-ps:
	docker compose ps