<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Scheduled Interviews - SmartHire</title>
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
                        <h3 class="card-title">Interview Schedules</h3>
                    </div>

                    <c:choose>
                        <c:when test="${not empty interviews}">
                            <div class="table-responsive">
                                <table class="table">
                                    <thead>
                                        <tr>
                                            <th>Interview ID</th>
                                            <th>Application ID</th>
                                            <th>Date</th>
                                            <th>Time</th>
                                            <th>Type</th>
                                            <th>Meeting Link / Location</th>
                                            <th>Status</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="interview" items="${interviews}">
                                            <tr>
                                                <td>#${interview.id}</td>
                                                <td>#${interview.applicationId}</td>
                                                <td>📅 ${interview.interviewDate}</td>
                                                <td>⏰ ${interview.interviewTime}</td>
                                                <td><span class="badge badge-info">${interview.interviewType}</span></td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${not empty interview.location}">
                                                            <a href="${interview.location}" target="_blank">${interview.location}</a>
                                                        </c:when>
                                                        <c:otherwise>Online Meeting</c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td>
                                                    <span class="badge badge-${interview.status == 'COMPLETED' ? 'success' : (interview.status == 'CANCELLED' ? 'danger' : 'primary')}">
                                                        ${interview.status}
                                                    </span>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="empty-state">
                                <div class="empty-icon">📅</div>
                                <h3 class="empty-title">No Interviews Scheduled</h3>
                                <p class="empty-description">You currently have no scheduled interviews. When a recruiter schedules a session, it will appear here.</p>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </main>
            
            <jsp:include page="/WEB-INF/views/common/footer.jsp" />
        </div>
    </div>
    <script src="${pageContext.request.contextPath}/js/app.js"></script>
</body>
</html>
