# Architecture Overview

## High-Level Flow

```
Client → Load Balancer → Spring Boot Application → Redis → Decision (Allow / HTTP 429)
```

---

## Component Breakdown

| Component | Description |
|------------|-------------|
| **Client** | Sends HTTP requests to the system via browser, mobile app, or external service, optionally including a JWT token. |
| **Load Balancer** | Distributes incoming traffic across multiple Spring Boot instances to ensure scalability and high availability. |
| **Spring Boot Application** | Core backend service responsible for authentication, rate limiting, and request processing. |
| **Authentication (JWT via Spring Security)** | Validates JWT tokens and extracts authenticated user identity for downstream rate limiting. |
| **Rate Limiter Service** | Implements the Token Bucket algorithm to enforce per-user request limits. |
| **Redis Storage** | Maintains shared token state across distributed instances using atomic operations to prevent race conditions. |
| **Controller Layer** | Executes business logic and generates API responses for permitted requests. |
| **Decision Layer** | Returns either successful processing or HTTP 429 (Too Many Requests) based on token availability. |

---

## Expanded Flow

```
Client
   ↓
Load Balancer
   ↓
Spring Boot Application Instance
   ├── JWT Authentication
   ├── Rate Limiter Service
   └── Controller
   ↓
Redis (Shared Token Storage)
   ↓
Decision (Allow / HTTP 429)
```
