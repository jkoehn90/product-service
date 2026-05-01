# Product Service — Microservices System

Part of the **Microservices System** — a distributed backend built with Java, Spring Boot, and Spring Cloud.

## Overview

The Product Service manages the **product catalog** for the Microservices System. It provides full CRUD operations for products, along with search and stock filtering capabilities. Authentication is handled upstream by the API Gateway — all requests reaching this service have already been validated.

## Tech Stack

| Technology | Purpose |
|---|---|
| Java 17 | Core language |
| Spring Boot 3.5.x | Application framework |
| Spring Data JPA | Database ORM |
| PostgreSQL | Relational database |
| Spring Security | Request permitting |
| Lombok | Boilerplate reduction |
| Netflix Eureka Client | Service discovery |

## Architecture Role

```
Client Request
      │
      ▼
API Gateway (port 8080)
      │
      ▼ routes /products/**
Product Service (port 8082)
      │         ▲
      ▼         │ WebClient calls
PostgreSQL   Order Service
(productdb)
```

## API Endpoints

All endpoints require a valid JWT token passed via the `Authorization: Bearer <token>` header.

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/products` | Create a new product |
| `GET` | `/products` | Get all products |
| `GET` | `/products/{id}` | Get product by ID |
| `PUT` | `/products/{id}` | Update a product |
| `DELETE` | `/products/{id}` | Delete a product |
| `GET` | `/products/search?name=` | Search products by name |
| `GET` | `/products/in-stock` | Get all in-stock products |

### Create/Update Product Request
```json
{
    "name": "iPhone 15",
    "description": "Apple iPhone 15 128GB",
    "price": 799.99,
    "quantity": 50
}
```

### Product Response
```json
{
    "id": 1,
    "name": "iPhone 15",
    "description": "Apple iPhone 15 128GB",
    "price": 799.99,
    "quantity": 50
}
```

## Project Structure

```
src/main/java/com/yourname/productservice/
├── controller/
│   └── ProductController.java
├── service/
│   └── ProductService.java
├── repository/
│   └── ProductRepository.java
├── entity/
│   └── Product.java
├── dto/
│   ├── ProductRequest.java
│   └── ProductResponse.java
└── config/
    └── SecurityConfig.java
```

## Getting Started

### Prerequisites
- Java 17+
- Maven
- PostgreSQL (or Docker)
- Eureka Server running on port `8761`

### Database Setup (Docker)
```bash
docker run --name productdb-postgres \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=yourpassword \
  -e POSTGRES_DB=productdb \
  -p 5433:5432 \
  -d postgres:15
```

### Running Locally
```bash
mvn spring-boot:run
```

### Configuration
Update `src/main/resources/application.yml` with your database credentials:
```yaml
spring:
  datasource:
    password: yourpassword
```

## Related Services

| Service | Port | Repo |
|---|---|---|
| Eureka Server | 8761 | [eureka-server](../eureka-server) |
| API Gateway | 8080 | [api-gateway](../api-gateway) |
| User Service | 8081 | [user-service](../user-service) |
| Order Service | 8083 | [order-service](../order-service) |