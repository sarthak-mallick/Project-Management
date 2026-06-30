<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<c:set var="pageTitle" value="Edit Task" scope="request" />
<!DOCTYPE html>
<html lang="en">
<head>
<%@ include file="fragments/head.jspf" %>
</head>
<body>
<%@ include file="fragments/navbar.jspf" %>
    <div class="container pb-5">
        <div class="row justify-content-center">
            <div class="col-md-7 col-lg-6">
                <a href="all-tasks?id=${projectId}" class="d-inline-block mb-3">&larr; Back to tasks</a>
                <div class="card">
                    <div class="card-body p-4 p-sm-5">
                        <h1 class="h4 page-title mb-1">Edit Task</h1>
                        <p class="text-muted mb-4"><c:out value="${task.name}" /></p>
                        <form:form modelAttribute="task" method="post">
                            <form:hidden path="id" />
                            <div class="mb-3">
                                <label for="effortEstimate" class="form-label">Effort Estimate (hours)</label>
                                <form:input value="${task.effortEstimate}" path="effortEstimate" id="effortEstimate" type="number" step="0.5" min="0" class="form-control" placeholder="e.g. 8" />
                            </div>
                            <div class="mb-3">
                                <label for="effortLogged" class="form-label">Logged Effort (hours)</label>
                                <form:input value="${task.effortLogged}" path="effortLogged" id="effortLogged" type="number" step="0.5" min="0" class="form-control" placeholder="e.g. 4" />
                            </div>
                            <div class="d-flex gap-2">
                                <button type="submit" class="btn btn-primary">Save Changes</button>
                                <a href="all-tasks?id=${projectId}" class="btn btn-light">Cancel</a>
                            </div>
                        </form:form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
