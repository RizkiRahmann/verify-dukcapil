# ===== Makefile untuk Docker Operations =====
# File: Makefile

.PHONY: help build up down logs restart clean test

help: ## Show this help message
	@echo 'Usage: make [target]'
	@echo ''
	@echo 'Targets:'
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z_-]+:.*?## / {printf "  %-15s %s\n", $$1, $$2}' $(MAKEFILE_LIST)

build: ## Build Docker images
	docker-compose build --no-cache

up: ## Start all services
	docker-compose up -d

down: ## Stop all services
	docker-compose down

logs: ## View logs
	docker-compose logs -f dukcapil-service

restart: ## Restart Dukcapil service
	docker-compose restart dukcapil-service

clean: ## Clean up everything
	docker-compose down -v
	docker system prune -f

test: ## Run tests against running service
	./test_dukcapil_service.sh

dev-up: ## Start only PostgreSQL for development
	docker-compose -f docker-compose-dev.yml up -d

dev-down: ## Stop development PostgreSQL
	docker-compose -f docker-compose-dev.yml down

status: ## Show service status
	docker-compose ps

health: ## Check service health
	curl -s http://localhost:8081/api/dukcapil/health | jq .

stats: ## Show service statistics
	curl -s http://localhost:8081/api/dukcapil/stats | jq .