# SmartHire – Servlet Architecture & Web Request Lifecycle Documentation

## Overview

SmartHire implements a modern Jakarta Servlet 6.0 web architecture following the classic Model-View-Controller (MVC) pattern. Servlets act as entry-point Controllers handling incoming HTTP requests, managing session authentication, orchestrating business calls via Service layers, and delegating UI rendering to JavaServer Pages (JSP).

---

## 1. Servlet Architecture & Component Hierarchy

```text
                        +----------------------------+
                        |  Browser / Client Request  |
                        +----------------------------+
                                      |
                                      v
                        +----------------------------+
                        |   AuthenticationFilter     |
                        | (RBAC & Session Inspection)|
                        +----------------------------+
                                      |
                                      v
                        +----------------------------+
                        |    Servlet / Controller    |
                        |   (e.g., JobsServlet)      |
                        +----------------------------+
                                  /        \
                                 /          \
        (Data Fetching & Logic) /            \ (View Delegation)
                               v              v
                     +------------------+   +-------------------+
                     |  Service Layer   |   | RequestDispatcher |
                     +------------------+   +-------------------+
                               |                      |
                               v                      v
                     +------------------+   +-------------------+
                     |    DAO Layer     |   |   JSP View Page   |
                     +------------------+   +-------------------+
                               |                      |
                               v                      v
                     +------------------+   +-------------------+
                     | MariaDB / MySQL  |   |   HTML5 Response  |
                     +------------------+   +-------------------+
```

---

## 2. Servlet Lifecycle Management

Every Servlet in SmartHire extends `jakarta.servlet.http.HttpServlet` and adheres to the standard Servlet lifecycle:

1. **Instantiation & Initialization (`init()`)**:
   - Executed once when the Servlet container loads the class.
   - Instantiates required Service layer dependencies (e.g., `this.userService = new UserService()`).
2. **Request Processing (`service()`, `doGet()`, `doPost()`)**:
   - Evaluates incoming request HTTP method and dispatches to appropriate lifecycle method (`doGet` for read/rendering, `doPost` for state changes).
3. **Destruction (`destroy()`)**:
   - Called when the application is undeployed or web server shuts down, freeing resources.

---

## 3. GET vs. POST Usage Conventions

SmartHire strictly separates read-only HTTP operations from state-modifying operations:

| HTTP Method | Purpose | SmartHire Controller Examples |
| :--- | :--- | :--- |
| **`doGet()`** | Rendering JSP views, retrieving search results, fetching profile data, displaying dashboards. | `JobsServlet`, `DashboardServlet`, `ProfileServlet`, `ApplicationsServlet`, `AdminUsersServlet` |
| **`doPost()`** | Processing form submissions, authentication, creating records, updating status, deleting entities. | `AuthServlet` (`/login`, `/register`), `PostJobServlet`, `ApplicationsServlet` (withdraw app), `AdminUsersServlet` (toggle user active status) |

---

## 4. Request & Response Objects

### A. `HttpServletRequest`
- **Request Parameters (`request.getParameter()`)**: Extracting form inputs, query parameters, search keywords.
- **Request Attributes (`request.setAttribute()`)**: Passing data models (`List<Job>`, `User`, statistics `Map`) from Servlet to JSP for request-scope rendering.

### B. `HttpServletResponse`
- **Response Headers**: Setting anti-caching headers (`Cache-Control`, `Pragma`, `Expires`) in `AuthenticationFilter`.
- **Status Codes**: Returning explicit HTTP status codes (`sc_OK`, `sc_FORBIDDEN`, `sc_NOT_FOUND`, `sc_INTERNAL_SERVER_ERROR`).

---

## 5. View Dispatching: `RequestDispatcher` vs. `sendRedirect()`

- **`RequestDispatcher.forward(request, response)`**:
  - Used for internal server-side forwarding to JSP templates inside `/WEB-INF/views/`.
  - Preserves request attributes and request URL in browser.
  - *Example*: Forwarding to `/WEB-INF/views/jobseeker/jobs.jsp`.
- **`HttpServletResponse.sendRedirect(contextPath + "/url")`**:
  - Used after POST processing (Post-Redirect-Get pattern) or authentication redirects to prevent duplicate form submissions.
  - *Example*: Redirecting to `/dashboard` after successful login or `/login` upon logout.

---

## 6. Session Management & Security

- **Session Data**: `HttpSession` stores strictly non-sensitive identity attributes:
  - `userId` (Integer)
  - `userRole` (String: `"JOB_SEEKER"`, `"RECRUITER"`, `"ADMIN"`)
  - `userName` (String)
  - *Security Enforcement*: Passwords, SHA-256 hashes, and database credentials are NEVER stored in session.
- **Session Fixation Prevention**: `AuthServlet.handleLogin()` explicitly invalidates existing sessions (`oldSession.invalidate()`) before creating a fresh authenticated session (`request.getSession(true)`).
- **Logout Handling**: `AuthServlet.handleLogout()` invalidates active session and redirects to `/login`.

---

## 7. Role-Based Access Control (`AuthenticationFilter`)

Protected routes are intercepted by `@WebFilter` in `AuthenticationFilter.java`:
1. **Unauthenticated Access**: Redirects to `/login`.
2. **Admin Route Protection (`/admin/*`)**: Restricts access to users with `UserRole.ADMIN`; forwards unauthorized requests to `/WEB-INF/views/error/403.jsp`.
3. **Recruiter Route Protection (`/post-job`, `/my-jobs`, `/applicants`, `/company-profile`)**: Blocks `JOB_SEEKER` role; forwards to `403.jsp`.

---

## 8. HTTP Error Handling & Error Pages

Configured in `WEB-INF/web.xml` and handled gracefully in Servlets:
- **403 Access Denied**: `/WEB-INF/views/error/403.jsp`
- **404 Page Not Found**: `/WEB-INF/views/error/404.jsp`
- **500 Server Error**: `/WEB-INF/views/error/500.jsp`

*Security Assurance*: Stack traces, raw SQL queries, and internal database details are caught by Service/Servlet try-catch blocks and never exposed to client browsers.

---

## 9. Servlet Inventory & Mappings

| Servlet Class | Route Mapping (`@WebServlet`) | Supported Methods | Description |
| :--- | :--- | :--- | :--- |
| `HomeServlet` | `/` | `doGet` | Redirects root URL to `/login` or `/dashboard`. |
| `AuthServlet` | `/login`, `/register`, `/logout` | `doGet`, `doPost` | Login, registration, session creation, and logout. |
| `DashboardServlet` | `/dashboard` | `doGet` | Role-based dynamic dashboard controller. |
| `JobsServlet` | `/jobs` | `doGet` | Job searching and browsing for candidates. |
| `JobDetailServlet` | `/job-detail` | `doGet`, `doPost` | Displays job description and handles candidate applications. |
| `ApplicationsServlet` | `/applications` | `doGet`, `doPost` | Displays candidate applications and handles withdrawal. |
| `ProfileServlet` | `/profile` | `doGet`, `doPost` | Job seeker profile display and update. |
| `InterviewsServlet` | `/interviews` | `doGet` | Displays scheduled interviews for job seeker. |
| `NotificationsServlet` | `/notifications` | `doGet`, `doPost` | System notification inbox and mark-as-read. |
| `CompanyProfileServlet` | `/company-profile` | `doGet`, `doPost` | Recruiter company profile creation and management. |
| `PostJobServlet` | `/post-job` | `doGet`, `doPost` | Recruiter job vacancy posting controller. |
| `RecruiterJobsServlet` | `/my-jobs` | `doGet`, `doPost` | Recruiter active/closed jobs management. |
| `RecruiterApplicants` | `/applicants` | `doGet`, `doPost` | Recruiter applicant review and status updates. |
| `AdminUsersServlet` | `/admin/users` | `doGet`, `doPost` | Admin user account listing and toggle status. |
| `AdminJobsServlet` | `/admin/jobs` | `doGet`, `doPost` | Admin system-wide job management. |
| `AdminCompaniesServlet`| `/admin/companies` | `doGet`, `doPost` | Admin corporate entity management. |
| `AdminApplications` | `/admin/applications` | `doGet` | Admin system-wide application tracking. |
| `AdminReportsServlet` | `/admin/reports` | `doGet` | Admin analytical metrics and reports. |
| `AdminSettingsServlet`| `/admin/settings` | `doGet`, `doPost` | Admin system configuration settings. |
