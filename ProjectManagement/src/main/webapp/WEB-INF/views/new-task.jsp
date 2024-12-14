<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Add a New Task</title>
    <!-- Include Bootstrap CSS -->
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
</head>
<body>
    <div class="container mt-5">
		<div class="float-right"><a href="/logout" class="btn btn-danger" role="button">Logout</a></div><br>
        <div class="row justify-content-center">
            <div class="col-md-6">
                <h1 class="text-center mb-4">Create New Task</h1>
                <form:form modelAttribute="task" method="post">
                    <div class="form-group">
                        <label for="name">Task Name</label>
                        <form:input path="name" id="name" class="form-control" placeholder="Enter New Task Name" />
						<p class="text-danger"><form:errors path="name" /></p>
                    </div>
                    <button type="submit" class="btn btn-primary btn-block">Create Task</button>
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