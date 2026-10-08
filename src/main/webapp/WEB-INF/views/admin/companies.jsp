<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Companies - SmartHire Admin</title>
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
                        <h3 class="card-title">Registered Enterprise Companies</h3>
                    </div>

                    <div class="table-responsive">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Company Name</th>
                                    <th>Industry</th>
                                    <th>Location</th>
                                    <th>Website</th>
                                    <th>Recruiter ID</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="c" items="${companies}">
                                    <tr>
                                        <td>#${c.id}</td>
                                        <td><strong>${c.companyName}</strong></td>
                                        <td>${c.industry}</td>
                                        <td>📍 ${c.location}</td>
                                        <td><a href="${c.website}" target="_blank">${c.website}</a></td>
                                        <td>Recruiter #${c.recruiterId}</td>
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
