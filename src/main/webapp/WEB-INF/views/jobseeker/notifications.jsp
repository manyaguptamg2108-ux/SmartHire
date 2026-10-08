<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Notifications - SmartHire</title>
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
                        <h3 class="card-title">My Notifications</h3>
                    </div>

                    <c:choose>
                        <c:when test="${not empty notifications}">
                            <div style="display: flex; flex-direction: column; gap: 1rem;">
                                <c:forEach var="notif" items="${notifications}">
                                    <div style="padding: 1.25rem; border-radius: var(--radius-md); border: 1px solid var(--border-color); background: ${notif.read ? 'var(--bg-card)' : 'var(--primary-light)'}; display: flex; justify-content: space-between; align-items: flex-start;">
                                        <div>
                                            <div style="display: flex; align-items: center; gap: 0.75rem; margin-bottom: 0.4rem;">
                                                <span class="badge badge-primary">${notif.notificationType}</span>
                                                <h4 style="font-weight: 700; font-size: 1rem; color: var(--text-main);">${notif.title}</h4>
                                            </div>
                                            <p style="font-size: 0.9rem; color: var(--text-main); margin-bottom: 0.4rem;">${notif.message}</p>
                                            <span style="font-size: 0.75rem; color: var(--text-muted);">${notif.createdAt}</span>
                                        </div>

                                        <c:if test="${!notif.read}">
                                            <form action="${pageContext.request.contextPath}/notifications" method="post">
                                                <input type="hidden" name="notificationId" value="${notif.id}" />
                                                <button type="submit" class="btn btn-sm btn-outline">Mark Read</button>
                                            </form>
                                        </c:if>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="empty-state">
                                <div class="empty-icon">🔔</div>
                                <h3 class="empty-title">No Notifications</h3>
                                <p class="empty-description">You're all caught up! New application updates and messages will appear here.</p>
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
