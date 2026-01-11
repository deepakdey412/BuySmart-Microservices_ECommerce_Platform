# Setup Guide - Scalable E-Commerce Platform

This guide will help you set up and run the complete microservices-based e-commerce platform.

## Prerequisites

- **Java 17+** - [Download](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
- **Maven 3.8+** - [Download](https://maven.apache.org/download.cgi)
- **Node.js 18+** - [Download](https://nodejs.org/)
- **Docker & Docker Compose** - [Download](https://www.docker.com/products/docker-desktop)
- **MySQL 8.0+** (Optional - Docker Compose includes MySQL containers)
- **Kafka 3.0+** (Optional - Docker Compose includes Kafka)

## Architecture Overview

### Microservices

1. **Eureka Service Registry** (Port: 8761)
   - Service discovery and registration

2. **Config Server** (Port: 8888)
   - Centralized configuration management

3. **API Gateway** (Port: 8080)
   - Single entry point for all client requests

4. **Auth-User Service** (Port: 8081)
   - Authentication, authorization, and user management
   - JWT-based security
   - Roles: USER, ADMIN

5. **Product Catalog Service** (Port: 8082)
   - Product and category management

6. **Inventory Service** (Port: 8083)
   - Stock management and reservations
   - Kafka consumer for order events

7. **Cart Service** (Port: 8084)
   - Shopping cart management

8. **Order Service** (Port: 8085)
   - Order processing and orchestration
   - Kafka producer for order events

9. **Payment Service** (Port: 8086)
   - Payment processing (Stripe integration)

10. **Notification Service** (Port: 8087)
    - Email notifications
    - Kafka consumer for order events

### Frontend

- **React 18 + Vite** (Port: 3000)
  - User interface
  - Tailwind CSS styling

## Quick Start with Docker Compose

### Step 1: Build the Project

```bash
# Build all microservices
mvn clean install -DskipTests

# Note: If you see <n> tags in pom.xml, replace them with <name> tags
```

### Step 2: Start All Services

```bash
# Start all services using Docker Compose
docker-compose up -d

# View logs
docker-compose logs -f

# Check service status
docker-compose ps
```

### Step 3: Start Frontend

```bash
cd frontend
npm install
npm run dev
```

The frontend will be available at `http://localhost:3000`

## Manual Setup (Without Docker)

### Step 1: Start Infrastructure

Start MySQL and Kafka manually or use Docker:

```bash
# Start MySQL (one instance for all services, or separate instances)
docker run -d --name mysql -e MYSQL_ROOT_PASSWORD=rootpassword -p 3306:3306 mysql:8.0

# Start Zookeeper
docker run -d --name zookeeper -p 2181:2181 confluentinc/cp-zookeeper:7.5.0

# Start Kafka
docker run -d --name kafka -p 9092:9092 \
  -e KAFKA_BROKER_ID=1 \
  -e KAFKA_ZOOKEEPER_CONNECT=localhost:2181 \
  -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 \
  confluentinc/cp-kafka:7.5.0
```

### Step 2: Create Databases

Create databases for each service:

```sql
CREATE DATABASE auth_db;
CREATE DATABASE product_db;
CREATE DATABASE inventory_db;
CREATE DATABASE cart_db;
CREATE DATABASE order_db;
CREATE DATABASE payment_db;
CREATE DATABASE notification_db;
```

### Step 3: Start Services in Order

**Terminal 1: Eureka Service Registry**
```bash
cd eureka-service-registry
mvn spring-boot:run
```

**Terminal 2: Config Server**
```bash
cd config-server
mvn spring-boot:run
```

**Terminal 3: Auth-User Service**
```bash
cd auth-user-service
mvn spring-boot:run
```

**Terminal 4: Product Catalog Service**
```bash
cd product-catalog-service
mvn spring-boot:run
```

**Terminal 5: Inventory Service**
```bash
cd inventory-service
mvn spring-boot:run
```

**Terminal 6: Cart Service**
```bash
cd cart-service
mvn spring-boot:run
```

**Terminal 7: Order Service**
```bash
cd order-service
mvn spring-boot:run
```

**Terminal 8: Payment Service**
```bash
cd payment-service
mvn spring-boot:run
```

**Terminal 9: Notification Service**
```bash
cd notification-service
mvn spring-boot:run
```

**Terminal 10: API Gateway**
```bash
cd api-gateway
mvn spring-boot:run
```

### Step 4: Start Frontend

```bash
cd frontend
npm install
npm run dev
```

## Configuration

### Database Configuration

Each service uses its own MySQL database. Update connection strings in `application.yml` files if needed.

### Stripe Configuration

Update Stripe secret key in `payment-service/src/main/resources/application.yml`:

```yaml
stripe:
  secret:
    key: sk_test_your_stripe_secret_key_here
```

### Email Configuration

Update email settings in `notification-service/src/main/resources/application.yml`:

```yaml
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: your-email@gmail.com
    password: your-app-password
```

## Testing

### API Endpoints

- **API Gateway**: http://localhost:8080
- **Eureka Dashboard**: http://localhost:8761
- **Swagger UI**: http://localhost:8080/swagger-ui.html (if configured)

### Test User Registration

1. Register a new user via `/api/auth/register`
2. Login via `/api/auth/login` to get JWT token
3. Use the token in Authorization header for protected endpoints

### Test Order Flow

1. Browse products
2. Add products to cart
3. Create order
4. Process payment
5. Receive order confirmation email

## Troubleshooting

### Services Not Starting

1. Check if ports are already in use
2. Verify database connections
3. Check Eureka service registry is running
4. Review service logs

### Database Connection Issues

1. Verify MySQL is running
2. Check database credentials
3. Ensure databases are created
4. Check network connectivity

### Kafka Issues

1. Verify Zookeeper is running
2. Check Kafka broker is accessible
3. Verify topic creation
4. Check consumer group configuration

### Frontend Issues

1. Verify API Gateway is running
2. Check CORS configuration
3. Verify JWT token is being sent
4. Check browser console for errors

## Production Deployment

### Environment Variables

Set environment variables for production:

- Database credentials
- JWT secret keys
- Stripe API keys
- Email configuration
- Kafka broker addresses

### Docker Images

Build Docker images:

```bash
cd <service-name>
mvn clean package
docker build -t <service-name>:latest .
```

### Kubernetes Deployment

Create Kubernetes manifests for:
- Deployments
- Services
- ConfigMaps
- Secrets
- Ingress

## Security Considerations

1. **JWT Secrets**: Use strong, randomly generated secrets
2. **Database Passwords**: Use strong passwords
3. **API Keys**: Store in environment variables or secrets management
4. **CORS**: Configure allowed origins properly
5. **HTTPS**: Use HTTPS in production
6. **Input Validation**: All user inputs are validated
7. **SQL Injection**: Using parameterized queries (JPA)

## Monitoring

- **Eureka Dashboard**: http://localhost:8761
- **Actuator Endpoints**: http://localhost:8080/actuator/health
- **Prometheus Metrics**: http://localhost:8080/actuator/prometheus (if configured)
- **Application Logs**: Check individual service logs

## Next Steps

1. Add unit and integration tests
2. Set up CI/CD pipeline
3. Configure monitoring and alerting
4. Add API rate limiting
5. Implement caching (Redis)
6. Add distributed tracing (Zipkin/Jaeger)
7. Set up load balancing
8. Implement circuit breakers (Resilience4j)

## Support

For issues or questions:
1. Check service logs
2. Review application.yml configuration
3. Verify all dependencies are installed
4. Check network connectivity between services

## License

MIT License