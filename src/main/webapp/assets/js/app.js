const contextPath = document.querySelector('meta[name="context-path"]').content;
const api = contextPath + "/api";

const authSection = document.getElementById("authSection");
const postSection = document.getElementById("postSection");
const usernameText = document.getElementById("username");
const logoutButton = document.getElementById("logoutButton");
const message = document.getElementById("message");
const postsList = document.getElementById("postsList");
const postForm = document.getElementById("postForm");

let currentUser = null;
let editId = null;

// Send a request to the backend.
async function request(url, method = "GET", data = null) {
    const options = { method: method };

    if (data !== null) {
        options.headers = { "Content-Type": "application/json" };
        options.body = JSON.stringify(data);
    }

    const response = await fetch(api + url, options);
    const result = await response.json();

    if (!response.ok) {
        throw new Error(result.message || "Request failed");
    }

    return result;
}

function showMessage(text) {
    message.textContent = text;
}

function updatePage() {
    authSection.style.display = currentUser ? "none" : "block";
    postSection.style.display = currentUser ? "block" : "none";
    logoutButton.style.display = currentUser ? "inline-block" : "none";
    usernameText.textContent = currentUser ? "Logged in as " + currentUser.username : "";
}

function resetPostForm() {
    editId = null;
    postForm.reset();
    document.getElementById("formTitle").textContent = "New post";
    document.getElementById("saveButton").textContent = "Publish";
    document.getElementById("cancelButton").style.display = "none";
}

async function loadPosts() {
    try {
        const posts = await request("/posts");
        postsList.innerHTML = "";

        if (posts.length === 0) {
            postsList.textContent = "No posts yet.";
            return;
        }

        for (const post of posts) {
            const box = document.createElement("div");
            const title = document.createElement("h3");
            const author = document.createElement("p");
            const content = document.createElement("p");

            title.textContent = post.title;
            author.textContent = "By " + post.authorUsername;
            content.textContent = post.content;

            box.appendChild(title);
            box.appendChild(author);
            box.appendChild(content);

            if (currentUser && currentUser.id === post.authorId) {
                const editButton = document.createElement("button");
                editButton.textContent = "Edit post";
                editButton.onclick = function () {
                    editId = post.id;
                    document.getElementById("postTitle").value = post.title;
                    document.getElementById("postContent").value = post.content;
                    document.getElementById("formTitle").textContent = "Edit post";
                    document.getElementById("saveButton").textContent = "Save";
                    document.getElementById("cancelButton").style.display = "inline-block";
                };
                box.appendChild(editButton);

                const deleteButton = document.createElement("button");
                deleteButton.textContent = "Delete post";
                deleteButton.onclick = async function () {
                    if (confirm("Delete this post?")) {
                        try {
                            await request("/posts/" + post.id, "DELETE");
                            showMessage("Post deleted.");
                            loadPosts();
                        } catch (error) {
                            showMessage(error.message);
                        }
                    }
                };
                box.appendChild(deleteButton);
            }

            const commentsTitle = document.createElement("h4");
            commentsTitle.textContent = "Comments";
            box.appendChild(commentsTitle);

            const commentsList = document.createElement("div");
            box.appendChild(commentsList);

            try {
                const comments = await request("/comments?postId=" + post.id);

                if (comments.length === 0) {
                    commentsList.textContent = "No comments yet.";
                }

                comments.forEach(function (comment) {
                    const commentBox = document.createElement("div");
                    const commentAuthor = document.createElement("p");
                    const commentContent = document.createElement("p");

                    commentAuthor.textContent = comment.authorUsername;
                    commentContent.textContent = comment.content;
                    commentBox.appendChild(commentAuthor);
                    commentBox.appendChild(commentContent);

                    if (currentUser && currentUser.id === comment.authorId) {
                        const editCommentButton = document.createElement("button");
                        editCommentButton.textContent = "Edit comment";
                        editCommentButton.onclick = async function () {
                            const newContent = prompt("Edit your comment:", comment.content);

                            if (newContent === null || newContent.trim() === "") {
                                return;
                            }

                            try {
                                await request("/comments/" + comment.id, "PUT", {
                                    content: newContent
                                });
                                showMessage("Comment updated.");
                                loadPosts();
                            } catch (error) {
                                showMessage(error.message);
                            }
                        };
                        commentBox.appendChild(editCommentButton);

                        const deleteCommentButton = document.createElement("button");
                        deleteCommentButton.textContent = "Delete comment";
                        deleteCommentButton.onclick = async function () {
                            if (confirm("Delete your comment?")) {
                                try {
                                    await request("/comments/" + comment.id, "DELETE");
                                    showMessage("Comment deleted.");
                                    loadPosts();
                                } catch (error) {
                                    showMessage(error.message);
                                }
                            }
                        };
                        commentBox.appendChild(deleteCommentButton);
                    }

                    commentsList.appendChild(commentBox);
                });
            } catch (error) {
                commentsList.textContent = "Could not load comments.";
            }

            if (currentUser) {
                const commentForm = document.createElement("form");
                const commentInput = document.createElement("textarea");
                const commentButton = document.createElement("button");

                commentInput.placeholder = "Write a comment";
                commentInput.required = true;
                commentButton.type = "submit";
                commentButton.textContent = "Comment";

                commentForm.appendChild(commentInput);
                commentForm.appendChild(commentButton);

                commentForm.onsubmit = async function (event) {
                    event.preventDefault();

                    try {
                        await request("/comments?postId=" + post.id, "POST", {
                            content: commentInput.value
                        });
                        showMessage("Comment added.");
                        loadPosts();
                    } catch (error) {
                        showMessage(error.message);
                    }
                };

                box.appendChild(commentForm);
            }

            postsList.appendChild(box);
        }
    } catch (error) {
        showMessage(error.message);
    }
}

document.getElementById("registerForm").onsubmit = async function (event) {
    event.preventDefault();

    const data = {
        username: document.getElementById("registerUsername").value,
        email: document.getElementById("registerEmail").value,
        password: document.getElementById("registerPassword").value
    };

    try {
        await request("/auth/register", "POST", data);
        this.reset();
        showMessage("Registered. Please log in.");
    } catch (error) {
        showMessage(error.message);
    }
};

document.getElementById("loginForm").onsubmit = async function (event) {
    event.preventDefault();

    const data = {
        username: document.getElementById("loginUsername").value,
        password: document.getElementById("loginPassword").value
    };

    try {
        const result = await request("/auth/login", "POST", data);
        currentUser = result.user;
        this.reset();
        updatePage();
        showMessage("Login successful.");
        loadPosts();
    } catch (error) {
        showMessage(error.message);
    }
};

logoutButton.onclick = async function () {
    try {
        await request("/auth/logout", "POST");
        currentUser = null;
        updatePage();
        resetPostForm();
        showMessage("Logged out.");
        loadPosts();
    } catch (error) {
        showMessage(error.message);
    }
};

postForm.onsubmit = async function (event) {
    event.preventDefault();

    const data = {
        title: document.getElementById("postTitle").value,
        content: document.getElementById("postContent").value
    };

    try {
        if (editId === null) {
            await request("/posts", "POST", data);
            showMessage("Post published.");
        } else {
            await request("/posts/" + editId, "PUT", data);
            showMessage("Post updated.");
        }

        resetPostForm();
        loadPosts();
    } catch (error) {
        showMessage(error.message);
    }
};

document.getElementById("cancelButton").onclick = resetPostForm;
document.getElementById("refreshButton").onclick = loadPosts;

async function start() {
    try {
        currentUser = await request("/auth/me");
    } catch (error) {
        currentUser = null;
    }

    updatePage();
    loadPosts();
}

start();
