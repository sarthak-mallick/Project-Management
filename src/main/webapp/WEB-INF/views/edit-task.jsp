<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Edit Task</title>
    <!-- Include Bootstrap CSS -->
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
</head>
<body>
    <div class="container mt-5">
		<div class="float-right"><a href="/logout" class="btn btn-danger" role="button">Logout</a></div><br>
        <div class="row justify-content-center">
            <div class="col-md-6">
                <h1 class="text-center mb-4">Edit Task</h1>
                <form:form modelAttribute="task" method="post">
                    <div class="form-group">
                        <label for="effortEstimate">Effort Estimate (in hours)</label>
                        <form:input value="${task.effortEstimate}" path="effortEstimate" id="effortEstimate" class="form-control" placeholder="Enter effort estimate" />
                    </div>
					<div class="form-group">
					    <label for="effortLogged">Logged Effort (in hours)</label>
					    <form:input value="${task.effortLogged}" path="effortLogged" id="effortLogged" class="form-control" placeholder="Update your total effort" />
					</div>
                    <button type="submit" class="btn btn-primary btn-block">Submit</button>
                </form:form>
                <hr class="mt-4">
            </div>
        </div>
    </div>

    <!-- Include Bootstrap JS (Optional, for interactivity) -->
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
</body>
</html>