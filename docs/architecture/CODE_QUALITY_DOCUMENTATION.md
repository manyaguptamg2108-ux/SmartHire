# SmartHire – Code Quality & Architecture Documentation

## Overview

This document highlights the code quality standards, architectural patterns, exception handling mechanisms, and security practices implemented across the SmartHire platform.

---

## 1. Multi-Tier Architectural Layering

SmartHire enforces a strict separation of concerns across a classic multi-tier enterprise Java architecture:

```text
[ Presentation Layer ]     JSP Views / HTML5 / CSS / JavaScript
        ↓
[ Controller Layer ]       Jakarta Servlets (HTTP Request Handling, Session Validation, Route Routing)
        ↓
[ Service Layer ]          Business Logic, Domain Validation, Transaction Boundaries, Multithreading
        ↓
[ DAO Layer ]              Data Access Objects & Generic Interfaces (GenericDAO<T>)
        ↓
[ Utility Layer ]          DBUtil Connection Pool, PasswordUtil, TransactionManager, TaskExecutors
        ↓
[ Persistence Layer ]      MariaDB 12.x / MySQL 8.0+ Database (smarthire)
```

### Layer Principles:
- **Servlets** handle HTTP requests, inspect `HttpSession`, sanitize request parameters, delegate to services, and forward/redirect to JSPs. Servlets do NOT execute SQL directly.
- **Services** encapsulate business rules, enforce validation, manage transaction boundaries via `TransactionManager`, and execute background notifications.
- **DAOs** interact exclusively with JDBC connections using `PreparedStatement` and map `ResultSet` rows to domain model instances.

---

## 2. Naming Conventions & Code Organization

- **Packages**: Flat, descriptive single-word package names (`controller`, `model`, `service`, `dao`, `interfaces`, `exception`, `thread`, `util`).
- **Classes & Interfaces**: PascalCase (e.g., `UserDAO`, `JobService`, `GenericDAO`). Interfaces prefixed or named abstractly (`GenericDAO`, `TaskExecutor`).
- **Methods & Variables**: camelCase (e.g., `getJobById`, `candidateId`, `passwordHash`).
- **Constants & Enums**: UPPER_SNAKE_CASE (e.g., `JobStatus.OPEN`, `UserRole.JOB_SEEKER`).

---

## 3. Custom Exception Hierarchy

SmartHire uses a structured hierarchy of custom runtime exceptions derived from `SmartHireException` (`src/main/java/exception/SmartHireException.java`):

```text
SmartHireException (Base Runtime Exception)
 ├── DatabaseAccessException (JDBC & SQL execution errors)
 ├── EntityNotFoundException (Requested model entity missing)
 ├── ValidationException     (Domain & business logic validation failures)
 ├── AuthenticationException (Invalid login credentials or unauthorized role access)
 └── TransactionException    (JDBC transaction commit/rollback failures)
```

### Benefits:
- **Clean API Contracts**: Methods express specific error conditions without cluttering signatures with checked `SQLException`.
- **User-Friendly Error Messages**: Technical SQL exceptions are caught at the DAO layer and rethrown as meaningful business exceptions suitable for display on UI web pages.

---

## 4. Security & Safety Practices

### A. PreparedStatement Usage (SQL Injection Protection)
All SQL queries across every DAO class in `src/main/java/dao/` use parameter binding via `PreparedStatement`. Raw SQL string concatenation is strictly forbidden.

### B. Password Hashing (`PasswordUtil`)
Passwords are cryptographically hashed using SHA-256 with salt support before storage in the database. Plaintext passwords are never logged or stored.

### C. Role-Based Access Control (RBAC)
Servlets verify the logged-in user's role (`UserRole.JOB_SEEKER`, `UserRole.RECRUITER`, `UserRole.ADMIN`) from `HttpSession` before fulfilling requests or displaying views. Unauthorized access attempts trigger automatic redirects to `/login` or `/dashboard`.

---

## 5. Automated Quality Verification

Verification is provided by `src/main/java/util/CodeQualitySmokeTest.java`, validating:
1. **Layer Separation**: Verifies package layout and interface bindings.
2. **Exception Hierarchy**: Confirms inheritance of `SmartHireException`.
3. **Prepared Statements & DAO Architecture**: Validates SQL parameterization safety across DAOs.
4. **Multithreading & Transactions**: Verifies presence of thread pool executors and transaction callbacks.
5. **Password Security**: Validates SHA-256 hash generation and verification.
