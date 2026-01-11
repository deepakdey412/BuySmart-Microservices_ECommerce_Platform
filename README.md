# Scalable E-Commerce Platform

Enterprise-grade microservices-based e-commerce platform built with Spring Boot 3 and React 18.

## Architecture

This platform follows microservices architecture with the following services:

1. **API Gateway** - Single entry point for all client requests
2. **Eureka Service Registry** - Service discovery and registration
3. **Config Server** - Centralized configuration management
4. **Auth-User Service** - Authentication, authorization, and user management
5. **Product Catalog Service** - Product and category management
6. **Inventory Service** - Stock management and reservations
7. **Cart Service** - Shopping cart management
8. **Order Service** - Order processing and orchestration
9. **Payment Service** - Payment processing (Stripe integration)
10. **Notification Service** - Email notifications

## Tech Stack

### Backend
- Java 17
- Spring Boot 3.2.0
- Spring Cloud 2023.0.0
- Spring Security + JWT
- Spring Data JPA + Hibernate
- MySQL (per service)
- Apache Kafka
- Spring Cloud Gateway
- Netflix Eureka
- Spring Cloud Config
- Feign Client
- Prometheus + Grafana
- Zipkin

### Frontend
- React 18
- Vite
- React Router DOM
- Axios
- Tailwind CSS
- ShadCN UI
- Recharts
- Context API

## Prerequisites

- Java 17+
- Maven 3.8+
- Node.js 18+
- Docker & Docker Compose
- MySQL 8.0+
- Apache Kafka 3.0+

## Quick Start

### Using Docker Compose (Recommended)

```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop all services
docker-compose down
```

### Manual Setup

1. **Start Infrastructure:**
   ```bash
   # Start MySQL, Kafka, Redis (if using)
   docker-compose up -d mysql kafka zookeeper
   ```

2. **Start Services in Order:**
   ```bash
   # 1. Eureka Service Registry
   cd eureka-service-registry && mvn spring-boot:run

   # 2. Config Server
   cd config-server && mvn spring-boot:run

   # 3. All Microservices
   # Run each service in separate terminal
   cd auth-user-service && mvn spring-boot:run
   cd product-catalog-service && mvn spring-boot:run
   # ... etc
   ```

3. **Start Frontend:**
   ```bash
   cd frontend
   npm install
   npm run dev
   ```

## Service Ports

| Service | Port |
|---------|------|
| API Gateway | 8080 |
| Eureka Server | 8761 |
| Config Server | 8888 |
| Auth-User Service | 8081 |
| Product Service | 8082 |
| Inventory Service | 8083 |
| Cart Service | 8084 |
| Order Service | 8085 |
| Payment Service | 8086 |
| Notification Service | 8087 |

## API Documentation

- API Gateway Swagger: http://localhost:8080/swagger-ui.html
- Eureka Dashboard: http://localhost:8761

## Database Schema

Each service has its own MySQL database:
- `auth_db`
- `product_db`
- `inventory_db`
- `cart_db`
- `order_db`
- `payment_db`
- `notification_db`

## Security

- JWT-based authentication
- Role-based access control (USER, ADMIN)
- API Gateway validates all requests
- BCrypt password hashing
- Refresh token mechanism

## Monitoring

- Prometheus: http://localhost:9090
- Grafana: http://localhost:3000
- Zipkin: http://localhost:9411

## Development

### Building

```bash
# Build all services
mvn clean install

# Build specific service
cd <service-name> && mvn clean install
```

### Testing

```bash
# Run all tests
mvn test

# Run specific service tests
cd <service-name> && mvn test
```
