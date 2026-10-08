<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Candidate Applicants - SmartHire</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="app-container">
        <jsp:include page="/WEB-INF/views/common/sidebar.jsp" />
        
        <div class="app-main">
            <jsp:include page="/WEB-INF/views/common/header.jsp" />
            
            <main class="content-body">
                <c:if test="${not empty sessionScope.applicantsSuccessMessage}">
                    <div class="alert alert-success alert-auto-dismiss">
                        <span>✓</span> ${sessionScope.applicantsSuccessMessage}
                    </div>
                    <% session.removeAttribute("applicantsSuccessMessage"); %>
                </c:if>

                <c:if test="${not empty sessionScope.applicantsErrorMessage}">
                    <div class="alert alert-danger alert-auto-dismiss">
                        <span>⚠️</span> ${sessionScope.applicantsErrorMessage}
                    </div>
                    <% session.removeAttribute("applicantsErrorMessage"); %>
                </c:if>

                <div class="card">
                    <div class="card-header">
                        <h3 class="card-title">Applicant Review Table</h3>
                        <c:if test="${not empty selectedJobId}">
                            <a href="${pageContext.request.contextPath}/applicants" class="btn btn-sm btn-outline">View All Jobs' Applicants</a>
                        </c:if>
                    </div>

                    <c:choose>
                        <c:when test="${not empty applications}">
                            <div class="table-responsive">
                                <table class="table">
                                    <thead>
                                        <tr>
                                            <th>App ID</th>
                                            <th>Job ID</th>
                                            <th>Candidate ID</th>
                                            <th>Match %</th>
                                            <th>Applied Date</th>
                                            <th>Current Status</th>
                                            <th>Update Status</th>
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
                                                    <span class="badge badge-${app.status == 'SHORTLISTED' ? 'primary' : (app.status == 'REJECTED' ? 'danger' : 'info')}">
                                                        ${app.status}
                                                    </span>
                                                </td>
                                                <td>
                                                    <form action="${pageContext.request.contextPath}/applicants" method="post" style="display: flex; gap: 0.5rem;">
                                                        <input type="hidden" name="applicationId" value="${app.id}" />
                                                        <c:if test="${not empty selectedJobId}">
                                                            <input type="hidden" name="jobId" value="${selectedJobId}" />
                                                        </c:if>
                                                        <select name="status" class="form-control" style="padding: 0.2rem 0.5rem; font-size: 0.8rem; width: 140px;">
                                                            <option value="UNDER_REVIEW" ${app.status == 'UNDER_REVIEW' ? 'selected' : ''}>Under Review</option>
                                                            <option value="SHORTLISTED" ${app.status == 'SHORTLISTED' ? 'selected' : ''}>Shortlist</option>
                                                            <option value="INTERVIEW" ${app.status == 'INTERVIEW' ? 'selected' : ''}>Schedule Interview</option>
                                                            <option value="SELECTED" ${app.status == 'SELECTED' ? 'selected' : ''}>Select / Hire</option>
                                                            <option value="REJECTED" ${app.status == 'REJECTED' ? 'selected' : ''}>Reject</option>
                                                        </select>
                                                        <button type="submit" class="btn btn-sm btn-primary">Update</button>
                                                    </form>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="empty-state">
                                <div class="empty-icon">👥</div>
                                <h3 class="empty-title">No Candidate Applicants</h3>
                                <p class="empty-description">No candidates have applied for your vacancies yet.</p>
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
