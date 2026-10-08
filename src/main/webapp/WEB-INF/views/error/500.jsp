<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>500 Internal Error - SmartHire</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="auth-wrapper">
        <div class="auth-card empty-state">
            <div class="empty-icon" style="color: var(--danger); font-size: 4rem;">⚠️</div>
            <h1 class="empty-title" style="font-size: 2rem;">500 - Server Error</h1>
            <p class="empty-description">
                An unexpected server error occurred while processing your request. Please try again later.
            </p>
            <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-primary">
                Return to Dashboard
            </a>
        </div>
    </div>
</body>
</html>
