<%@taglib prefix="c" uri="jakarta.tags.core"  %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@page import="com.projectManagement.models.Project"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Project Tasks</title>
	<link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
</head>

<body>
	<div class="container mt-5">
		<div class="float-right"><a href="/logout" class="btn btn-danger" role="button">Logout</a></div><br>
		<div class="row justify-content-center">
			<h1 class="text-center mb-4">Tasks</h1>
			<div class="table-responsive">
				<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
				<script>
					function updateTaskAssignee(taskId, userId) {
						console.log("Reached here");
						$.ajax({
							type: "POST",
							url: "updateTaskAssignee",
							data: { taskId: taskId, userId: userId },
							success: function (response) {
								alert(response);
							},
							error: function (xhr, status, error) {
								alert("Error: " + error);
							}
						});
					}
				</script>
				<table class="table table-striped table-hover table-bordered">
					<tr>
						<td>Task ID</td>
						<td>Task Name</td>
						<td>Edit Task</td>
						<td>Percent Completed</td>
						<td>Assignee</td>
						<td>Delete Task</td>
					</tr>
					<c:forEach var="task" items="${requestScope.taskList}">
						<tr>
							<td>
								<c:out value="${task.id}" />
							</td>
							<td>
								<c:out value="${task.name}" />
							</td>
							<td><a href="edit-task?id=${task.id}&projectId=${requestScope.projectId}">Edit</a></td>
							<td>
								<div class="progress">
									<div class="progress-bar" role="progressbar"
										style="width: ${task.percentComplete}%;"> ${task.percentComplete}%
									</div>
								</div>
							</td>
							<td>
								<form:form method="get" action="update-assignee" modelAttribute="task"
									class="form-horizontal">
									<form:select path="assignee" onchange="updateTaskAssignee(${task.id}, this.value)">
										<form:option value="${task.assignee.id}" label="${task.assignee.name}"
											selected="true" />
										<form:options items="${userMap[task.id]}" itemValue="id" itemLabel="name" />
									</form:select>
								</form:form>
							</td>
							<td><a href="delete-task?id=${task.id}&projectId=${requestScope.projectId}">Delete</a></td>
						</tr>
					</c:forEach>
				</table>
				<!-- Include Bootstrap JS (Optional, for interactivity) -->
				<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
				<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
			</div>
			<a href="new-task?id=${requestScope.projectId}">Create New Task</a>
		</div>
	</div>
</body>

</html>