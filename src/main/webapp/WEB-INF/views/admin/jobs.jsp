<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Jobs Management - SmartHire Admin</title>
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
                        <h3 class="card-title">All System Job Listings</h3>
                    </div>

                    <div class="table-responsive">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Title</th>
                                    <th>Company ID</th>
                                    <th>Type</th>
                                    <th>Location</th>
                                    <th>Salary Range</th>
                                    <th>Status</th>
                                    <th>Deadline</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="job" items="${jobs}">
                                    <tr>
                                        <td>#${job.id}</td>
                                        <td><strong>${job.title}</strong></td>
                                        <td>Company #${job.companyId}</td>
                                        <td><span class="badge badge-info">${job.jobType}</span></td>
                                        <td>📍 ${job.location}</td>
                                        <td>💰 \$${job.salaryMin} - \$${job.salaryMax}</td>
                                        <td><span class="badge badge-${job.status == 'ACTIVE' ? 'success' : 'secondary'}">${job.status}</span></td>
                                        <td>${job.applicationDeadline}</td>
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
