<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<aside class="app-sidebar">
    <div class="sidebar-header" style="padding: 0.5rem 1rem;">
        <a href="${pageContext.request.contextPath}/dashboard" class="sidebar-brand" style="display: flex; align-items: center; justify-content: center; width: 100%;">
            <img src="${pageContext.request.contextPath}/images/logo.png" alt="SmartHire Logo" style="max-height: 48px; max-width: 100%; object-fit: contain; filter: drop-shadow(0 2px 4px rgba(0,0,0,0.2));" />
        </a>
    </div>
    
    <nav class="sidebar-nav">
        <c:choose>
            <%-- JOB SEEKER SIDEBAR --%>
            <c:when test="${sessionScope.user.role == 'JOB_SEEKER' || sessionScope.userRole == 'JOB_SEEKER'}">
                <div class="nav-section-title">Job Seeker Portal</div>
                <a href="${pageContext.request.contextPath}/dashboard" class="nav-link ${activePage == 'dashboard' ? 'active' : ''}">
                    <span class="icon">📊</span> Dashboard
                </a>
                <a href="${pageContext.request.contextPath}/profile" class="nav-link ${activePage == 'profile' ? 'active' : ''}">
                    <span class="icon">👤</span> My Profile
                </a>
                <a href="${pageContext.request.contextPath}/resume-match" class="nav-link ${activePage == 'resume-match' ? 'active' : ''}">
                    <span class="icon">✨</span> Smart Resume Match
                </a>
                <a href="${pageContext.request.contextPath}/jobs" class="nav-link ${activePage == 'jobs' || activePage == 'job-detail' ? 'active' : ''}">
                    <span class="icon">💼</span> Browse Jobs
                </a>
                <a href="${pageContext.request.contextPath}/applications" class="nav-link ${activePage == 'applications' ? 'active' : ''}">
                    <span class="icon">📄</span> Applications
                </a>
                <a href="${pageContext.request.contextPath}/interviews" class="nav-link ${activePage == 'interviews' ? 'active' : ''}">
                    <span class="icon">📅</span> Interviews
                </a>
                <a href="${pageContext.request.contextPath}/notifications" class="nav-link ${activePage == 'notifications' ? 'active' : ''}">
                    <span class="icon">🔔</span> Notifications
                </a>
                <a href="${pageContext.request.contextPath}/settings" class="nav-link ${activePage == 'settings' ? 'active' : ''}">
                    <span class="icon">⚙️</span> Settings
                </a>
            </c:when>

            <%-- RECRUITER SIDEBAR --%>
            <c:when test="${sessionScope.user.role == 'RECRUITER' || sessionScope.userRole == 'RECRUITER'}">
                <div class="nav-section-title">Recruiter Portal</div>
                <a href="${pageContext.request.contextPath}/dashboard" class="nav-link ${activePage == 'dashboard' ? 'active' : ''}">
                    <span class="icon">📊</span> Dashboard
                </a>
                <a href="${pageContext.request.contextPath}/company-profile" class="nav-link ${activePage == 'company-profile' ? 'active' : ''}">
                    <span class="icon">🏢</span> Company Profile
                </a>
                <a href="${pageContext.request.contextPath}/post-job" class="nav-link ${activePage == 'post-job' ? 'active' : ''}">
                    <span class="icon">➕</span> Post Job
                </a>
                <a href="${pageContext.request.contextPath}/my-jobs" class="nav-link ${activePage == 'my-jobs' ? 'active' : ''}">
                    <span class="icon">📋</span> My Jobs
                </a>
                <a href="${pageContext.request.contextPath}/applicants" class="nav-link ${activePage == 'applicants' ? 'active' : ''}">
                    <span class="icon">👥</span> Applicants
                </a>
                <a href="${pageContext.request.contextPath}/interviews" class="nav-link ${activePage == 'interviews' ? 'active' : ''}">
                    <span class="icon">📅</span> Interviews
                </a>
                <a href="${pageContext.request.contextPath}/notifications" class="nav-link ${activePage == 'notifications' ? 'active' : ''}">
                    <span class="icon">🔔</span> Notifications
                </a>
                <a href="${pageContext.request.contextPath}/settings" class="nav-link ${activePage == 'settings' ? 'active' : ''}">
                    <span class="icon">⚙️</span> Settings
                </a>
            </c:when>

            <%-- ADMIN SIDEBAR --%>
            <c:when test="${sessionScope.user.role == 'ADMIN' || sessionScope.userRole == 'ADMIN'}">
                <div class="nav-section-title">System Administration</div>
                <a href="${pageContext.request.contextPath}/dashboard" class="nav-link ${activePage == 'dashboard' ? 'active' : ''}">
                    <span class="icon">📊</span> Dashboard
                </a>
                <a href="${pageContext.request.contextPath}/admin/users" class="nav-link ${activePage == 'admin-users' ? 'active' : ''}">
                    <span class="icon">👥</span> Users
                </a>
                <a href="${pageContext.request.contextPath}/admin/job-seekers" class="nav-link ${activePage == 'admin-jobseekers' ? 'active' : ''}">
                    <span class="icon">🎓</span> Job Seekers
                </a>
                <a href="${pageContext.request.contextPath}/admin/recruiters" class="nav-link ${activePage == 'admin-recruiters' ? 'active' : ''}">
                    <span class="icon">👔</span> Recruiters
                </a>
                <a href="${pageContext.request.contextPath}/admin/companies" class="nav-link ${activePage == 'admin-companies' ? 'active' : ''}">
                    <span class="icon">🏢</span> Companies
                </a>
                <a href="${pageContext.request.contextPath}/admin/jobs" class="nav-link ${activePage == 'admin-jobs' ? 'active' : ''}">
                    <span class="icon">💼</span> Jobs
                </a>
                <a href="${pageContext.request.contextPath}/admin/applications" class="nav-link ${activePage == 'admin-applications' ? 'active' : ''}">
                    <span class="icon">📑</span> Applications
                </a>
                <a href="${pageContext.request.contextPath}/admin/reports" class="nav-link ${activePage == 'admin-reports' ? 'active' : ''}">
                    <span class="icon">📈</span> Reports
                </a>
                <a href="${pageContext.request.contextPath}/admin/settings" class="nav-link ${activePage == 'admin-settings' ? 'active' : ''}">
                    <span class="icon">⚙️</span> Settings
                </a>
            </c:when>
            <%-- GUEST / PUBLIC SIDEBAR --%>
            <c:otherwise>
                <div class="nav-section-title">Main Menu</div>
                <a href="${pageContext.request.contextPath}/" class="nav-link ${activePage == 'landing' || activePage == 'home' ? 'active' : ''}">
                    <span class="icon">🏠</span> Home
                </a>
                <a href="${pageContext.request.contextPath}/jobs" class="nav-link ${activePage == 'jobs' || activePage == 'job-detail' ? 'active' : ''}">
                    <span class="icon">💼</span> Browse Jobs
                </a>
                <a href="${pageContext.request.contextPath}/resume-match" class="nav-link ${activePage == 'resume-match' ? 'active' : ''}">
                    <span class="icon">✨</span> Smart Resume Match
                </a>
                
                <div class="nav-section-title" style="margin-top: 1.25rem;">Recruiters</div>
                <a href="${pageContext.request.contextPath}/post-job" class="nav-link ${activePage == 'post-job' ? 'active' : ''}">
                    <span class="icon">➕</span> Post a Job
                </a>
                <a href="${pageContext.request.contextPath}/login" class="nav-link">
                    <span class="icon">🏢</span> Recruiter Portal
                </a>
            </c:otherwise>
        </c:choose>
    </nav>
    
    <div class="sidebar-footer">
        <c:choose>
            <c:when test="${not empty sessionScope.user}">
                <a href="${pageContext.request.contextPath}/logout" class="nav-link" style="color: #ef4444;">
                    <span class="icon">🚪</span> Sign Out
                </a>
            </c:when>
            <c:otherwise>
                <a href="${pageContext.request.contextPath}/login" class="nav-link" style="color: #2563eb; font-weight: 600;">
                    <span class="icon">🔑</span> Sign In / Register
                </a>
            </c:otherwise>
        </c:choose>
    </div>
</aside>
