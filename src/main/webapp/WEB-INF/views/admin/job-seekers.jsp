<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Job Seekers - SmartHire Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="app-container">
        <jsp:include page="/WEB-INF/views/common/sidebar.jsp" />
        
        <div class="app-main">
            <jsp:include page="/WEB-INF/views/common/header.jsp" />
            
            <main class="content-body">
                <div class="card">
                    <div class="card-header">
                        <h3 class="card-title">Job Seekers Profiles</h3>
                    </div>

                    <div class="table-responsive">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Name</th>
                                    <th>Email</th>
                                    <th>Phone</th>
                                    <th>Location</th>
                                    <th>Profile Completion</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="seeker" items="${seekers}">
                                    <tr>
                                        <td>#${seeker.id}</td>
                                        <td><strong>${seeker.name}</strong></td>
                                        <td>${seeker.email}</td>
                                        <td>${seeker.phone}</td>
                                        <td>📍 ${seeker.location}</td>
                                        <td>
                                            <div style="display: flex; align-items: center; gap: 0.5rem;">
                                                <span>${seeker.profileCompletion}%</span>
                                                <div class="progress-bar-container" style="width: 80px; margin: 0;">
                                                    <div class="progress-bar-fill" style="width: ${seeker.profileCompletion}%;"></div>
                                                </div>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
            </main>
            
            <jsp:include page="/WEB-INF/views/common/footer.jsp" />
        </div>
    </div>
    <script src="${pageContext.request.contextPath}/js/app.js"></script>
</body>
</html>
