<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Account Settings - SmartHire</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="app-container">
        <jsp:include page="/WEB-INF/views/common/sidebar.jsp" />
        
        <div class="app-main">
            <jsp:include page="/WEB-INF/views/common/header.jsp" />
            
            <main class="content-body">
                <c:if test="${not empty sessionScope.settingsSuccessMessage}">
                    <div class="alert alert-success alert-auto-dismiss">
                        <span>✓</span> ${sessionScope.settingsSuccessMessage}
                    </div>
                    <% session.removeAttribute("settingsSuccessMessage"); %>
                </c:if>

                <c:if test="${not empty sessionScope.settingsErrorMessage}">
                    <div class="alert alert-danger alert-auto-dismiss">
                        <span>⚠️</span> ${sessionScope.settingsErrorMessage}
                    </div>
                    <% session.removeAttribute("settingsErrorMessage"); %>
                </c:if>

                <div class="grid-cols-2">
                    <!-- Account Details Summary -->
                    <div class="card">
                        <div class="card-header">
                            <h3 class="card-title">Account Information</h3>
                        </div>
                        <div style="display: flex; flex-direction: column; gap: 1rem; font-size: 0.95rem;">
                            <div>
                                <strong style="color: var(--text-muted); font-size: 0.75rem; text-transform: uppercase; display: block;">User ID</strong>
                                <span>#${userAccount.id}</span>
                            </div>
                            <div>
                                <strong style="color: var(--text-muted); font-size: 0.75rem; text-transform: uppercase; display: block;">Full Name</strong>
                                <span>${userAccount.name}</span>
                            </div>
                            <div>
                                <strong style="color: var(--text-muted); font-size: 0.75rem; text-transform: uppercase; display: block;">Email Address</strong>
                                <span>${userAccount.email}</span>
                            </div>
                            <div>
                                <strong style="color: var(--text-muted); font-size: 0.75rem; text-transform: uppercase; display: block;">Account Role</strong>
                                <span class="badge badge-primary">${userAccount.role}</span>
                            </div>
                            <div>
                                <strong style="color: var(--text-muted); font-size: 0.75rem; text-transform: uppercase; display: block;">Account Status</strong>
                                <span class="badge badge-success">${userAccount.status}</span>
                            </div>
                        </div>
                    </div>

                    <!-- Security / Change Password Form -->
                    <div class="card">
                        <div class="card-header">
                            <h3 class="card-title">Change Password</h3>
                        </div>
                        <form action="${pageContext.request.contextPath}/settings" method="post">
                            <div class="form-group">
                                <label class="form-label">Current Password</label>
                                <input type="password" name="currentPassword" class="form-control" placeholder="••••••••" required />
                            </div>

                            <div class="form-group">
                                <label class="form-label">New Password</label>
                                <input type="password" name="newPassword" class="form-control" placeholder="••••••••" required />
                            </div>

                            <div class="form-group">
                                <label class="form-label">Confirm New Password</label>
                                <input type="password" name="confirmNewPassword" class="form-control" placeholder="••••••••" required />
                            </div>

                            <button type="submit" class="btn btn-primary">
                                Update Password
                            </button>
                        </form>
                    </div>
                </div>
            </main>
            
            <jsp:include page="/WEB-INF/views/common/footer.jsp" />
        </div>
    </div>
    <script src="${pageContext.request.contextPath}/js/app.js"></script>
</body>
</html>
