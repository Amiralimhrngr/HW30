package ir.maktabsharif.servlet;

import ir.maktabsharif.dto.UserReqDto;
import ir.maktabsharif.dto.UserRespDto;
import ir.maktabsharif.exception.AppException;
import ir.maktabsharif.service.UserService;
import ir.maktabsharif.util.ApplicationContext;
import ir.maktabsharif.util.JsonUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class AuthServlet extends HttpServlet {
    private final UserService userService = ApplicationContext.getUserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String path = request.getPathInfo();
        if (!"/me".equals(path)) {
            JsonUtil.error(response, HttpServletResponse.SC_NOT_FOUND, "Endpoint not found.");
            return;
        }
        HttpSession session = request.getSession(false);
        if (Optional.ofNullable(session).isEmpty() || !(session.getAttribute("userId") instanceof Long)) {
            JsonUtil.error(response, HttpServletResponse.SC_UNAUTHORIZED, "You are not logged in.");
            return;
        }
        JsonUtil.write(response, HttpServletResponse.SC_OK, new UserRespDto(
                (Long) session.getAttribute("userId"),
                (String) session.getAttribute("username"),
                (String) session.getAttribute("email")
        ));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getPathInfo();
        try {
            if ("/register".equals(path)) {
                UserReqDto userReqDto = JsonUtil.read(request, UserReqDto.class);
                UserRespDto user = userService.register(userReqDto);
                JsonUtil.write(response, HttpServletResponse.SC_CREATED, Map.of(
                        "message", "Registration successful.",
                        "user", user
                ));
                return;
            }
            if ("/login".equals(path)) {
                UserReqDto userReqDto = JsonUtil.read(request, UserReqDto.class);
                UserRespDto user = userService.login(userReqDto);
                HttpSession session = request.getSession(true);
                request.changeSessionId();
                session.setAttribute("userId", user.id());
                session.setAttribute("username", user.username());
                session.setAttribute("email", user.email());
                session.setMaxInactiveInterval(30 * 60);
                JsonUtil.write(response, HttpServletResponse.SC_OK, Map.of(
                        "message", "Login successful.",
                        "user", user
                ));
                return;
            }
            if ("/logout".equals(path)) {
                HttpSession session = request.getSession(false);
                if (Optional.ofNullable(session).isPresent()) {
                    session.invalidate();
                }
                JsonUtil.write(response, HttpServletResponse.SC_OK, "Logout successful.");
                return;
            }
            JsonUtil.error(response, HttpServletResponse.SC_NOT_FOUND, "Endpoint not found.");
        } catch (AppException e) {
            JsonUtil.error(response, e.getStatusCode(), e.getMessage());
        } catch (IOException e) {
            JsonUtil.error(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON request body.");
        } catch (Exception e) {
            getServletContext().log("Auth API error", e);
            JsonUtil.error(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An internal server error occurred.");
        }
    }
}
