<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Recruiter Dashboard - SmartHire</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        /* Canonical Compact Dashboard Layout & Responsive Rhythm */
        html, body {
            margin: 0;
            padding: 0;
            background-color: #f8fafc;
            min-height: 100vh;
        }
        .app-container {
            display: flex;
            min-height: 100vh;
        }
        .app-main {
            flex: 1;
            display: flex;
            flex-direction: column;
            min-height: 100vh;
            background: #f8fafc;
            margin-left: 240px;
        }
        .landing-main-wrap {
            flex: 1;
            max-width: 1360px;
            width: 100%;
            margin: 0 auto;
            padding: 1.25rem 1.5rem;
            display: flex;
            flex-direction: column;
            gap: 1.25rem;
            box-sizing: border-box;
        }
        .sh-hero-compact {
            display: grid;
            grid-template-columns: 1.25fr 0.75fr;
            gap: 1.25rem;
            align-items: center;
            background: linear-gradient(135deg, #ffffff 0%, #fff7ed 100%);
            border-radius: 16px;
            padding: 1.1rem 1.35rem;
            border: 1px solid #e2e8f0;
            box-shadow: 0 4px 15px rgba(234, 88, 12, 0.03);
        }
        .sh-hero-title-sm {
            font-size: 1.85rem;
            font-weight: 900;
            line-height: 1.15;
            color: #0f172a;
            margin: 0 0 0.35rem 0;
            letter-spacing: -0.02em;
        }
        .sh-hero-title-sm .highlight {
            background: linear-gradient(135deg, #ea580c, #c2410c);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
        }
        .sh-hero-sub-sm {
            font-size: 0.85rem;
            color: #475569;
            line-height: 1.4;
            margin: 0 0 0.75rem 0;
            max-width: 540px;
        }
        .sh-hero-btns {
            display: flex;
            align-items: center;
            gap: 0.75rem;
        }
        .btn-sh-primary {
            background: linear-gradient(135deg, #ea580c, #c2410c);
            color: #ffffff;
            padding: 0.5rem 1.2rem;
            border-radius: 50px;
            font-weight: 700;
            font-size: 0.82rem;
            text-decoration: none;
            box-shadow: 0 3px 10px rgba(234, 88, 12, 0.2);
            transition: all 0.2s;
            display: inline-block;
        }
        .btn-sh-primary:hover {
            transform: translateY(-1px);
            box-shadow: 0 5px 14px rgba(234, 88, 12, 0.3);
            color: #ffffff;
        }
        .btn-sh-secondary {
            background: #ffffff;
            color: #1e293b;
            padding: 0.5rem 1.2rem;
            border-radius: 50px;
            font-weight: 700;
            font-size: 0.82rem;
            border: 1px solid #cbd5e1;
            text-decoration: none;
            transition: all 0.2s;
            display: inline-block;
        }
        .btn-sh-secondary:hover {
            background: #f8fafc;
            border-color: #94a3b8;
            color: #0f172a;
        }
        .sh-demo-card {
            background: #ffffff;
            border-radius: 14px;
            padding: 1rem;
            border: 1px solid #e2e8f0;
            box-shadow: 0 6px 16px rgba(0, 0, 0, 0.03);
            position: relative;
        }
        .sh-demo-badge {
            position: absolute;
            top: -10px;
            right: 14px;
            background: #f1f5f9;
            color: #64748b;
            font-size: 0.65rem;
            font-weight: 800;
            letter-spacing: 0.04em;
            padding: 0.15rem 0.55rem;
            border-radius: 50px;
            border: 1px solid #cbd5e1;
            text-transform: uppercase;
        }
        .sh-stats-strip {
            display: grid;
            grid-template-columns: repeat(4, 1fr);
            gap: 1rem;
        }
        .sh-stat-mini-card {
            background: #ffffff;
            border-radius: 14px;
            padding: 0.75rem 1rem;
            border: 1px solid #e2e8f0;
            display: flex;
            align-items: center;
            gap: 0.75rem;
            box-shadow: 0 2px 8px rgba(0,0,0,0.02);
        }
        .sh-stat-icon {
            width: 38px;
            height: 38px;
            border-radius: 10px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 1.1rem;
            flex-shrink: 0;
        }
        .sh-action-panels {
            display: grid;
            grid-template-columns: 1fr 1fr 1.15fr;
            gap: 1.25rem;
            align-items: stretch;
        }
        .sh-panel-card {
            background: #ffffff;
            border-radius: 16px;
            padding: 1rem 1.15rem;
            border: 1px solid #e2e8f0;
            display: flex;
            flex-direction: column;
            justify-content: space-between;
            box-shadow: 0 4px 12px rgba(0,0,0,0.02);
        }
        .sh-search-mini-form {
            display: flex;
            flex-direction: column;
            gap: 0.45rem;
        }
        .sh-form-input-compact {
            padding: 0.45rem 0.65rem;
            border-radius: 8px;
            border: 1px solid #cbd5e1;
            font-size: 0.8rem;
            width: 100%;
            box-sizing: border-box;
        }
        @media (max-width: 1024px) {
            .sh-hero-compact { grid-template-columns: 1fr; }
            .sh-stats-strip { grid-template-columns: repeat(2, 1fr); }
            .sh-action-panels { grid-template-columns: 1fr; }
        }
        @media (max-width: 640px) {
            .app-main { margin-left: 0 !important; }
            .sh-stats-strip { grid-template-columns: 1fr; }
        }
    </style>
</head>
<body>
    <div class="app-container">
        <!-- Sidebar Navigation -->
        <jsp:include page="/WEB-INF/views/common/sidebar.jsp">
            <jsp:param name="activePage" value="dashboard"/>
        </jsp:include>

        <!-- Main Content Area -->
        <div class="app-main">
            <!-- Header Navbar -->
            <jsp:include page="/WEB-INF/views/common/header.jsp" />

            <!-- Canonical Dashboard Main Wrap -->
            <main class="landing-main-wrap">
                
                <!-- 1. HERO COMPACT SECTION -->
                <section class="sh-hero-compact">
                    <div>
                        <div style="display: inline-flex; align-items: center; gap: 0.35rem; background: #fff7ed; color: #c2410c; font-size: 0.7rem; font-weight: 700; padding: 0.2rem 0.65rem; border-radius: 50px; margin-bottom: 0.4rem;">
                            👔 Employer Recruitment Suite
                        </div>
                        <h1 class="sh-hero-title-sm">
                            Welcome, <span class="highlight">${not empty sessionScope.user.name ? sessionScope.user.name : sessionScope.userName}!</span> 👋
                        </h1>
                        <p class="sh-hero-sub-sm">
                            Company: <strong>${not empty company ? company.companyName : 'Profile Incomplete'}</strong> • Manage job listings, review candidate match scores, and organize interviews.
                        </p>
                        <div class="sh-hero-btns">
                            <a href="${pageContext.request.contextPath}/post-job" class="btn-sh-primary">
                                ➕ Post New Job →
                            </a>
                            <a href="${pageContext.request.contextPath}/company-profile" class="btn-sh-secondary">
                                🏢 Company Settings
                            </a>
                        </div>
                    </div>

                    <!-- Right Column: Recruiter Profile Summary Card -->
                    <div class="sh-demo-card">
                        <span class="sh-demo-badge">VERIFIED RECRUITER</span>
                        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.3rem;">
                            <span style="background: #fff7ed; color: #ea580c; font-size: 0.7rem; font-weight: 800; padding: 0.15rem 0.55rem; border-radius: 50px;">
                                🏢 ${not empty company ? company.companyName : 'SmartHire Partner'}
                            </span>
                            <span style="font-size: 0.7rem; color: #64748b; font-weight: 600;">Recruiter Suite</span>
                        </div>
                        <h3 style="font-size: 1rem; font-weight: 800; color: #0f172a; margin: 0 0 0.15rem 0;">
                            ${not empty recruiter ? recruiter.name : sessionScope.userName}
                        </h3>
                        <p style="font-size: 0.8rem; color: #64748b; font-weight: 600; margin-bottom: 0.5rem;">
                            Designation: ${not empty recruiter.designation ? recruiter.designation : 'Recruitment Specialist'}
                        </p>
                        <a href="${pageContext.request.contextPath}/my-jobs" style="display: block; text-align: center; background: #f1f5f9; color: #ea580c; font-weight: 700; font-size: 0.78rem; padding: 0.35rem; border-radius: 8px; text-decoration: none;">
                            Manage ${activeJobsCount} Active Vacancies →
                        </a>
                    </div>
                </section>

                <!-- 2. QUICK STATS STRIP (4 Mini Cards) -->
                <section class="sh-stats-strip">
                    <div class="sh-stat-mini-card">
                        <div class="sh-stat-icon" style="background: #fff7ed; color: #ea580c;">📋</div>
                        <div>
                            <div style="font-size: 0.95rem; font-weight: 900; color: #0f172a;">${activeJobsCount}</div>
                            <div style="font-size: 0.72rem; color: #64748b; font-weight: 600;">Active Posted Jobs</div>
                        </div>
                    </div>
                    <div class="sh-stat-mini-card">
                        <div class="sh-stat-icon" style="background: #f0fdf4; color: #16a34a;">👥</div>
                        <div>
                            <div style="font-size: 0.95rem; font-weight: 900; color: #0f172a;">${totalApplicantsCount}</div>
                            <div style="font-size: 0.72rem; color: #64748b; font-weight: 600;">Total Candidate Applications</div>
                        </div>
                    </div>
                    <div class="sh-stat-mini-card">
                        <div class="sh-stat-icon" style="background: #eff6ff; color: #2563eb;">📅</div>
                        <div>
                            <div style="font-size: 0.95rem; font-weight: 900; color: #0f172a;">${interviewsCount}</div>
                            <div style="font-size: 0.72rem; color: #64748b; font-weight: 600;">Scheduled Interviews</div>
                        </div>
                    </div>
                    <div class="sh-stat-mini-card">
                        <div class="sh-stat-icon" style="background: #faf5ff; color: #9333ea;">🏢</div>
                        <div>
                            <div style="font-size: 0.82rem; font-weight: 800; color: #0f172a;">${not empty company ? company.companyName : 'SmartHire'}</div>
                            <div style="font-size: 0.72rem; color: #64748b; font-weight: 600;">Company Account</div>
                        </div>
                    </div>
                </section>

                <!-- 3. ACTION PANELS (3 Columns) -->
                <section class="sh-action-panels">
                    <!-- Panel 1: Recent Applicants -->
                    <div class="sh-panel-card">
                        <div>
                            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.4rem;">
                                <h3 style="font-size: 0.9rem; font-weight: 800; color: #7c2d12; margin: 0;">
                                    Recent Job Applicants
                                </h3>
                                <a href="${pageContext.request.contextPath}/applicants" style="font-size: 0.72rem; font-weight: 700; color: #ea580c;">View All →</a>
                            </div>
                            <c:choose>
                                <c:when test="${not empty recentApplications}">
                                    <ul style="padding-left: 0; list-style: none; margin: 0 0 0.6rem 0; font-size: 0.78rem; color: #475569;">
                                        <c:forEach var="app" items="${recentApplications}">
                                            <li style="display: flex; justify-content: space-between; align-items: center; padding: 0.3rem 0; border-bottom: 1px solid #f1f5f9;">
                                                <span>App #${app.id} (Job #${app.jobId})</span>
                                                <span class="badge badge-info" style="font-size: 0.68rem; padding: 0.12rem 0.45rem;">${app.matchPercentage}% Match</span>
                                            </li>
                                        </c:forEach>
                                    </ul>
                                </c:when>
                                <c:otherwise>
                                    <p style="font-size: 0.78rem; color: #64748b; margin: 0.4rem 0 0.75rem 0;">No applicant records found for active listings.</p>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <a href="${pageContext.request.contextPath}/applicants" class="btn-sh-primary" style="text-align: center; font-size: 0.78rem; padding: 0.4rem;">
                            Review All Candidates →
                        </a>
                    </div>

                    <!-- Panel 2: Recruiter Management Shortcuts -->
                    <div class="sh-panel-card">
                        <div>
                            <h3 style="font-size: 0.9rem; font-weight: 800; color: #0f172a; margin: 0 0 0.35rem 0;">
                                Recruiter Suite Actions
                            </h3>
                            <ul style="padding-left: 1rem; margin: 0 0 0.6rem 0; font-size: 0.78rem; color: #475569; line-height: 1.45;">
                                <li style="margin-bottom: 0.2rem;"><a href="${pageContext.request.contextPath}/post-job" style="color: #475569; font-weight: 600;">Post Vacancy</a>: Publish job listing</li>
                                <li style="margin-bottom: 0.2rem;"><a href="${pageContext.request.contextPath}/my-jobs" style="color: #475569; font-weight: 600;">My Listings</a>: Edit active jobs</li>
                                <li><a href="${pageContext.request.contextPath}/company-profile" style="color: #475569; font-weight: 600;">Company Profile</a>: Enterprise details</li>
                            </ul>
                        </div>
                        <a href="${pageContext.request.contextPath}/post-job" class="btn-sh-secondary" style="text-align: center; font-size: 0.78rem; padding: 0.4rem; border-color: #ea580c; color: #ea580c;">
                            ➕ Publish New Vacancy →
                        </a>
                    </div>

                    <!-- Panel 3: Quick Applicant Search -->
                    <div class="sh-panel-card" style="background: #f8fafc; border-color: #cbd5e1;">
                        <div>
                            <h3 style="font-size: 0.9rem; font-weight: 800; color: #0f172a; margin: 0 0 0.35rem 0;">
                                Quick Applicant Search
                            </h3>
                            <form action="${pageContext.request.contextPath}/applicants" method="get" class="sh-search-mini-form">
                                <input type="text" name="keyword" class="sh-form-input-compact" placeholder="Candidate skill or name..." />
                                <select name="status" class="sh-form-input-compact">
                                    <option value="">All Application Statuses</option>
                                    <option value="APPLIED">Applied</option>
                                    <option value="SHORTLISTED">Shortlisted</option>
                                    <option value="REJECTED">Rejected</option>
                                </select>
                                <button type="submit" class="btn-sh-primary" style="width: 100%; font-size: 0.78rem; padding: 0.4rem; border: none; margin-top: 0.1rem; cursor: pointer;">
                                    Search Candidates →
                                </button>
                            </form>
                        </div>
                    </div>
                </section>

            </main>
            
            <jsp:include page="/WEB-INF/views/common/footer.jsp" />
        </div>
    </div>
    <script src="${pageContext.request.contextPath}/js/app.js"></script>
</body>
</html>
