<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Add a New User</title>
    <!-- Include Bootstrap CSS -->
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
</head>
<body>
    <div class="container mt-5">
        <div class="row justify-content-center">
            <div class="col-md-6">
                <h1 class="text-center mb-4">Add a New User</h1>
                <form:form modelAttribute="user" method="post">
                    <div class="form-group">
                        <label for="name">Name</label>
                        <form:input path="name" id="name" class="form-control" placeholder="Enter your name" />
						<p class="text-danger"><form:errors path="name" /></p>
                    </div>
					<div class="form-group">
					    <label for="email">Email</label>
					    <form:input path="email" id="email" class="form-control" placeholder="Enter your email" />
						<p class="text-danger"><form:errors path="email" /></p>
					</div>
                    <div class="form-group">
                        <label for="password">Password</label>
                        <form:input path="password" id="password" class="form-control" type="password" placeholder="Enter your password" />
						<p class="text-danger"><form:errors path="password" /></p>
                    </div>
                    <button type="submit" class="btn btn-primary btn-block">Add User</button>
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