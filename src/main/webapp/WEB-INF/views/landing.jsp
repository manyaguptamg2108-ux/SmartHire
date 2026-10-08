<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SmartHire - Intelligent Job Matching & Recruitment</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        /* Zero-Scroll Single Desktop Screen Optimization */
        html, body {
            height: 100%;
            margin: 0;
            padding: 0;
            background-color: #f8fafc;
            overflow: hidden; /* Prevents vertical & horizontal scroll */
        }
        .app-container {
            height: 100vh;
            display: flex;
            overflow: hidden;
        }
        .app-main {
            flex: 1;
            display: flex;
            flex-direction: column;
            height: 100vh;
            overflow: hidden;
            background: #f8fafc;
        }
        .landing-main-wrap {
            flex: 1;
            max-width: 1360px;
            width: 100%;
            margin: 0 auto;
            padding: 0.6rem 1.25rem;
            display: flex;
            flex-direction: column;
            justify-content: space-between;
            box-sizing: border-box;
            gap: 0.6rem;
            overflow: hidden;
        }
        .sh-hero-compact {
            display: grid;
            grid-template-columns: 1.25fr 0.75fr;
            gap: 1.25rem;
            align-items: center;
            background: linear-gradient(135deg, #ffffff 0%, #eff6ff 100%);
            border-radius: 16px;
            padding: 0.85rem 1.25rem;
            border: 1px solid #e2e8f0;
            box-shadow: 0 4px 15px rgba(37, 99, 235, 0.03);
        }
        .sh-hero-title-sm {
            font-size: 1.85rem;
            font-weight: 900;
            line-height: 1.15;
            color: #0f172a;
            margin: 0 0 0.25rem 0;
            letter-spacing: -0.02em;
        }
        .sh-hero-title-sm .highlight {
            background: linear-gradient(135deg, #2563eb, #4f46e5);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
        }
        .sh-hero-sub-sm {
            font-size: 0.82rem;
            color: #475569;
            line-height: 1.35;
            margin: 0 0 0.6rem 0;
            max-width: 520px;
        }
        .sh-hero-btns {
            display: flex;
            align-items: center;
            gap: 0.6rem;
        }
        .btn-sh-primary {
            background: linear-gradient(135deg, #2563eb, #4f46e5);
            color: #ffffff;
            padding: 0.45rem 1.1rem;
            border-radius: 50px;
            font-weight: 700;
            font-size: 0.8rem;
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
            padding: 0.45rem 1.1rem;
            border-radius: 50px;
            font-weight: 700;
            font-size: 0.8rem;
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
        /* Demo Card Styling */
        .sh-demo-card {
            background: #ffffff;
            border-radius: 14px;
            padding: 0.85rem;
            border: 1px solid #e2e8f0;
            box-shadow: 0 6px 16px rgba(0, 0, 0, 0.03);
            position: relative;
        }
        .sh-demo-badge {
            position: absolute;
            top: -9px;
            right: 12px;
            background: #f1f5f9;
            color: #64748b;
            font-size: 0.6rem;
            font-weight: 800;
            letter-spacing: 0.04em;
            padding: 0.15rem 0.5rem;
            border-radius: 50px;
            border: 1px solid #cbd5e1;
            text-transform: uppercase;
        }
        /* 4-Card Quick Strip */
        .sh-stats-strip {
            display: grid;
            grid-template-columns: repeat(4, 1fr);
            gap: 0.65rem;
        }
        .sh-stat-mini-card {
            background: #ffffff;
            border-radius: 12px;
            padding: 0.55rem 0.75rem;
            border: 1px solid #e2e8f0;
            display: flex;
            align-items: center;
            gap: 0.5rem;
            box-shadow: 0 2px 6px rgba(0,0,0,0.015);
        }
        .sh-stat-icon {
            width: 32px;
            height: 32px;
            border-radius: 8px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 1rem;
            flex-shrink: 0;
        }
        /* 3-Column Action Panels */
        .sh-action-panels {
            display: grid;
            grid-template-columns: 1fr 1fr 1.15fr;
            gap: 0.65rem;
        }
        .sh-panel-card {
            background: #ffffff;
            border-radius: 14px;
            padding: 0.75rem 0.95rem;
            border: 1px solid #e2e8f0;
            display: flex;
            flex-direction: column;
            justify-content: space-between;
        }
        .sh-search-mini-form {
            display: flex;
            flex-direction: column;
            gap: 0.35rem;
        }
        .sh-form-input-compact {
            padding: 0.35rem 0.55rem;
            border-radius: 7px;
            border: 1px solid #cbd5e1;
            font-size: 0.78rem;
            width: 100%;
            box-sizing: border-box;
        }
    </style>
</head>
<body>
    <div class="app-container">
        <!-- Sidebar Navigation -->
        <jsp:include page="/WEB-INF/views/common/sidebar.jsp">
            <jsp:param name="activePage" value="landing"/>
        </jsp:include>

        <!-- Main Content Area -->
        <div class="app-main">
            <!-- Header Navbar -->
            <header class="app-header" style="background: #ffffff; padding: 0.4rem 1.5rem; border-bottom: 1px solid #e2e8f0; display: flex; align-items: center; justify-content: space-between; height: 52px; flex-shrink: 0;">
                <div style="display: flex; align-items: center; gap: 1rem;">
                    <a href="${pageContext.request.contextPath}/" style="display: flex; align-items: center; gap: 0.4rem; text-decoration: none;">
                        <img src="${pageContext.request.contextPath}/images/logo.png" alt="SmartHire Logo" style="height: 32px; width: auto; object-fit: contain;" />
                    </a>
                    <span style="font-size: 0.78rem; font-weight: 600; color: #64748b; border-left: 1px solid #cbd5e1; padding-left: 0.75rem;">
                        Intelligent Career & Recruitment Platform
                    </span>
                </div>
                <div style="display: flex; align-items: center; gap: 0.6rem;">
                    <a href="${pageContext.request.contextPath}/jobs" style="font-size: 0.8rem; font-weight: 600; color: #475569; text-decoration: none; padding: 0.3rem 0.6rem;">
                        🔍 Search Jobs
                    </a>
                    <c:choose>
                        <c:when test="${not empty sessionScope.user}">
                            <a href="${pageContext.request.contextPath}/dashboard" class="btn-sh-primary" style="font-size: 0.78rem; padding: 0.35rem 0.9rem;">
                                Go to Dashboard →
                            </a>
                        </c:when>
                        <c:otherwise>
                            <a href="${pageContext.request.contextPath}/login" class="btn-sh-secondary" style="font-size: 0.78rem; padding: 0.35rem 0.9rem;">
                                Sign In
                            </a>
                            <a href="${pageContext.request.contextPath}/register" class="btn-sh-primary" style="font-size: 0.78rem; padding: 0.35rem 0.9rem;">
                                Get Started
                            </a>
                        </c:otherwise>
                    </c:choose>
                </div>
            </header>

            <!-- Landing Body Container (No vertical scroll) -->
            <main class="landing-main-wrap">
                
                <!-- 1. COMPACT HERO SECTION -->
                <section class="sh-hero-compact">
                    <div>
                        <div style="display: inline-flex; align-items: center; gap: 0.35rem; background: #dbeafe; color: #1e40af; font-size: 0.68rem; font-weight: 700; padding: 0.15rem 0.55rem; border-radius: 50px; margin-bottom: 0.35rem;">
                            ✨ Intelligent Career & Skill Matching
                        </div>
                        <h1 class="sh-hero-title-sm">
                            Find the Right <span class="highlight">Opportunity.</span>
                        </h1>
                        <p class="sh-hero-sub-sm">
                            SmartHire connects candidate skills with real-world job requirements using intelligent match scoring, resume analysis, and real-time application tracking.
                        </p>
                        <div class="sh-hero-btns">
                            <a href="${pageContext.request.contextPath}/jobs" class="btn-sh-primary">
                                💼 Browse Jobs →
                            </a>
                            <a href="${pageContext.request.contextPath}/resume-match" class="btn-sh-secondary">
                                ✨ Smart Resume Match
                            </a>
                        </div>
                    </div>

                    <!-- Right Column: Demo Match Preview Card -->
                    <div class="sh-demo-card">
                        <span class="sh-demo-badge">DEMO PREVIEW</span>
                        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.25rem;">
                            <span style="background: #dcfce7; color: #15803d; font-size: 0.68rem; font-weight: 800; padding: 0.12rem 0.5rem; border-radius: 50px;">
                                🟢 92% Match Score
                            </span>
                            <span style="font-size: 0.68rem; color: #64748b; font-weight: 600;">Full-Time</span>
                        </div>
                        <h3 style="font-size: 0.98rem; font-weight: 800; color: #0f172a; margin: 0 0 0.1rem 0;">
                            Senior Software Engineer
                        </h3>
                        <p style="font-size: 0.78rem; color: #64748b; font-weight: 600; margin-bottom: 0.4rem;">
                            TechCorp Solutions • Bangalore / Remote
                        </p>
                        <div style="display: flex; gap: 0.3rem; flex-wrap: wrap; margin-bottom: 0.5rem;">
                            <span style="background: #eff6ff; color: #1d4ed8; font-size: 0.68rem; font-weight: 700; padding: 0.1rem 0.4rem; border-radius: 5px;">Java</span>
                            <span style="background: #eff6ff; color: #1d4ed8; font-size: 0.68rem; font-weight: 700; padding: 0.1rem 0.4rem; border-radius: 5px;">Spring Boot</span>
                            <span style="background: #eff6ff; color: #1d4ed8; font-size: 0.68rem; font-weight: 700; padding: 0.1rem 0.4rem; border-radius: 5px;">SQL</span>
                            <span style="background: #eff6ff; color: #1d4ed8; font-size: 0.68rem; font-weight: 700; padding: 0.1rem 0.4rem; border-radius: 5px;">React</span>
                        </div>
                        <a href="${pageContext.request.contextPath}/jobs" style="display: block; text-align: center; background: #f1f5f9; color: #2563eb; font-weight: 700; font-size: 0.75rem; padding: 0.3rem; border-radius: 7px; text-decoration: none;">
                            View Details in Job Search →
                        </a>
                    </div>
                </section>

                <!-- 2. QUICK STATS & FEATURE STRIP (4 Mini Cards) -->
                <section class="sh-stats-strip">
                    <div class="sh-stat-mini-card">
                        <div class="sh-stat-icon" style="background: #eff6ff; color: #2563eb;">⚡</div>
                        <div>
                            <div style="font-size: 0.78rem; font-weight: 800; color: #0f172a;">Smart Job Matching</div>
                            <div style="font-size: 0.68rem; color: #64748b;">Algorithms align your skills</div>
                        </div>
                    </div>
                    <div class="sh-stat-mini-card">
                        <div class="sh-stat-icon" style="background: #f0fdf4; color: #16a34a;">📄</div>
                        <div>
                            <div style="font-size: 0.78rem; font-weight: 800; color: #0f172a;">Resume Parser</div>
                            <div style="font-size: 0.68rem; color: #64748b;">PDF & DOCX text extraction</div>
                        </div>
                    </div>
                    <div class="sh-stat-mini-card">
                        <div class="sh-stat-icon" style="background: #faf5ff; color: #9333ea;">📊</div>
                        <div>
                            <div style="font-size: 0.78rem; font-weight: 800; color: #0f172a;">Application Tracking</div>
                            <div style="font-size: 0.68rem; color: #64748b;">Real-time status updates</div>
                        </div>
                    </div>
                    <div class="sh-stat-mini-card">
                        <div class="sh-stat-icon" style="background: #fff7ed; color: #ea580c;">👔</div>
                        <div>
                            <div style="font-size: 0.78rem; font-weight: 800; color: #0f172a;">Recruiter Suite</div>
                            <div style="font-size: 0.68rem; color: #64748b;">Job postings & candidate review</div>
                        </div>
                    </div>
                </section>

                <!-- 3. ACTION PANELS & QUICK SEARCH ROW -->
                <section class="sh-action-panels">
                    <!-- Panel 1: For Job Seekers -->
                    <div class="sh-panel-card">
                        <div>
                            <h3 style="font-size: 0.88rem; font-weight: 800; color: #1e3a8a; margin: 0 0 0.25rem 0;">
                                For Job Seekers
                            </h3>
                            <ul style="padding-left: 0.9rem; margin: 0 0 0.5rem 0; font-size: 0.75rem; color: #475569; line-height: 1.4;">
                                <li>Discover curated job openings</li>
                                <li>Upload resume for instant skill match</li>
                                <li>Track interview schedules & responses</li>
                            </ul>
                        </div>
                        <a href="${pageContext.request.contextPath}/register" class="btn-sh-primary" style="text-align: center; font-size: 0.75rem; padding: 0.35rem;">
                            Join as Candidate →
                        </a>
                    </div>

                    <!-- Panel 2: For Recruiters -->
                    <div class="sh-panel-card">
                        <div>
                            <h3 style="font-size: 0.88rem; font-weight: 800; color: #7c2d12; margin: 0 0 0.25rem 0;">
                                For Employers
                            </h3>
                            <ul style="padding-left: 0.9rem; margin: 0 0 0.5rem 0; font-size: 0.75rem; color: #475569; line-height: 1.4;">
                                <li>Post new job listings in minutes</li>
                                <li>Filter applicants by match score</li>
                                <li>Schedule and manage interviews</li>
                            </ul>
                        </div>
                        <a href="${pageContext.request.contextPath}/register" class="btn-sh-secondary" style="text-align: center; font-size: 0.75rem; padding: 0.35rem; border-color: #ea580c; color: #ea580c;">
                            Start Hiring →
                        </a>
                    </div>

                    <!-- Panel 3: Quick Job Search Form -->
                    <div class="sh-panel-card" style="background: #f8fafc; border-color: #cbd5e1;">
                        <div>
                            <h3 style="font-size: 0.88rem; font-weight: 800; color: #0f172a; margin: 0 0 0.3rem 0;">
                                Quick Job Search
                            </h3>
                            <form action="${pageContext.request.contextPath}/jobs" method="get" class="sh-search-mini-form">
                                <input type="text" name="keyword" class="sh-form-input-compact" placeholder="Title, skill, or keyword..." />
                                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 0.3rem;">
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
                                <button type="submit" class="btn-sh-primary" style="width: 100%; font-size: 0.75rem; padding: 0.35rem; border: none; margin-top: 0.1rem; cursor: pointer;">
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
