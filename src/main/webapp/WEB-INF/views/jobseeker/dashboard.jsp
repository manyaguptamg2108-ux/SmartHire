<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Job Seeker Dashboard - SmartHire</title>
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
            background: linear-gradient(135deg, #ffffff 0%, #eff6ff 100%);
            border-radius: 16px;
            padding: 1.1rem 1.35rem;
            border: 1px solid #e2e8f0;
            box-shadow: 0 4px 15px rgba(37, 99, 235, 0.03);
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
            background: linear-gradient(135deg, #2563eb, #4f46e5);
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
            background: linear-gradient(135deg, #2563eb, #4f46e5);
            color: #ffffff;
            padding: 0.5rem 1.2rem;
            border-radius: 50px;
            font-weight: 700;
            font-size: 0.82rem;
            text-decoration: none;
            box-shadow: 0 3px 10px rgba(37, 99, 235, 0.2);
            transition: all 0.2s;
            display: inline-block;
        }
        .btn-sh-primary:hover {
            transform: translateY(-1px);
            box-shadow: 0 5px 14px rgba(37, 99, 235, 0.3);
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
                        <div style="display: inline-flex; align-items: center; gap: 0.35rem; background: #dbeafe; color: #1e40af; font-size: 0.7rem; font-weight: 700; padding: 0.2rem 0.65rem; border-radius: 50px; margin-bottom: 0.4rem;">
                            🎓 Candidate Workspace
                        </div>
                        <h1 class="sh-hero-title-sm">
                            Welcome back, <span class="highlight">${not empty sessionScope.user.name ? sessionScope.user.name : sessionScope.userName}!</span> 👋
                        </h1>
                        <p class="sh-hero-sub-sm">
                            Track your active applications, match your resume against live job vacancies, and schedule upcoming interviews in real-time.
                        </p>
                        <div class="sh-hero-btns">
                            <a href="${pageContext.request.contextPath}/jobs" class="btn-sh-primary">
                                💼 Browse Vacancies →
                            </a>
                            <a href="${pageContext.request.contextPath}/resume-match" class="btn-sh-secondary">
                                ✨ Smart Resume Match
                            </a>
                        </div>
                    </div>

                    <!-- Right Column: Candidate Profile Card -->
                    <div class="sh-demo-card">
                        <span class="sh-demo-badge">${profileCompletion}% COMPLETE</span>
                        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.3rem;">
                            <span style="background: #dcfce7; color: #15803d; font-size: 0.7rem; font-weight: 800; padding: 0.15rem 0.55rem; border-radius: 50px;">
                                🟢 Candidate Profile
                            </span>
                            <span style="font-size: 0.7rem; color: #64748b; font-weight: 600;">Active Account</span>
                        </div>
                        <h3 style="font-size: 1rem; font-weight: 800; color: #0f172a; margin: 0 0 0.15rem 0;">
                            ${not empty jobSeeker.name ? jobSeeker.name : sessionScope.userName}
                        </h3>
                        <p style="font-size: 0.8rem; color: #64748b; font-weight: 600; margin-bottom: 0.5rem;">
                            ${not empty jobSeeker.location ? jobSeeker.location : 'Location Not Set'} • ${not empty jobSeeker.education ? jobSeeker.education : 'Education Pending'}
                        </p>
                        <div style="background: #e2e8f0; border-radius: 50px; height: 6px; overflow: hidden; margin-bottom: 0.6rem;">
                            <div style="background: linear-gradient(90deg, #2563eb, #10b981); height: 100%; width: ${profileCompletion}%;"></div>
                        </div>
                        <a href="${pageContext.request.contextPath}/profile" style="display: block; text-align: center; background: #f1f5f9; color: #2563eb; font-weight: 700; font-size: 0.78rem; padding: 0.35rem; border-radius: 8px; text-decoration: none;">
                            Update Candidate Profile →
                        </a>
                    </div>
                </section>

                <!-- 2. QUICK STATS STRIP (4 Mini Cards) -->
                <section class="sh-stats-strip">
                    <div class="sh-stat-mini-card">
                        <div class="sh-stat-icon" style="background: #eff6ff; color: #2563eb;">💼</div>
                        <div>
                            <div style="font-size: 0.95rem; font-weight: 900; color: #0f172a;">${activeJobsCount}</div>
                            <div style="font-size: 0.72rem; color: #64748b; font-weight: 600;">Active Jobs Available</div>
                        </div>
                    </div>
                    <div class="sh-stat-mini-card">
                        <div class="sh-stat-icon" style="background: #f0fdf4; color: #16a34a;">📄</div>
                        <div>
                            <div style="font-size: 0.95rem; font-weight: 900; color: #0f172a;">${applicationsCount}</div>
                            <div style="font-size: 0.72rem; color: #64748b; font-weight: 600;">Applications Submitted</div>
                        </div>
                    </div>
                    <div class="sh-stat-mini-card">
                        <div class="sh-stat-icon" style="background: #faf5ff; color: #9333ea;">📅</div>
                        <div>
                            <div style="font-size: 0.95rem; font-weight: 900; color: #0f172a;">${interviewsCount}</div>
                            <div style="font-size: 0.72rem; color: #64748b; font-weight: 600;">Interviews Scheduled</div>
                        </div>
                    </div>
                    <div class="sh-stat-mini-card">
                        <div class="sh-stat-icon" style="background: #fff7ed; color: #ea580c;">🔔</div>
                        <div>
                            <div style="font-size: 0.95rem; font-weight: 900; color: #0f172a;">${unreadNotificationsCount}</div>
                            <div style="font-size: 0.72rem; color: #64748b; font-weight: 600;">Unread Alerts</div>
                        </div>
                    </div>
                </section>

                <!-- 3. ACTION PANELS (3 Columns) -->
                <section class="sh-action-panels">
                    <!-- Panel 1: Recent Applications -->
                    <div class="sh-panel-card">
                        <div>
                            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.4rem;">
                                <h3 style="font-size: 0.9rem; font-weight: 800; color: #1e3a8a; margin: 0;">
                                    My Applications
                                </h3>
                                <a href="${pageContext.request.contextPath}/applications" style="font-size: 0.72rem; font-weight: 700; color: #2563eb;">View All →</a>
                            </div>
                            <c:choose>
                                <c:when test="${not empty recentApplications}">
                                    <ul style="padding-left: 0; list-style: none; margin: 0 0 0.6rem 0; font-size: 0.78rem; color: #475569;">
                                        <c:forEach var="app" items="${recentApplications}">
                                            <li style="display: flex; justify-content: space-between; align-items: center; padding: 0.3rem 0; border-bottom: 1px solid #f1f5f9;">
                                                <span>Application #${app.id}</span>
                                                <span class="badge badge-info" style="font-size: 0.68rem; padding: 0.12rem 0.45rem;">${app.status}</span>
                                            </li>
                                        </c:forEach>
                                    </ul>
                                </c:when>
                                <c:otherwise>
                                    <p style="font-size: 0.78rem; color: #64748b; margin: 0.4rem 0 0.75rem 0;">No active applications submitted yet.</p>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <a href="${pageContext.request.contextPath}/jobs" class="btn-sh-primary" style="text-align: center; font-size: 0.78rem; padding: 0.4rem;">
                            Explore Job Market →
                        </a>
                    </div>

                    <!-- Panel 2: Career Tools -->
                    <div class="sh-panel-card">
                        <div>
                            <h3 style="font-size: 0.9rem; font-weight: 800; color: #0f172a; margin: 0 0 0.35rem 0;">
                                Career Tools
                            </h3>
                            <ul style="padding-left: 1rem; margin: 0 0 0.6rem 0; font-size: 0.78rem; color: #475569; line-height: 1.45;">
                                <li style="margin-bottom: 0.2rem;"><a href="${pageContext.request.contextPath}/resume-match" style="color: #475569; font-weight: 600;">Resume Analyzer</a>: Skill match score</li>
                                <li style="margin-bottom: 0.2rem;"><a href="${pageContext.request.contextPath}/profile" style="color: #475569; font-weight: 600;">Profile Optimization</a>: Update details</li>
                                <li><a href="${pageContext.request.contextPath}/notifications" style="color: #475569; font-weight: 600;">Alert Notifications</a>: View alerts</li>
                            </ul>
                        </div>
                        <a href="${pageContext.request.contextPath}/resume-match" class="btn-sh-secondary" style="text-align: center; font-size: 0.78rem; padding: 0.4rem; border-color: #2563eb; color: #2563eb;">
                            Launch Resume Matcher →
                        </a>
                    </div>

                    <!-- Panel 3: Quick Job Search Form -->
                    <div class="sh-panel-card" style="background: #f8fafc; border-color: #cbd5e1;">
                        <div>
                            <h3 style="font-size: 0.9rem; font-weight: 800; color: #0f172a; margin: 0 0 0.35rem 0;">
                                Quick Job Search
                            </h3>
                            <form action="${pageContext.request.contextPath}/jobs" method="get" class="sh-search-mini-form">
                                <input type="text" name="keyword" class="sh-form-input-compact" placeholder="Title, skill, or keyword..." />
                                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 0.35rem;">
                                    <select name="location" class="sh-form-input-compact">
                                        <option value="">Any Location</option>
                                        <option value="Bangalore">Bangalore</option>
                                        <option value="Mumbai">Mumbai</option>
                                        <option value="Delhi">Delhi</option>
                                        <option value="Remote">Remote</option>
                                    </select>
                                    <select name="jobType" class="sh-form-input-compact">
                                        <option value="">Any Type</option>
                                        <option value="FULL_TIME">Full Time</option>
                                        <option value="PART_TIME">Part Time</option>
                                        <option value="REMOTE">Remote</option>
                                    </select>
                                </div>
                                <button type="submit" class="btn-sh-primary" style="width: 100%; font-size: 0.78rem; padding: 0.4rem; border: none; margin-top: 0.1rem; cursor: pointer;">
                                    Search Jobs →
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
