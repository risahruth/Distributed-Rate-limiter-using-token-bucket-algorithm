# Architecture Overview

## High-Level Flow

```
Client
   ↓
React UI (Monitoring Dashboard)
   ↓
Load Balancer (Production Only)
   ↓
Spring Boot Application
   ↓
JWT Authentication (Spring Security Filter)
   ↓
Rate Limiter Service (Token Bucket Algorithm)
   ↓
Redis (Shared Token Storage)
   ↓
Decision (Allow / HTTP 429)
```

---

## Component Breakdown

| Component | Description |
|------------|-------------|
| **Client** | Sends HTTP requests via browser or external service and interacts with the system through the React UI. |
| **React UI (Monitoring Dashboard)** | Provides login interface, displays token usage, shows allowed and blocked requests, and sends authenticated API requests to backend. Stores JWT token after login and attaches it in the Authorization header. |
| **Load Balancer** | Distributes incoming traffic across multiple Spring Boot instances to ensure scalability and high availability in production deployments. |
| **Spring Boot Application** | Core backend service responsible for authentication, rate limiting, logging, configuration management, and request processing. |
| **JWT Authentication (Spring Security Filter)** | Validates JWT tokens from incoming requests and extracts authenticated user identity before rate limiting is applied. |
| **Rate Limiter Service** | Implements the Token Bucket algorithm to enforce per-user request limits using centralized configuration. |
| **Redis Storage** | Maintains shared token state across distributed instances using atomic operations to prevent race conditions and ensure consistency. |
| **Controller Layer** | Executes business logic only if the request passes authentication and rate limiting checks. |
| **Decision Layer** | Returns either successful processing (HTTP 200) or HTTP 429 (Too Many Requests) if token limits are exceeded. |

---

## Authentication Flow (JWT-Based)

```
1. User submits login credentials via React UI
        ↓
2. React sends POST /auth/login to Spring Boot
        ↓
3. Spring Security authenticates credentials
        ↓
4. JWT token is generated and returned to React
        ↓
5. React stores JWT (localStorage or memory)
        ↓
6. For every protected request:
        - React sends JWT in Authorization header
        ↓
7. Spring Security JWT Filter validates token
        ↓
8. Authenticated User ID is extracted
        ↓
9. Request proceeds to Rate Limiter Service
```

---

## Backend Execution Order

```
Incoming HTTP Request
        ↓
Load Balancer (If enabled)
        ↓
Spring Boot Instance
        ↓
Spring Security Filter Chain (JWT Validation)
        ↓
Extract Authenticated User Identity
        ↓
Rate Limiter Service (Token Bucket Execution)
        ↓
Redis (Read / Update Token State)
        ↓
If Tokens Available → Controller Executes → HTTP 200
If Tokens Exhausted → Return HTTP 429
```
