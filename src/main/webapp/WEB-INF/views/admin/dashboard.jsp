<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Control Center - SmartHire</title>
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
            background: linear-gradient(135deg, #ffffff 0%, #f3e8ff 100%);
            border-radius: 16px;
            padding: 1.1rem 1.35rem;
            border: 1px solid #e2e8f0;
            box-shadow: 0 4px 15px rgba(126, 34, 206, 0.03);
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
            background: linear-gradient(135deg, #7e22ce, #6b21a8);
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
            background: linear-gradient(135deg, #7e22ce, #6b21a8);
            color: #ffffff;
            padding: 0.5rem 1.2rem;
            border-radius: 50px;
            font-weight: 700;
            font-size: 0.82rem;
            text-decoration: none;
            box-shadow: 0 3px 10px rgba(126, 34, 206, 0.2);
            transition: all 0.2s;
            display: inline-block;
        }
        .btn-sh-primary:hover {
            transform: translateY(-1px);
            box-shadow: 0 5px 14px rgba(126, 34, 206, 0.3);
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
                        <div style="display: inline-flex; align-items: center; gap: 0.35rem; background: #f3e8ff; color: #7e22ce; font-size: 0.7rem; font-weight: 700; padding: 0.2rem 0.65rem; border-radius: 50px; margin-bottom: 0.4rem;">
                            🛡️ System Administration Center
                        </div>
                        <h1 class="sh-hero-title-sm">
                            System Control & <span class="highlight">Analytics Center</span> 📊
                        </h1>
                        <p class="sh-hero-sub-sm">
                            Platform-wide management of user accounts, candidate profiles, recruiter organizations, job vacancies, and system performance metrics.
                        </p>
                        <div class="sh-hero-btns">
                            <a href="${pageContext.request.contextPath}/admin/users" class="btn-sh-primary">
                                👥 Manage Users →
                            </a>
                            <a href="${pageContext.request.contextPath}/admin/reports" class="btn-sh-secondary">
                                📈 Analytics Reports
                            </a>
                        </div>
                    </div>

                    <!-- Right Column: Admin System Status Card -->
                    <div class="sh-demo-card">
                        <span class="sh-demo-badge">SYSTEM HEALTH OK</span>
                        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.3rem;">
                            <span style="background: #f3e8ff; color: #7e22ce; font-size: 0.7rem; font-weight: 800; padding: 0.15rem 0.55rem; border-radius: 50px;">
                                🛡️ Admin Root
                            </span>
                            <span style="font-size: 0.7rem; color: #64748b; font-weight: 600;">System Active</span>
                        </div>
                        <h3 style="font-size: 1rem; font-weight: 800; color: #0f172a; margin: 0 0 0.15rem 0;">
                            SmartHire Platform Overview
                        </h3>
                        <p style="font-size: 0.8rem; color: #64748b; font-weight: 600; margin-bottom: 0.5rem;">
                            ${totalUsers} Total Users • ${totalCompanies} Companies Registered
                        </p>
                        <a href="${pageContext.request.contextPath}/admin/jobs" style="display: block; text-align: center; background: #f1f5f9; color: #7e22ce; font-weight: 700; font-size: 0.78rem; padding: 0.35rem; border-radius: 8px; text-decoration: none;">
                            Monitor ${totalJobs} Job Openings →
                        </a>
                    </div>
                </section>

                <!-- 2. QUICK STATS STRIP (4 Mini Cards) -->
                <section class="sh-stats-strip">
                    <div class="sh-stat-mini-card">
                        <div class="sh-stat-icon" style="background: #f3e8ff; color: #7e22ce;">👥</div>
                        <div>
                            <div style="font-size: 0.95rem; font-weight: 900; color: #0f172a;">${totalUsers}</div>
                            <div style="font-size: 0.72rem; color: #64748b; font-weight: 600;">Registered Accounts</div>
                        </div>
                    </div>
                    <div class="sh-stat-mini-card">
                        <div class="sh-stat-icon" style="background: #eff6ff; color: #2563eb;">🎓</div>
                        <div>
                            <div style="font-size: 0.95rem; font-weight: 900; color: #0f172a;">${totalJobSeekers}</div>
                            <div style="font-size: 0.72rem; color: #64748b; font-weight: 600;">Job Seekers</div>
                        </div>
                    </div>
                    <div class="sh-stat-mini-card">
                        <div class="sh-stat-icon" style="background: #fff7ed; color: #ea580c;">👔</div>
                        <div>
                            <div style="font-size: 0.95rem; font-weight: 900; color: #0f172a;">${totalRecruiters}</div>
                            <div style="font-size: 0.72rem; color: #64748b; font-weight: 600;">Recruiters</div>
                        </div>
                    </div>
                    <div class="sh-stat-mini-card">
                        <div class="sh-stat-icon" style="background: #f0fdf4; color: #16a34a;">🏢</div>
                        <div>
                            <div style="font-size: 0.95rem; font-weight: 900; color: #0f172a;">${totalCompanies}</div>
                            <div style="font-size: 0.72rem; color: #64748b; font-weight: 600;">Companies</div>
                        </div>
                    </div>
                </section>

                <!-- 3. ACTION PANELS (3 Columns) -->
                <section class="sh-action-panels">
                    <!-- Panel 1: User Administration -->
                    <div class="sh-panel-card">
                        <div>
                            <h3 style="font-size: 0.9rem; font-weight: 800; color: #7e22ce; margin: 0 0 0.35rem 0;">
                                User Administration
                            </h3>
                            <ul style="padding-left: 1rem; margin: 0 0 0.6rem 0; font-size: 0.78rem; color: #475569; line-height: 1.45;">
                                <li style="margin-bottom: 0.2rem;"><a href="${pageContext.request.contextPath}/admin/users" style="color: #475569; font-weight: 600;">User Accounts</a>: ${totalUsers} total</li>
                                <li style="margin-bottom: 0.2rem;"><a href="${pageContext.request.contextPath}/admin/job-seekers" style="color: #475569; font-weight: 600;">Job Seekers</a>: ${totalJobSeekers} candidates</li>
                                <li><a href="${pageContext.request.contextPath}/admin/recruiters" style="color: #475569; font-weight: 600;">Recruiters</a>: ${totalRecruiters} company leads</li>
                            </ul>
                        </div>
                        <a href="${pageContext.request.contextPath}/admin/users" class="btn-sh-primary" style="text-align: center; font-size: 0.78rem; padding: 0.4rem;">
                            Manage All Users →
                        </a>
                    </div>

                    <!-- Panel 2: Enterprise & Job Monitoring -->
                    <div class="sh-panel-card">
                        <div>
                            <h3 style="font-size: 0.9rem; font-weight: 800; color: #0f172a; margin: 0 0 0.35rem 0;">
                                Platform Modules
                            </h3>
                            <ul style="padding-left: 1rem; margin: 0 0 0.6rem 0; font-size: 0.78rem; color: #475569; line-height: 1.45;">
                                <li style="margin-bottom: 0.2rem;"><a href="${pageContext.request.contextPath}/admin/companies" style="color: #475569; font-weight: 600;">Companies</a>: ${totalCompanies} orgs</li>
                                <li style="margin-bottom: 0.2rem;"><a href="${pageContext.request.contextPath}/admin/jobs" style="color: #475569; font-weight: 600;">Job Vacancies</a>: ${totalJobs} active</li>
                                <li><a href="${pageContext.request.contextPath}/admin/applications" style="color: #475569; font-weight: 600;">Applications</a>: ${totalApplications} total</li>
                            </ul>
                        </div>
                        <a href="${pageContext.request.contextPath}/admin/jobs" class="btn-sh-secondary" style="text-align: center; font-size: 0.78rem; padding: 0.4rem; border-color: #7e22ce; color: #7e22ce;">
                            View All Jobs →
                        </a>
                    </div>

                    <!-- Panel 3: System Analytics & Reports -->
                    <div class="sh-panel-card" style="background: #f8fafc; border-color: #cbd5e1;">
                        <div>
                            <h3 style="font-size: 0.9rem; font-weight: 800; color: #0f172a; margin: 0 0 0.35rem 0;">
                                Analytics & Reports
                            </h3>
                            <p style="font-size: 0.78rem; color: #475569; margin-bottom: 0.6rem;">
                                Monitor real-time conversion rates, active applicant ratios, and system health metrics.
                            </p>
                            <a href="${pageContext.request.contextPath}/admin/reports" class="btn-sh-primary" style="display: block; text-align: center; font-size: 0.78rem; padding: 0.4rem; border: none; margin-top: 0.1rem; cursor: pointer; text-decoration: none;">
                                Open Reports Dashboard →
                            </a>
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
