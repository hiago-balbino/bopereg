.PHONY: clean-pgk docker-up docker-ps docker-down

clean-pgk:
	mvn clean package

docker-up: clean-pgk
	docker compose up -d --build

docker-down:
	docker compose down -v --remove-orphans

docker-ps:
	docker compose ps