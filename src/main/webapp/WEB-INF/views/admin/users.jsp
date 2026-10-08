<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Users Administration - SmartHire</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="app-container">
        <jsp:include page="/WEB-INF/views/common/sidebar.jsp" />
        
        <div class="app-main">
            <jsp:include page="/WEB-INF/views/common/header.jsp" />
            
            <main class="content-body">
                <c:if test="${not empty sessionScope.adminSuccessMessage}">
                    <div class="alert alert-success alert-auto-dismiss">
                        <span>✓</span> ${sessionScope.adminSuccessMessage}
                    </div>
                    <% session.removeAttribute("adminSuccessMessage"); %>
                </c:if>

                <c:if test="${not empty sessionScope.adminErrorMessage}">
                    <div class="alert alert-danger alert-auto-dismiss">
                        <span>⚠️</span> ${sessionScope.adminErrorMessage}
                    </div>
                    <% session.removeAttribute("adminErrorMessage"); %>
                </c:if>

                <div class="card">
                    <div class="card-header">
                        <h3 class="card-title">Registered User Accounts</h3>
                    </div>

                    <div class="table-responsive">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Full Name</th>
                                    <th>Email Address</th>
                                    <th>Role</th>
                                    <th>Status</th>
                                    <th>Created At</th>
                                    <th>Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="u" items="${users}">
                                    <tr>
                                        <td>#${u.id}</td>
                                        <td><strong>${u.name}</strong></td>
                                        <td>${u.email}</td>
                                        <td>
                                            <span class="badge badge-${u.role == 'ADMIN' ? 'warning' : (u.role == 'RECRUITER' ? 'info' : 'primary')}">
                                                ${u.role}
                                            </span>
                                        </td>
                                        <td>
                                            <span class="badge badge-${u.status == 'ACTIVE' ? 'success' : 'danger'}">
                                                ${u.status}
                                            </span>
                                        </td>
                                        <td>${u.createdAt.toLocalDate()}</td>
                                        <td>
                                            <c:if test="${u.role != 'ADMIN'}">
                                                <form action="${pageContext.request.contextPath}/admin/users" method="post" style="display: inline;">
                                                    <input type="hidden" name="userId" value="${u.id}" />
                                                    <c:choose>
                                                        <c:when test="${u.status == 'ACTIVE'}">
                                                            <input type="hidden" name="action" value="deactivate" />
                                                            <button type="submit" class="btn btn-sm btn-danger" data-confirm="Deactivate user #${u.id}?">Deactivate</button>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <input type="hidden" name="action" value="activate" />
                                                            <button type="submit" class="btn btn-sm btn-primary">Activate</button>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </form>
                                            </c:if>
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
