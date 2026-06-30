<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<c:set var="pageTitle" value="Projects" scope="request" />
<!DOCTYPE html>
<html lang="en">
<head>
<%@ include file="fragments/head.jspf" %>
</head>
<body>
<%@ include file="fragments/navbar.jspf" %>
    <div class="container pb-5">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h1 class="h3 page-title mb-0">Projects</h1>
            <a href="new-project" class="btn btn-primary">+ New Project</a>
        </div>

        <div class="card">
            <div class="card-body p-0">
                <c:choose>
                    <c:when test="${empty projectList}">
                        <div class="empty-state">
                            <div class="display-6 mb-2">&#128193;</div>
                            <p class="mb-1 fw-semibold">No projects yet</p>
                            <p class="mb-3">Create your first project to get started.</p>
                            <a href="new-project" class="btn btn-brand-soft">+ New Project</a>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="table-responsive">
                            <table class="table table-hover align-middle mb-0">
                                <thead>
                                    <tr>
                                        <th class="ps-4">ID</th>
                                        <th>Project Name</th>
                                        <th>Tasks</th>
                                        <th class="text-end pe-4">Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="project" items="${projectList}">
                                        <tr>
                                            <td class="ps-4 text-muted">#<c:out value="${project.id}" /></td>
                                            <td class="fw-semibold"><c:out value="${project.name}" /></td>
                                            <td><a href="all-tasks?id=${project.id}">View tasks</a></td>
                                            <td class="text-end pe-4">
                                                <c:choose>
                                                    <c:when test="${sessionScope.userId == project.projectManagerId}">
                                                        <a href="delete-project?id=${project.id}" class="btn btn-sm btn-outline-danger"
                                                           onclick="return confirm('Delete this project and all its tasks?');">Delete</a>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge badge-restricted">Restricted</span>
                                                    </c:otherwise>
                                                </c:choose>
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
</body>
</html>
