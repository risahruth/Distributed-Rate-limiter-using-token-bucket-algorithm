# Project Scope

## Objective
To design and develop a distributed rate limiting and traffic control
system to manage API requests and prevent system overload.

## In-Scope
- User-based request rate limiting
- API Endpoints to check and update rate limits
- Token bucket algorithm implementation
- Centralized rate limit configuration
- Logging of allowed and blocked requests
- Basic in-memory and redis based storage
- User Authentication(JWT via Spring Security).
- UI-based monitoring dashboards
- Global deployment.

## Out-of-Scope
- ML-driven traffic prediction

## Target Users
- Backend developers
- System administrators

## Assumptions and Constraints
- Implemented using Java and Spring Boot
- Designed for small to medium-scale systems
- Runs in a controlled server environment

## Deliverables
- Source code
- Documentation
- Architecture diagrams
