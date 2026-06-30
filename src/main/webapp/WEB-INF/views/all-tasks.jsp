<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<c:set var="pageTitle" value="Tasks" scope="request" />
<!DOCTYPE html>
<html lang="en">
<head>
<%@ include file="fragments/head.jspf" %>
</head>
<body>
<%@ include file="fragments/navbar.jspf" %>
    <div class="container pb-5">
        <a href="/all-projects" class="d-inline-block mb-3">&larr; Back to projects</a>
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h1 class="h3 page-title mb-0">Tasks</h1>
            <a href="new-task?id=${requestScope.projectId}" class="btn btn-primary">+ New Task</a>
        </div>

        <div class="card">
            <div class="card-body p-0">
                <c:choose>
                    <c:when test="${empty taskList}">
                        <div class="empty-state">
                            <div class="display-6 mb-2">&#9989;</div>
                            <p class="mb-1 fw-semibold">No tasks yet</p>
                            <p class="mb-3">Add a task to start tracking progress.</p>
                            <a href="new-task?id=${requestScope.projectId}" class="btn btn-brand-soft">+ New Task</a>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="table-responsive">
                            <table class="table table-hover align-middle mb-0">
                                <thead>
                                    <tr>
                                        <th class="ps-4">ID</th>
                                        <th>Task</th>
                                        <th style="min-width: 180px;">Progress</th>
                                        <th style="min-width: 200px;">Assignee</th>
                                        <th class="text-end pe-4">Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="task" items="${taskList}">
                                        <tr>
                                            <td class="ps-4 text-muted">#<c:out value="${task.id}" /></td>
                                            <td class="fw-semibold"><c:out value="${task.name}" /></td>
                                            <td>
                                                <div class="progress" role="progressbar"
                                                     aria-valuenow="${task.percentComplete}" aria-valuemin="0" aria-valuemax="100">
                                                    <div class="progress-bar" style="width: ${task.percentComplete}%;">${task.percentComplete}%</div>
                                                </div>
                                            </td>
                                            <td>
                                                <select class="form-select form-select-sm"
                                                        onchange="updateTaskAssignee(${task.id}, this.value)">
                                                    <c:choose>
                                                        <c:when test="${not empty task.assignee}">
                                                            <option value="${task.assignee.id}" selected><c:out value="${task.assignee.name}" /></option>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <option value="" selected disabled>Unassigned</option>
                                                        </c:otherwise>
                                                    </c:choose>
                                                    <c:forEach var="u" items="${userMap[task.id]}">
                                                        <option value="${u.id}"><c:out value="${u.name}" /></option>
                                                    </c:forEach>
                                                </select>
                                            </td>
                                            <td class="text-end pe-4">
                                                <a href="edit-task?id=${task.id}&projectId=${requestScope.projectId}" class="btn btn-sm btn-outline-secondary">Edit</a>
                                                <a href="delete-task?id=${task.id}&projectId=${requestScope.projectId}" class="btn btn-sm btn-outline-danger"
                                                   onclick="return confirm('Delete this task?');">Delete</a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>

    <div id="pm-toast" class="pm-toast" role="status" aria-live="polite"></div>

    <script>
        function showToast(message, isError) {
            var t = document.getElementById('pm-toast');
            t.textContent = message;
            t.className = 'pm-toast show' + (isError ? ' error' : '');
            setTimeout(function () { t.className = 'pm-toast'; }, 3000);
        }

        function updateTaskAssignee(taskId, userId) {
            if (!userId) { return; }
            var body = 'taskId=' + encodeURIComponent(taskId) + '&userId=' + encodeURIComponent(userId);
            fetch('updateTaskAssignee', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: body
            })
            .then(function (res) {
                return res.text().then(function (text) {
                    if (!res.ok) { throw new Error(text || 'Request failed'); }
                    return text;
                });
            })
            .then(function (msg) {
                showToast(msg, false);
                setTimeout(function () { window.location.reload(); }, 1200);
            })
            .catch(function (err) {
                showToast('Error: ' + err.message, true);
            });
        }
    </script>
</body>
</html>
