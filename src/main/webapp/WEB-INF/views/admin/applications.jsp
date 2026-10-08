<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Applications - SmartHire Admin</title>
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
                        <h3 class="card-title">All System Applications</h3>
                    </div>

                    <div class="table-responsive">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Job ID</th>
                                    <th>Job Seeker ID</th>
                                    <th>Match %</th>
                                    <th>Applied Date</th>
                                    <th>Status</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="app" items="${applications}">
                                    <tr>
                                        <td>#${app.id}</td>
                                        <td>Job #${app.jobId}</td>
                                        <td>Candidate #${app.jobSeekerId}</td>
                                        <td><span class="badge badge-info">${app.matchPercentage}%</span></td>
                                        <td>${app.appliedAt.toLocalDate()}</td>
                                        <td>
                                            <span class="badge badge-${app.status == 'SELECTED' ? 'success' : (app.status == 'REJECTED' ? 'danger' : 'primary')}">
                                                ${app.status}
                                            </span>
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
