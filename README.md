# SmartHire – Intelligent Online Job Portal

[![Java](https://img.shields.io/badge/Java-17%2B-blue.svg)](https://www.oracle.com/java/)
[![Jakarta EE](https://img.shields.io/badge/Jakarta%20EE- Servlet%206.0-orange.svg)](https://jakarta.ee/)
[![Maven](https://img.shields.io/badge/Build-Maven-red.svg)](https://maven.apache.org/)
[![Database](https://img.shields.io/badge/Database-MariaDB%20%2F%20MySQL-blue)](https://mariadb.org/)
[![Build Status](https://img.shields.io/badge/Build-PASSING-success.svg)](#build-instructions)

SmartHire is a full-stack, enterprise-grade Java web application built on **Jakarta Servlet 6.0**, **JSP 3.0**, **JDBC Persistence**, **Java Multithreading**, **JDBC Transaction Management**, **Generics/Collections**, and **Role-Based Security Filters**. It delivers an intelligent job search, resume matching, and recruitment management platform.

---

## Table of Contents

- [Project Overview](#project-overview)
- [Objectives](#objectives)
- [Main Features](#main-features)
- [Technology Stack](#technology-stack)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [Requirements](#requirements)
- [Database Setup](#database-setup)
- [Configuration](#configuration)
- [Build Instructions](#build-instructions)
- [Tomcat Deployment](#tomcat-deployment)
- [Run the Application](#run-the-application)
- [Application Flow](#application-flow)
- [User Roles](#user-roles)
- [Testing](#testing)
- [Innovation](#innovation)
- [Rubric Mapping](#rubric-mapping)
- [Documentation Index](#documentation-index)
- [Known Limitations / Future Enhancements](#known-limitations--future-enhancements)

---

## Project Overview

SmartHire bridges the gap between job seekers and recruiters through an intelligent, feature-rich recruitment web application. The platform provides automated resume-job matching, real-time live job search API integration (Adzuna), asynchronous thread-pool background processing for notifications, atomic transaction management for multi-step workflow operations, and role-based portal access for Candidates, Recruiters, and Administrators.

---

## Objectives

1. **Enterprise Layered Architecture**: Clear separation of concerns into Model, View, Controller (Servlet), Service, DAO, Interface, and Utility packages.
2. **Robust Security & Session Management**: Secure SHA-256 password hashing with salt, SQL injection protection via `PreparedStatement`, and declarative RBAC `AuthenticationFilter`.
3. **High Performance & Concurrency**: Multi-threaded execution (`ExecutorService`), thread-safe counters (`AtomicInteger`, `ConcurrentHashMap`), and asynchronous background tasks.
4. **Data Integrity & Consistency**: Explicit JDBC transaction boundaries (`commit`, `rollback`, `setAutoCommit(false)`) ensuring atomic composite operations.
5. **Seamless User Onboarding**: Instant registration-to-login transition with automatic session initialization and role-based landing redirection.

---

## Main Features

- **Automatic Registration & Instant Login**: Instant user creation, password hashing, automated profile creation, and immediate dashboard redirect without re-login.
- **Intelligent Skill & Resume Matching**: Quantitative similarity score calculation matching candidate skills against job requirements.
- **Live Job Search Engine**: External job API integration (Adzuna) providing real-time live job postings alongside internal database jobs.
- **Asynchronous Task Management**: Non-blocking background notification dispatch using Java `ExecutorService` thread pool.
- **Role-Based Dashboard Portals**: Customized interfaces for Job Seekers (profile, applications, interviews), Recruiters (job posting, applicant pipeline, company profile), and Admins (system users, company audits, reports).
- **Atomic Application Workflow**: Application submission, status updates, and candidate alert notifications wrapped in atomic database transactions.

---

## Technology Stack

| Category | Technology / Library | Version / Details |
| :--- | :--- | :--- |
| **JDK** | Java Development Kit | 17 LTS or higher |
| **Web Spec** | Jakarta Servlet / JSP / JSTL | Servlet 6.0, JSP 3.0, JSTL 3.0 |
| **Build Tool** | Apache Maven | 3.8+ |
| **Database** | MariaDB / MySQL / Embedded H2 | MariaDB 12.x / MySQL 8.0+ / H2 2.2+ |
| **App Server** | Apache Tomcat | 10.1+ (Jakarta EE compatible) |
| **Document Processing** | Apache PDFBox & Apache POI | PDFBox 3.0.1 (PDF), POI 5.2.5 (DOCX) |
| **Concurrency** | Java Concurrency API | `ExecutorService`, `AtomicInteger`, `ConcurrentHashMap` |
| **Security** | Security Utilities | SHA-256 with Salt Hashing, PreparedStatements, WebFilter RBAC |

---

## Architecture

SmartHire enforces strict tiered architectural separation:

```text
               JSP / HTML5 / CSS3 / JavaScript (Views)
                                  ↓
          Controller / Servlet Layer (19 @WebServlet Controllers)
                                  │
                  ├── AuthenticationFilter (RBAC & Session Inspection)
                                  │
                                  ▼
            Service Layer (Business Rules & Domain Validation)
             ├── BackgroundTaskManager (ExecutorService Thread Pool)
             └── TransactionManager (JDBC Transaction Boundaries)
                  │       ├── setAutoCommit(false)
                  │       ├── commit() / rollback()
                  │       └── TransactionCallback<T>
                                  ▼
             DAO Layer (GenericDAO<T> & ResultSet Mapping)
                                  ↓
           JDBC Connection Manager (DBUtil with H2 Fallback)
                                  ↓
            MariaDB / MySQL Database (smarthire schema)
```

For full Servlet architectural details, see [docs/architecture/SERVLET_DOCUMENTATION.md](docs/architecture/SERVLET_DOCUMENTATION.md).

---

## Project Structure

```text
SmartHire/
├── README.md                           # Repository Documentation & Guide
├── pom.xml                             # Maven Dependencies & Build Configuration
├── database.sql                        # Database Initializer & SQL Schema
├── .gitignore                          # Git Exclusions
├── .env.example                        # Environment Variable Configuration Template
│
├── docs/                               # Comprehensive Documentation Suite
│   ├── architecture/                   # Architectural & Code Quality Docs
│   │   ├── CODE_QUALITY_DOCUMENTATION.md
│   │   ├── SERVICE_LAYER_DOCUMENTATION.md
│   │   └── SERVLET_DOCUMENTATION.md
│   ├── database/                       # Schema, JDBC & Transaction Docs
│   │   ├── DATABASE_SCHEMA.md
│   │   ├── JDBC_DOCUMENTATION.md
│   │   └── TRANSACTION_DOCUMENTATION.md
│   ├── features/                       # Deep Dives on Core Features
│   │   ├── AUTHENTICATION_DOCUMENTATION.md
│   │   ├── COLLECTIONS_GENERICS_DOCUMENTATION.md
│   │   ├── INNOVATION_DOCUMENTATION.md
│   │   ├── LIVE_JOB_SEARCH_DOCUMENTATION.md
│   │   ├── MULTITHREADING_DOCUMENTATION.md
│   │   ├── REGISTRATION_FLOW_DOCUMENTATION.md
│   │   └── RESUME_MATCHING_DOCUMENTATION.md
│   ├── rubric/                         # Rubric Audit & Grading Matrix
│   │   └── FINAL_RUBRIC_AUDIT.md
│   └── testing/                        # Comprehensive Test Step Reports (Steps 1-11)
│       ├── STEP1_STATUS.md
│       ├── ...
│       └── STEP11_STATUS.md
│
├── scripts/                            # Helper Build & Test Scripts
│   ├── build.bat                       # Automated Maven Package Script
│   ├── run-tests.bat                   # Automated Test Suite Script
│   └── database-setup.txt              # Database Initializer Instructions
│
└── src/
    └── main/
        ├── java/                       # Java Source Code (Package Architecture)
        │   ├── controller/             # Servlet Controllers & Filters
        │   ├── dao/                    # GenericDAO<T> & Data Access Objects
        │   ├── exception/              # Custom Exceptions Hierarchy
        │   ├── interfaces/             # Service & Operations Contracts
        │   ├── model/                  # Domain Model Entities & Enums
        │   ├── service/                # Business Logic & External API Clients
        │   ├── thread/                 # Multithreading & Task Executors
        │   └── util/                   # Connection Manager, Hashing & Test Suites
        │
        ├── resources/                  # Classpath Resources (database.sql)
        │
        └── webapp/                     # Web Assets & View Pages
            ├── index.jsp               # Root Landing / Entry Controller
            ├── css/                    # Modular Stylesheets
            ├── js/                     # Frontend Scripts
            ├── images/                 # Image Assets
            └── WEB-INF/                # Protected Web Resources
                ├── web.xml             # Deployment Descriptor & Error Pages
                └── views/              # Role-Based Modular JSP Views
                    ├── common/         # Shared Headers, Footers & Sidebars
                    ├── error/          # Custom Error Pages (403, 404, 500)
                    ├── admin/          # Admin Portal Views
                    ├── jobseeker/      # Job Seeker Views
                    └── recruiter/      # Recruiter Views
```

---

## Requirements

Before building and deploying SmartHire, ensure the following software is installed:

1. **Java Development Kit (JDK)**: JDK 17 or higher (`java -version`).
2. **Apache Maven**: Version 3.8+ (`mvn -version`).
3. **Database Server**: MariaDB 10.x / 12.x or MySQL 8.0+ running on port 3306.
4. **Web Server**: Apache Tomcat 10.1+ (configured for Jakarta EE 10 / Servlet 6.0).

---

## Database Setup

1. Start your MariaDB or MySQL Database Server on `localhost:3306`.
2. Execute the root `database.sql` script to create the database schema and default seed data:

   ```bash
   mysql -u root -p < database.sql
   ```

   *Alternatively, execute `database.sql` in your SQL client (MySQL Workbench, DBeaver, phpMyAdmin).*

3. The script will initialize:
   - Database: `smarthire`
   - Tables: `users`, `jobs`, `applications`, `interviews`, `notifications`, `companies`, `resume_profiles`, `skills`
   - Default Accounts & Sample Data

*Note: If MySQL is not available during automated tests, SmartHire automatically falls back to an embedded in-memory H2 database.*

---

## Configuration

SmartHire supports flexible database credential configuration via System Properties or Environment Variables:

### Option A: Environment Variables (Recommended)
Set `DB_USER` and `DB_PASSWORD` before starting your terminal or Tomcat server:
- **PowerShell**:
  ```powershell
  $env:DB_USER="root"
  $env:DB_PASSWORD="your_mysql_password"
  ```
- **CMD**:
  ```cmd
  set DB_USER=root
  set DB_PASSWORD=your_mysql_password
  ```

### Option B: JVM System Properties
Pass flags directly during execution or Tomcat JVM parameters:
```bash
-DDB_USER=root -DDB_PASSWORD=your_mysql_password -DDB_URL="jdbc:mysql://localhost:3306/smarthire?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
```

See `.env.example` for details.

---

## Build Instructions

To compile the application and build the web archive (`SmartHire.war`):

```bash
# Clean previous builds and package application
mvn clean package
```

Upon successful build:
- Artifact generated: `target/SmartHire.war`
- Status: `BUILD SUCCESS`

Alternatively, on Windows double-click:
```cmd
scripts\build.bat
```

---

## Tomcat Deployment

1. Ensure your local Tomcat 10.1+ instance is installed.
2. Stop Tomcat if it is running.
3. Copy the compiled `target/SmartHire.war` artifact into Tomcat's `webapps/` folder:
   ```cmd
   copy target\SmartHire.war "C:\apache-tomcat-10.1.x\webapps\"
   ```
4. Start Tomcat:
   ```cmd
   "C:\apache-tomcat-10.1.x\bin\startup.bat"
   ```
5. Tomcat automatically extracts `SmartHire.war` into context `/SmartHire`.

---

## Run the Application

Once Tomcat is started, open your web browser and navigate to the canonical application URL:

👉 **[http://localhost:8080/SmartHire/](http://localhost:8080/SmartHire/)**

---

## Application Flow

```text
Root Application URL (http://localhost:8080/SmartHire/)
                        │
         ┌──────────────┴──────────────┐
         ▼                             ▼
Unauthenticated User           Authenticated User
         │                             │
 Redirect to /login            Redirect to /dashboard
         │                             │
   ┌─────┴────────┐                    ▼
   ▼              ▼           Role-Based Dashboard
/login        /register        (Job Seeker / Recruiter / Admin)
   │              │
   └──────┬───────┘
          ▼
Automatic Registration & Session Creation
          ↓
   Redirect to /dashboard
```

---

## User Roles

| Role | Default Email | Default Password | Accessible Features |
| :--- | :--- | :--- | :--- |
| **Job Seeker** | `john.doe@example.com` | `password123` | Dashboard, Job Search, Live API Search, Resume Matcher, Applications, Interviews, Notifications, Profile, Settings |
| **Recruiter** | `recruiter1@techcorp.com` | `password123` | Dashboard, Post Job, My Jobs, Applicants Management, Company Profile, Interviews, Notifications, Settings |
| **Admin** | `admin@smarthire.com` | `admin123` | Master Dashboard, User Audits, Company Verification, Job Audits, System Reports, Settings |

---

## Testing

SmartHire contains a comprehensive verification test suite (11 test classes) covering every tier of the application.

### Running Test Suites via Maven

```bash
# 1. Code Quality & Security Smoke Test
mvn exec:java "-Dexec.mainClass=util.CodeQualitySmokeTest"

# 2. Canonical Route & Servlet Mapping Test
mvn exec:java "-Dexec.mainClass=util.RouteSmokeTest"

# 3. Automatic Registration & Authentication Flow Test
mvn exec:java "-Dexec.mainClass=util.FullAuthenticationFlowTest"

# 4. DAO Layer CRUD Operations Test
mvn exec:java "-Dexec.mainClass=util.DAOTest"

# 5. Service Layer Logic Test
mvn exec:java "-Dexec.mainClass=util.ServiceTest"

# 6. JDBC Connection Pool Test
mvn exec:java "-Dexec.mainClass=util.JDBCTest"

# 7. Multithreading & Concurrency Test
mvn exec:java "-Dexec.mainClass=util.MultithreadingTest"

# 8. Transaction Commit/Rollback Test
mvn exec:java "-Dexec.mainClass=util.TransactionTest"

# 9. Collections & Generics Test
mvn exec:java "-Dexec.mainClass=util.CollectionsGenericsTest"
```

Or run all tests automatically via script:
```cmd
scripts\run-tests.bat
```

For full test reports, see [docs/testing/](docs/testing/).

---

## Innovation

SmartHire features 6 key technical innovations:

1. **Intelligent Skill Matching Engine**: Algorithmic set intersection calculating matching percentages between candidate skills and job requirements.
2. **Asynchronous Notification Engine**: `ExecutorService` thread pool managing alert dispatches without blocking HTTP request threads.
3. **Thread-Safe Application Tracker**: Lock-free concurrent counter (`ConcurrentHashMap` & `AtomicInteger`) preventing race conditions.
4. **Live External Job Aggregation**: Adzuna REST API client providing real-time external job listings alongside database jobs.
5. **Atomic Multi-Step Transactions**: Transaction-wrapped job application & notification operations maintaining strict ACID database consistency.
6. **Dynamic Resume Document Parser**: Apache PDFBox and POI integration parsing uploaded PDF and Word resumes.

For full details, see [docs/features/INNOVATION_DOCUMENTATION.md](docs/features/INNOVATION_DOCUMENTATION.md).

---

## Rubric Mapping

| Evaluation Criteria | Rubric Marks | Primary Implementation Classes | Documentation |
| :--- | :--- | :--- | :--- |
| **1. Object-Oriented Programming (OOP)** | 10 Marks | `User`, `JobSeeker`, `Recruiter`, `Admin`, `Job`, `Application` | [docs/architecture/CODE_QUALITY_DOCUMENTATION.md](docs/architecture/CODE_QUALITY_DOCUMENTATION.md) |
| **2. Collections & Generics** | 6 Marks | `GenericDAO<T>`, `Set<Skill>`, `List<Job>`, `ConcurrentHashMap` | [docs/features/COLLECTIONS_GENERICS_DOCUMENTATION.md](docs/features/COLLECTIONS_GENERICS_DOCUMENTATION.md) |
| **3. Multithreading & Synchronization** | 4 Marks | `SmartHireTaskExecutor`, `BackgroundTaskManager`, `SmartHireAppListener` | [docs/features/MULTITHREADING_DOCUMENTATION.md](docs/features/MULTITHREADING_DOCUMENTATION.md) |
| **4. Database Access Layer (DAO)** | 7 Marks | 9 DAO implementations (`UserDAO`, `JobDAO`, `ApplicationDAO`, etc.) | [docs/database/DATABASE_SCHEMA.md](docs/database/DATABASE_SCHEMA.md) |
| **5. JDBC Database Connection** | 3 Marks | `DBUtil` connection manager, `PreparedStatement`, auto-close | [docs/database/JDBC_DOCUMENTATION.md](docs/database/JDBC_DOCUMENTATION.md) |
| **6. Transaction Management** | 3 Marks | `TransactionManager`, `ApplicationTransactionService` | [docs/database/TRANSACTION_DOCUMENTATION.md](docs/database/TRANSACTION_DOCUMENTATION.md) |
| **7. Servlets & Web Layer** | 10 Marks | 19 `@WebServlet` controllers, `AuthenticationFilter` RBAC | [docs/architecture/SERVLET_DOCUMENTATION.md](docs/architecture/SERVLET_DOCUMENTATION.md) |
| **8. Code Quality & Architecture** | 5 Marks | Tiered architecture, custom exception hierarchy (`SmartHireException`) | [docs/architecture/CODE_QUALITY_DOCUMENTATION.md](docs/architecture/CODE_QUALITY_DOCUMENTATION.md) |
| **9. Registration & Onboarding Flow** | 5 Marks | `AuthServlet` automatic session, instant `/dashboard` redirect | [docs/features/REGISTRATION_FLOW_DOCUMENTATION.md](docs/features/REGISTRATION_FLOW_DOCUMENTATION.md) |
| **10. Technical Innovation** | 2 Marks | Skill matching algorithm, async tasks, resume parser, live API search | [docs/features/INNOVATION_DOCUMENTATION.md](docs/features/INNOVATION_DOCUMENTATION.md) |

For complete rubric analysis, see [docs/rubric/FINAL_RUBRIC_AUDIT.md](docs/rubric/FINAL_RUBRIC_AUDIT.md).

---

## Documentation Index

- **Architecture**:
  - [SERVLET_DOCUMENTATION.md](docs/architecture/SERVLET_DOCUMENTATION.md)
  - [CODE_QUALITY_DOCUMENTATION.md](docs/architecture/CODE_QUALITY_DOCUMENTATION.md)
  - [SERVICE_LAYER_DOCUMENTATION.md](docs/architecture/SERVICE_LAYER_DOCUMENTATION.md)
- **Database**:
  - [DATABASE_SCHEMA.md](docs/database/DATABASE_SCHEMA.md)
  - [JDBC_DOCUMENTATION.md](docs/database/JDBC_DOCUMENTATION.md)
  - [TRANSACTION_DOCUMENTATION.md](docs/database/TRANSACTION_DOCUMENTATION.md)
- **Features**:
  - [AUTHENTICATION_DOCUMENTATION.md](docs/features/AUTHENTICATION_DOCUMENTATION.md)
  - [MULTITHREADING_DOCUMENTATION.md](docs/features/MULTITHREADING_DOCUMENTATION.md)
  - [COLLECTIONS_GENERICS_DOCUMENTATION.md](docs/features/COLLECTIONS_GENERICS_DOCUMENTATION.md)
  - [INNOVATION_DOCUMENTATION.md](docs/features/INNOVATION_DOCUMENTATION.md)
  - [LIVE_JOB_SEARCH_DOCUMENTATION.md](docs/features/LIVE_JOB_SEARCH_DOCUMENTATION.md)
  - [RESUME_MATCHING_DOCUMENTATION.md](docs/features/RESUME_MATCHING_DOCUMENTATION.md)
  - [REGISTRATION_FLOW_DOCUMENTATION.md](docs/features/REGISTRATION_FLOW_DOCUMENTATION.md)
- **Testing & Status Reports**:
  - [STEP10_STATUS.md](docs/testing/STEP10_STATUS.md)
  - [STEP11_STATUS.md](docs/testing/STEP11_STATUS.md)
- **Rubric Audit**:
  - [FINAL_RUBRIC_AUDIT.md](docs/rubric/FINAL_RUBRIC_AUDIT.md)

---

## Known Limitations / Future Enhancements

- **OAuth2 / Social Login Integration**: Currently supports custom SHA-256 local authentication; future versions can integrate Google/LinkedIn OAuth2 SSO.
- **WebSocket Real-Time Chat**: Notification system currently uses asynchronous background task queue; WebSocket bi-directional chat between recruiters and candidates can be added.
- **Microservices Deployment**: Application is packaged as a monolithic WAR for Servlet 6.0 / Tomcat 10.1; could be modularized into Spring Boot / Quarkus microservices.
