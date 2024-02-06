.PHONY: clean-pkg docker-up docker-ps docker-down

clean-pkg:
	mvn clean package

docker-up: clean-pkg
	docker compose up -d --build

docker-down:
	docker compose down -v --remove-orphans

docker-ps:
	docker compose ps