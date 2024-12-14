<%@taglib prefix="c" uri="jakarta.tags.core"  %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@page import="com.projectManagement.models.Project"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>All Projects</title>
	<link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
</head>

<body>
	<div class="container mt-5">
		<div class="float-right"><a href="/logout" class="btn btn-danger" role="button">Logout</a></div><br>
		<div class="row justify-content-center">
			<h1 class="text-center mb-4">Projects</h1>
			<div class="table-responsive">
				<table class="table table-striped table-hover table-bordered">
					<tr>
						<td>Project ID</td>
						<td>Project Name</td>
						<td>Tasks</td>
						<td>Delete Project</td>
					</tr>
					<c:forEach var="project" items="${requestScope.projectList}">
						<tr>
							<td>
								<c:out value="${project.id}" />
							</td>
							<td>
								<c:out value="${project.name}" />
							</td>
							<td><a href="all-tasks?id=${project.id}">View Tasks</a></td>
							
							<td>
								<c:choose>
								    <c:when test="${sessionScope.userId==project.projectManagerId}">
										<a href="delete-project?id=${project.id}">Delete</a> 
								        <br />
								    </c:when>    
								    <c:otherwise>
								        Restricted
								        <br />
								    </c:otherwise>
								</c:choose>
							</td>
						</tr>
					</c:forEach>
				</table>

				<!-- Include Bootstrap JS (Optional, for interactivity) -->
				<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
				<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
			</div>
			<a href="new-project">Create New Project</a>
		</div>
	</div>

</body>

</html>