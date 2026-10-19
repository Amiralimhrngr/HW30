package ir.maktabsharif.filter;

import ir.maktabsharif.util.JsonUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public class AuthFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String method = httpRequest.getMethod();

        // Public reads are allowed. Creating, editing and deleting posts require a session.
        boolean protectedOperation = "POST".equalsIgnoreCase(method)
                || "PUT".equalsIgnoreCase(method)
                || "PATCH".equalsIgnoreCase(method)
                || "DELETE".equalsIgnoreCase(method);

        if (!protectedOperation) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = httpRequest.getSession(false);
        if (session == null || !(session.getAttribute("userId") instanceof Long)) {
            JsonUtil.error((HttpServletResponse) response, HttpServletResponse.SC_UNAUTHORIZED,
                    "Please log in before creating or managing posts.");
            return;
        }

        chain.doFilter(request, response);
    }
}
