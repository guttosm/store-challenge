.PHONY: help build run test clean docker-build docker-up docker-down docker-logs docker-clean coverage coverage-report sonar

# Variables
APP_NAME=store-service
DOCKER_IMAGE=$(APP_NAME):latest
COMPOSE_FILE=compose.yaml

help: ## Show this help message
	@echo 'Usage: make [target]'
	@echo ''
	@echo 'Available targets:'
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z_-]+:.*?## / {printf "  %-15s %s\n", $$1, $$2}' $(MAKEFILE_LIST)

build: ## Build the application
	./mvnw clean package -DskipTests

run: ## Run the application locally
	./mvnw spring-boot:run

test: ## Run all tests
	./mvnw test

test-unit: ## Run unit tests only
	./mvnw test -Dtest=*Test

test-integration: ## Run integration tests only
	./mvnw test -Dtest=*IT

clean: ## Clean build artifacts
	./mvnw clean
	rm -rf target/

docker-build: ## Build Docker image
	docker build -t $(DOCKER_IMAGE) .

docker-up: ## Start services with docker-compose
	docker-compose -f $(COMPOSE_FILE) up -d

docker-down: ## Stop services with docker-compose
	docker-compose -f $(COMPOSE_FILE) down

docker-logs: ## Show logs from docker-compose
	docker-compose -f $(COMPOSE_FILE) logs -f

docker-clean: docker-down ## Clean Docker resources
	docker-compose -f $(COMPOSE_FILE) down -v
	docker rmi $(DOCKER_IMAGE) || true

docker-restart: docker-down docker-up ## Restart docker services

install: ## Install dependencies
	./mvnw dependency:resolve

lint: ## Run code quality checks
	./mvnw checkstyle:check || echo "Checkstyle plugin not configured"

package: build ## Package the application
	@echo "Application packaged successfully"

all: clean build test ## Clean, build, and test

coverage: ## Run tests with coverage
	./mvnw clean test jacoco:report

coverage-report: coverage ## Generate and open coverage report
	@echo "Coverage report generated at: target/site/jacoco/index.html"
	@if command -v open > /dev/null; then open target/site/jacoco/index.html; \
	elif command -v xdg-open > /dev/null; then xdg-open target/site/jacoco/index.html; \
	else echo "Please open target/site/jacoco/index.html in your browser"; fi

coverage-check: ## Check coverage thresholds
	./mvnw clean test jacoco:check

sonar: ## Run SonarCloud analysis locally (requires SONAR_TOKEN)
	./mvnw clean verify sonar:sonar

verify: ## Run all tests including integration tests
	./mvnw clean verify

