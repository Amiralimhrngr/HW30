<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="context-path" content="${pageContext.request.contextPath}">
    <title>Blog</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <script src="${pageContext.request.contextPath}/assets/js/app.js" defer></script>
</head>
<body>
<h1>Blog Management</h1>

<p id="message"></p>
<p id="username"></p>
<button id="logoutButton" type="button" style="display:none">Log out</button>

<div id="authSection">
    <h2>Register</h2>
    <form id="registerForm">
        <label for="registerUsername"></label><input id="registerUsername" placeholder="Username" required>
        <label for="registerEmail"></label><input id="registerEmail" type="email" placeholder="Email" required>
        <label for="registerPassword"></label><input id="registerPassword" type="password" placeholder="Password"
                                                     required>
        <button type="submit">Register</button>
    </form>

    <h2>Login</h2>
    <form id="loginForm">
        <label for="loginUsername"></label><input id="loginUsername" placeholder="Username" required>
        <label for="loginPassword"></label><input id="loginPassword" type="password" placeholder="Password" required>
        <button type="submit">Login</button>
    </form>
</div>

<div id="postSection" style="display:none">
    <h2 id="formTitle">New post</h2>
    <form id="postForm">
        <label for="postTitle"></label><input id="postTitle" placeholder="Post title" required>
        <label for="postContent"></label><textarea id="postContent" placeholder="Post content" rows="5"
                                                   required></textarea>
        <button id="saveButton" type="submit">Publish</button>
        <button id="cancelButton" type="button" style="display:none">Cancel</button>
    </form>
</div>

<h2>Posts</h2>
<button id="refreshButton" type="button">Refresh</button>
<div id="postsList"></div>
</body>
</html>