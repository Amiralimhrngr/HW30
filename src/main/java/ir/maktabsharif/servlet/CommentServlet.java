package ir.maktabsharif.servlet;

import ir.maktabsharif.dto.CommentReqDto;
import ir.maktabsharif.exception.AppException;
import ir.maktabsharif.service.CommentService;
import ir.maktabsharif.util.ApplicationContext;
import ir.maktabsharif.util.JsonUtil;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Optional;

public class CommentServlet extends HttpServlet {
    private final CommentService commentService = ApplicationContext.getCommentService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Long postId = parsePostId(request.getParameter("postId"));
            JsonUtil.write(response, HttpServletResponse.SC_OK, commentService.findByPostId(postId));
        } catch (AppException e) {
            JsonUtil.error(response, e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            getServletContext().log("Comment GET error", e);
            JsonUtil.error(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "An internal server error occurred.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Long userId = requireUserId(request);
            Long postId = parsePostId(request.getParameter("postId"));
            CommentReqDto body = JsonUtil.read(request, CommentReqDto.class);
            JsonUtil.write(response, HttpServletResponse.SC_CREATED, commentService.create(userId, postId, body));
        } catch (AppException e) {
            JsonUtil.error(response, e.getStatusCode(), e.getMessage());
        } catch (IOException e) {
            JsonUtil.error(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON request body.");
        } catch (Exception e) {
            getServletContext().log("Comment create error", e);
            JsonUtil.error(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "An internal server error occurred.");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Long userId = requireUserId(request);
            Long commentId = parseCommentId(request.getPathInfo());
            CommentReqDto body = JsonUtil.read(request, CommentReqDto.class);
            JsonUtil.write(response, HttpServletResponse.SC_OK, commentService.update(userId, commentId, body));
        } catch (AppException e) {
            JsonUtil.error(response, e.getStatusCode(), e.getMessage());
        } catch (IOException e) {
            JsonUtil.error(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON request body.");
        } catch (Exception e) {
            getServletContext().log("Comment update error", e);
            JsonUtil.error(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "An internal server error occurred.");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Long userId = requireUserId(request);
            Long commentId = parseCommentId(request.getPathInfo());
            commentService.delete(userId, commentId);
            JsonUtil.write(response, HttpServletResponse.SC_OK, "Comment deleted successfully.");
        } catch (AppException e) {
            JsonUtil.error(response, e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            getServletContext().log("Comment delete error", e);
            JsonUtil.error(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "An internal server error occurred.");
        }
    }

    private Long requireUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (Optional.ofNullable(session).isEmpty() || !(session.getAttribute("userId") instanceof Long)) {
            throw new AppException(HttpServletResponse.SC_UNAUTHORIZED, "Please log in first.");
        }
        return (Long) session.getAttribute("userId");
    }

    private Long parsePostId(String value) {
        if (Optional.ofNullable(value).isEmpty() || !value.matches("\\d+")) {
            throw new AppException(HttpServletResponse.SC_BAD_REQUEST, "A valid postId is required.");
        }
        try {
            long id = Long.parseLong(value);
            if (id <= 0) {
                throw new NumberFormatException();
            }
            return id;
        } catch (NumberFormatException e) {
            throw new AppException(HttpServletResponse.SC_BAD_REQUEST, "A valid postId is required.");
        }
    }

    private Long parseCommentId(String path) {
        if (Optional.ofNullable(path).isEmpty() || !path.matches("/\\d+")) {
            throw new AppException(HttpServletResponse.SC_BAD_REQUEST, "A valid comment ID is required.");
        }
        try {
            return Long.parseLong(path.substring(1));
        } catch (NumberFormatException e) {
            throw new AppException(HttpServletResponse.SC_BAD_REQUEST, "A valid comment ID is required.");
        }
    }
}
