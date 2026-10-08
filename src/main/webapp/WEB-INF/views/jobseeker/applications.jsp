<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Applications - SmartHire</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="app-container">
        <jsp:include page="/WEB-INF/views/common/sidebar.jsp" />
        
        <div class="app-main">
            <jsp:include page="/WEB-INF/views/common/header.jsp" />
            
            <main class="content-body">
                <c:if test="${not empty sessionScope.appSuccessMessage}">
                    <div class="alert alert-success alert-auto-dismiss">
                        <span>✓</span> ${sessionScope.appSuccessMessage}
                    </div>
                    <% session.removeAttribute("appSuccessMessage"); %>
                </c:if>

                <c:if test="${not empty sessionScope.appErrorMessage}">
                    <div class="alert alert-danger alert-auto-dismiss">
                        <span>⚠️</span> ${sessionScope.appErrorMessage}
                    </div>
                    <% session.removeAttribute("appErrorMessage"); %>
                </c:if>

                <div class="card">
                    <div class="card-header">
                        <h3 class="card-title">Submitted Applications History</h3>
                    </div>

                    <c:choose>
                        <c:when test="${not empty applications}">
                            <div class="table-responsive">
                                <table class="table">
                                    <thead>
                                        <tr>
                                            <th>ID</th>
                                            <th>Job Title</th>
                                            <th>Match %</th>
                                            <th>Cover Note</th>
                                            <th>Applied Date</th>
                                            <th>Current Status</th>
                                            <th>Action</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="app" items="${applications}">
                                            <c:set var="job" value="${jobMap[app.jobId]}" />
                                            <tr>
                                                <td>#${app.id}</td>
                                                <td>
                                                    <strong>
                                                        <a href="${pageContext.request.contextPath}/job-detail?id=${app.jobId}">
                                                            ${not empty job ? job.title : 'Job #'.concat(app.jobId)}
                                                        </a>
                                                    </strong>
                                                </td>
                                                <td>
                                                    <span class="badge badge-info">${app.matchPercentage}%</span>
                                                </td>
                                                <td>
                                                    <span style="font-size: 0.85rem; color: var(--text-muted);">
                                                        ${not empty app.coverLetter ? app.coverLetter : 'N/A'}
                                                    </span>
                                                </td>
                                                <td>${app.appliedAt.toLocalDate()}</td>
                                                <td>
                                                    <span class="badge badge-${app.status == 'SELECTED' ? 'success' : (app.status == 'REJECTED' || app.status == 'WITHDRAWN' ? 'danger' : (app.status == 'SHORTLISTED' ? 'primary' : 'info'))}">
                                                        ${app.status}
                                                    </span>
                                                </td>
                                                <td>
                                                    <c:if test="${app.status != 'WITHDRAWN' && app.status != 'REJECTED' && app.status != 'SELECTED'}">
                                                        <form action="${pageContext.request.contextPath}/applications" method="post" style="display: inline;">
                                                            <input type="hidden" name="applicationId" value="${app.id}" />
                                                            <button type="submit" class="btn btn-sm btn-secondary" data-confirm="Are you sure you want to withdraw this application?">
                                                                Withdraw
                                                            </button>
                                                        </form>
                                                    </c:if>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="empty-state">
                                <div class="empty-icon">📄</div>
                                <h3 class="empty-title">No Applications Found</h3>
                                <p class="empty-description">You haven't submitted any job applications yet. Browse open listings and apply today!</p>
                                <a href="${pageContext.request.contextPath}/jobs" class="btn btn-primary">Browse Jobs</a>
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
