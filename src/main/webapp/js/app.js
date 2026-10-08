/**
 * SmartHire Application Client JavaScript
 */
document.addEventListener('DOMContentLoaded', function() {
    // Mobile sidebar toggle
    const toggleBtn = document.getElementById('mobileSidebarToggle');
    const sidebar = document.querySelector('.app-sidebar');

    if (toggleBtn && sidebar) {
        toggleBtn.addEventListener('click', function() {
            sidebar.classList.toggle('show');
        });
    }

    // Auto dismiss alerts after 5 seconds
    const alerts = document.querySelectorAll('.alert-auto-dismiss');
    alerts.forEach(function(alert) {
        setTimeout(function() {
            alert.style.opacity = '0';
            alert.style.transition = 'opacity 0.5s ease';
            setTimeout(function() {
                alert.remove();
            }, 500);
        }, 5000);
    });

    // Confirmation dialogs
    const confirmActions = document.querySelectorAll('[data-confirm]');
    confirmActions.forEach(function(elem) {
        elem.addEventListener('click', function(e) {
            const message = elem.getAttribute('data-confirm') || 'Are you sure you want to proceed?';
            if (!confirm(message)) {
                e.preventDefault();
            }
        });
    });
});
