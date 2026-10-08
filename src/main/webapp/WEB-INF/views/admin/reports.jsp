<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Reports - SmartHire Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="app-container">
        <jsp:include page="/WEB-INF/views/common/sidebar.jsp" />
        
        <div class="app-main">
            <jsp:include page="/WEB-INF/views/common/header.jsp" />
            
            <main class="content-body">
                <div class="card mb-4" style="margin-bottom: 2rem;">
                    <div class="card-header">
                        <h3 class="card-title">SmartHire System Performance Summary</h3>
                    </div>
                    
                    <div class="grid-cols-3" style="margin-bottom: 0;">
                        <div class="card metric-card">
                            <div class="metric-icon indigo">📈</div>
                            <div class="metric-content">
                                <span class="metric-value">${userCount}</span>
                                <span class="metric-label">Registered Accounts</span>
                            </div>
                        </div>

                        <div class="card metric-card">
                            <div class="metric-icon green">💼</div>
                            <div class="metric-content">
                                <span class="metric-value">${jobCount}</span>
                                <span class="metric-label">Active &amp; Historical Jobs</span>
                            </div>
                        </div>

                        <div class="card metric-card">
                            <div class="metric-icon teal">📑</div>
                            <div class="metric-content">
                                <span class="metric-value">${applicationCount}</span>
                                <span class="metric-label">Submitted Applications</span>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="grid-cols-2">
                    <div class="card">
                        <h3 class="card-title" style="margin-bottom: 1rem;">Platform Conversion Metrics</h3>
                        <div style="display: flex; flex-direction: column; gap: 1rem;">
                            <div>
                                <div style="display: flex; justify-content: space-between; font-size: 0.85rem; font-weight: 600;">
                                    <span>Applications per Active Job (Avg)</span>
                                    <span>${jobCount > 0 ? (applicationCount / jobCount) : 0}</span>
                                </div>
                                <div class="progress-bar-container">
                                    <div class="progress-bar-fill" style="width: 75%;"></div>
                                </div>
                            </div>

                            <div>
                                <div style="display: flex; justify-content: space-between; font-size: 0.85rem; font-weight: 600;">
                                    <span>Job Seekers to Recruiters Ratio</span>
                                    <span>${recruiterCount > 0 ? (jobSeekerCount / recruiterCount) : 0}:1</span>
                                </div>
                                <div class="progress-bar-container">
                                    <div class="progress-bar-fill" style="width: 60%;"></div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="card">
                        <h3 class="card-title" style="margin-bottom: 1rem;">System Health &amp; Architecture</h3>
                        <div style="font-size: 0.9rem; display: flex; flex-direction: column; gap: 0.75rem;">
                            <div><strong>Framework:</strong> Jakarta Servlet 6.0 / JSP 3.0</div>
                            <div><strong>Database Engine:</strong> MariaDB / MySQL Relational Schema</div>
                            <div><strong>Database Connection Pool:</strong> HikariCP JDBC Manager</div>
                            <div><strong>Security Hash:</strong> SHA-256 Auth Encryption</div>
                        </div>
                    </div>
                </div>
            </main>
            
            <jsp:include page="/WEB-INF/views/common/footer.jsp" />
        </div>
    </div>
    <script src="${pageContext.request.contextPath}/js/app.js"></script>
</body>
</html>
