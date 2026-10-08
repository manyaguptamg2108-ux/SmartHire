# SmartHire – Live Job Search Integration Documentation

## Overview
SmartHire features a dual-layer job browsing and search architecture that seamlessly unifies **SmartHire Local Database Jobs** created by recruiters with **Real Live Online Listings** from external REST APIs (Adzuna Job Search API).

---

## 1. External API Provider
- **Provider**: Adzuna Job Search API (`https://developer.adzuna.com/`)
- **Country / Market**: India (`in`)
- **Supported Fields**: `externalId`, `title`, `companyName`, `location`, `description`, `salaryMin`, `salaryMax`, `jobType`, `category`, `createdDate`, `sourceName`, `redirectUrl`.

---

## 2. API Configuration
Credentials are strictly decoupled from code and read from environment variables or system properties:
- `ADZUNA_APP_ID` or `-Dadzuna.app.id`
- `ADZUNA_APP_KEY` or `-Dadzuna.app.key`

*Credentials are never displayed in JSPs, browser output, logs, database tables, or exception tracebacks.*

---

## 3. Layered Architecture & Request Flow
```
User Search Input (Keyword, Location, JobType)
  ↓
JobsServlet (/jobs)
  ↓
LiveJobSearchService (Caching & Validation)
  ↓
AdzunaJobApiClient (java.net.http.HttpClient)
  ↓
Adzuna REST API (https://api.adzuna.com/v1/api/jobs/in/search/1)
  ↓
LiveJobResult Model Mapping
  ↓
jobs.jsp View Template (SmartHire Jobs + Live Online Jobs)
```

---

## 4. Local vs External Job Behavior
- **SmartHire Database Jobs**:
  - Indicator: `SmartHire Verified / Local Job`
  - Action: **View Details & Apply** → Opens internal `/job-detail?id=X` workflow & persists `Application` row in MariaDB/H2.
- **Live Online Jobs (API)**:
  - Indicator: `External Live / Adzuna Live`
  - Action: **Apply on Source ↗** → Opens the provider's real listing URL (`redirectUrl`) in a new browser tab. Does NOT fabricate fake local application rows.

---

## 5. Caching & Performance
- `LiveJobSearchService` implements short-lived in-memory caching (`10 minutes` TTL) using `ConcurrentHashMap`.
- Duplicate search queries return cached results instantly, optimizing network performance and respecting API quota limits.

---

## 6. Verification Test Suite
- Test Suite: `util.LiveJobSearchTest`
- Command: `mvn exec:java "-Dexec.mainClass=util.LiveJobSearchTest" "-Dexec.classpathScope=provided"`
