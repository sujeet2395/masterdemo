# Multi-Tenant SaaS Platform

This is a multi-tenant SaaS platform with a Java Spring Boot backend and an Angular frontend.

## Backend

The backend is built with Spring Boot, providing REST APIs for tenant management. It uses JPA/Hibernate for database operations and supports multi-tenancy.

### Running the Backend

1. Navigate to the `backend` directory.
2. Run `mvn spring-boot:run`.

The backend will start on port 8080.

## Frontend

The frontend is an Angular application with components for tenant management.

### Running the Frontend

1. Navigate to the `frontend` directory.
2. Run `npm install`.
3. Run `npm start`.

The frontend will start on port 4200.

## Database

The database schema is defined in `database/schema.sql`. It supports multi-tenant organization data.

## Documentation

- [API Documentation](api-docs.md)
- [Deployment Guide](deployment.md)