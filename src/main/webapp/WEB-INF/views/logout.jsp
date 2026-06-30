<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<c:set var="pageTitle" value="Logged Out" scope="request" />
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
                    <div class="card text-center">
                        <div class="card-body p-5">
                            <div class="display-3 mb-2">&#128075;</div>
                            <h1 class="h4 page-title mb-2">Signed out</h1>
                            <p class="text-muted mb-4">You have been logged out successfully.</p>
                            <a href="/login" class="btn btn-primary px-4">Sign in again</a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
