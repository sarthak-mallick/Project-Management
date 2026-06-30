<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<c:set var="pageTitle" value="New Task" scope="request" />
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
                        <h1 class="h4 page-title mb-4">Create New Task</h1>
                        <form:form modelAttribute="task" method="post" action="new-task?id=${projectId}">
                            <div class="mb-3">
                                <label for="name" class="form-label">Task Name</label>
                                <form:input path="name" id="name" class="form-control" placeholder="Enter a task name" />
                                <div class="text-danger small mt-1"><form:errors path="name" /></div>
                            </div>
                            <div class="d-flex gap-2">
                                <button type="submit" class="btn btn-primary">Create Task</button>
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
