<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sign In - SmartHire</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="auth-wrapper">
        <div class="auth-card">
            <div class="auth-header" style="text-align: center;">
                <div class="auth-logo" style="margin-bottom: 0.5rem;">
                    <img src="${pageContext.request.contextPath}/images/logo.png" alt="SmartHire Logo" style="max-height: 100px; width: auto; max-width: 100%; object-fit: contain;" />
                </div>
            </div>

            <c:if test="${not empty requestScope.errorMessage}">
                <div class="alert alert-danger">
                    <span>⚠️</span> ${requestScope.errorMessage}
                </div>
            </c:if>

            <c:if test="${not empty requestScope.successMessage}">
                <div class="alert alert-success">
                    <span>✓</span> ${requestScope.successMessage}
                </div>
            </c:if>

            <form action="${pageContext.request.contextPath}/login" method="post">
                <div class="form-group">
                    <label class="form-label" for="email">Email Address</label>
                    <input type="email" id="email" name="email" class="form-control" 
                           placeholder="name@example.com" value="${requestScope.email}" required autofocus />
                </div>

                <div class="form-group">
                    <label class="form-label" for="password">Password</label>
                    <input type="password" id="password" name="password" class="form-control" 
                           placeholder="••••••••" required />
                </div>

                <button type="submit" class="btn btn-primary btn-lg" style="width: 100%;">
                    Sign In
                </button>
            </form>

            <div class="auth-footer">
                Don't have an account? 
                <a href="${pageContext.request.contextPath}/register" style="font-weight: 600;">Create Account</a>
            </div>
        </div>
    </div>
    <script src="${pageContext.request.contextPath}/js/app.js"></script>
</body>
</html>
