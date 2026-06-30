<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<c:set var="pageTitle" value="Create Account" scope="request" />
<!DOCTYPE html>
<html lang="en">
<head>
<%@ include file="fragments/head.jspf" %>
</head>
<body>
    <div class="auth-wrapper">
        <div class="container">
            <div class="row justify-content-center">
                <div class="col-md-6 col-lg-5">
                    <div class="text-center mb-4">
                        <div class="display-6 fw-bold" style="color: var(--pm-primary);">&#9889; Project Manager</div>
                        <p class="text-muted mb-0">Create your account</p>
                    </div>
                    <div class="card">
                        <div class="card-body p-4 p-sm-5">
                            <h1 class="h4 page-title text-center mb-4">Sign up</h1>
                            <form:form modelAttribute="user" method="post">
                                <div class="mb-3">
                                    <label for="name" class="form-label">Name</label>
                                    <form:input path="name" id="name" class="form-control" placeholder="Your full name" />
                                    <div class="text-danger small mt-1"><form:errors path="name" /></div>
                                </div>
                                <div class="mb-3">
                                    <label for="email" class="form-label">Email</label>
                                    <form:input path="email" id="email" class="form-control" placeholder="you@example.com" />
                                    <div class="text-danger small mt-1"><form:errors path="email" /></div>
                                </div>
                                <div class="mb-3">
                                    <label for="password" class="form-label">Password</label>
                                    <form:input path="password" id="password" class="form-control" type="password" placeholder="Choose a password" />
                                    <div class="text-danger small mt-1"><form:errors path="password" /></div>
                                </div>
                                <div class="d-grid">
                                    <button type="submit" class="btn btn-primary">Create Account</button>
                                </div>
                            </form:form>
                            <hr class="my-4">
                            <p class="text-center mb-0">Already registered? <a href="login">Sign in</a></p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
