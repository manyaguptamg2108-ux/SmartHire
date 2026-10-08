<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Create Account - SmartHire</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="auth-wrapper">
        <div class="auth-card" style="max-width: 480px;">
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

            <form action="${pageContext.request.contextPath}/register" method="post">
                <div class="form-group">
                    <label class="form-label" for="name">Full Name</label>
                    <input type="text" id="name" name="name" class="form-control" 
                           placeholder="John Doe" value="${requestScope.name}" required />
                </div>

                <div class="form-group">
                    <label class="form-label" for="email">Email Address</label>
                    <input type="email" id="email" name="email" class="form-control" 
                           placeholder="name@example.com" value="${requestScope.email}" required />
                </div>

                <div class="form-group">
                    <label class="form-label" for="role">I want to join as</label>
                    <select id="role" name="role" class="form-control" required>
                        <option value="JOB_SEEKER" ${requestScope.role == 'JOB_SEEKER' ? 'selected' : ''}>Job Seeker (Looking for opportunities)</option>
                        <option value="RECRUITER" ${requestScope.role == 'RECRUITER' ? 'selected' : ''}>Recruiter (Hiring talent)</option>
                    </select>
                </div>

                <div class="form-group">
                    <label class="form-label" for="password">Password</label>
                    <input type="password" id="password" name="password" class="form-control" 
                           placeholder="••••••••" required />
                </div>

                <div class="form-group">
                    <label class="form-label" for="confirmPassword">Confirm Password</label>
                    <input type="password" id="confirmPassword" name="confirmPassword" class="form-control" 
                           placeholder="••••••••" required />
                </div>

                <button type="submit" class="btn btn-primary btn-lg" style="width: 100%;">
                    Complete Registration
                </button>
            </form>

            <div class="auth-footer">
                Already have an account? 
                <a href="${pageContext.request.contextPath}/login" style="font-weight: 600;">Sign In</a>
            </div>
        </div>
    </div>
    <script src="${pageContext.request.contextPath}/js/app.js"></script>
</body>
</html>
