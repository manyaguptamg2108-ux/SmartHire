<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>404 Page Not Found - SmartHire</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="auth-wrapper">
        <div class="auth-card empty-state">
            <div class="empty-icon" style="color: var(--warning); font-size: 4rem;">🔍</div>
            <h1 class="empty-title" style="font-size: 2rem;">404 - Page Not Found</h1>
            <p class="empty-description">
                The page or resource you are searching for does not exist or has been moved.
            </p>
            <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-primary">
                Return to Dashboard
            </a>
        </div>
    </div>
</body>
</html>
