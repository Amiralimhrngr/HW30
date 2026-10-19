package ir.maktabsharif.servlet;

import ir.maktabsharif.dto.PostReqDto;
import ir.maktabsharif.dto.PostRespDto;
import ir.maktabsharif.exception.AppException;
import ir.maktabsharif.service.PostService;
import ir.maktabsharif.util.ApplicationContext;
import ir.maktabsharif.util.JsonUtil;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Optional;

public class PostServlet extends HttpServlet {
    private final PostService postService = ApplicationContext.getPostService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String path = request.getPathInfo();
            if (Optional.ofNullable(path).isEmpty() || "/".equals(path)) {
                JsonUtil.write(response, HttpServletResponse.SC_OK, postService.findAll());
                return;
            }
            Long id = parseId(path);
            PostRespDto post = postService.findById(id);
            JsonUtil.write(response, HttpServletResponse.SC_OK, post);
        } catch (AppException e) {
            JsonUtil.error(response, e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            getServletContext().log("Post GET error", e);
            JsonUtil.error(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An internal server error occurred.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            if (Optional.ofNullable(request.getPathInfo()).isPresent() && !"/".equals(request.getPathInfo())) {
                JsonUtil.error(response, HttpServletResponse.SC_NOT_FOUND, "Endpoint not found.");
                return;
            }
            Long userId = requireUserId(request);
            PostReqDto postReqDto = JsonUtil.read(request, PostReqDto.class);
            PostRespDto created = postService.create(userId, postReqDto);
            JsonUtil.write(response, HttpServletResponse.SC_CREATED, created);
        } catch (AppException e) {
            JsonUtil.error(response, e.getStatusCode(), e.getMessage());
        } catch (IOException e) {
            JsonUtil.error(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON request body.");
        } catch (Exception e) {
            getServletContext().log("Post create error", e);
            JsonUtil.error(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An internal server error occurred.");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            Long userId = requireUserId(request);
            Long postId = parseId(request.getPathInfo());
            PostReqDto dto = JsonUtil.read(request, PostReqDto.class);
            JsonUtil.write(response, HttpServletResponse.SC_OK, postService.update(userId, postId, dto));
        } catch (AppException e) {
            JsonUtil.error(response, e.getStatusCode(), e.getMessage());
        } catch (IOException e) {
            JsonUtil.error(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON request body.");
        } catch (Exception e) {
            getServletContext().log("Post update error", e);
            JsonUtil.error(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An internal server error occurred.");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            Long userId = requireUserId(request);
            Long postId = parseId(request.getPathInfo());
            postService.delete(userId, postId);
            JsonUtil.write(response, HttpServletResponse.SC_OK, "Post deleted successfully.");
        } catch (AppException e) {
            JsonUtil.error(response, e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            getServletContext().log("Post delete error", e);
            JsonUtil.error(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An internal server error occurred.");
        }
    }

    private Long requireUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (Optional.ofNullable(session).isEmpty() || !(session.getAttribute("userId") instanceof Long)) {
            throw new AppException(HttpServletResponse.SC_UNAUTHORIZED, "Please log in to manage posts.");
        }
        return (Long) session.getAttribute("userId");
    }

    private Long parseId(String path) {
        if (Optional.ofNullable(path).isEmpty() || !path.matches("/\\d+")) {
            throw new AppException(HttpServletResponse.SC_BAD_REQUEST, "A valid post ID is required in the URL.");
        }
        try {
            return Long.parseLong(path.substring(1));
        } catch (NumberFormatException e) {
            throw new AppException(HttpServletResponse.SC_BAD_REQUEST, "A valid post ID is required in the URL.");
        }
    }
}
