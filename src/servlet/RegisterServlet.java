package servlet;

import exception.InvalidInputException;
import model.User;
import service.UserOperations;
import service.UserService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Phase 4 - Registration Servlet (CLO1, CLO2, CLO3)
 * Processes student registration via GET/POST methods.
 * Implements server-side credential validation, duplicate checks,
 * and user enrollment.
 */
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final UserOperations userOperations = new UserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Forward GET request to register.html
        response.sendRedirect(request.getContextPath() + "/register.html");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String studentId = request.getParameter("studentId");
        String studentName = request.getParameter("studentName");
        String email = request.getParameter("email");
        String requestedWith = request.getHeader("X-Requested-With");
        boolean isAjax = "XMLHttpRequest".equalsIgnoreCase(requestedWith) ||
                request.getHeader("Accept") != null && request.getHeader("Accept").contains("application/json");

        try {
            // Server-side check for password confirmation
            if (password == null || !password.equals(confirmPassword)) {
                throw new InvalidInputException("Passwords do not match. Please re-enter.");
            }

            // Create new User entity (Phase 1 & 3)
            User newUser = new User(
                    username != null ? username.trim() : "",
                    password,
                    email != null ? email.trim() : "",
                    studentId != null ? studentId.trim() : "",
                    studentName != null ? studentName.trim() : "",
                    "Student"
            );
            String course = request.getParameter("course");
            if (course != null && !course.trim().isEmpty()) {
                newUser.setCourse(course.trim());
            }

            User registeredUser = userOperations.register(newUser);

            HttpSession session = request.getSession(true);
            request.changeSessionId();
            User sessionUser = sessionUser(registeredUser);
            session.setAttribute("user", sessionUser);
            session.setAttribute("username", sessionUser.getUsername());
            session.setAttribute("studentName", sessionUser.getStudentName());
            session.setAttribute("studentId", sessionUser.getStudentId());
            session.setAttribute("role", sessionUser.getRole());
            session.setMaxInactiveInterval(30 * 60);
            response.setHeader("Cache-Control", "no-store");

            if (isAjax) {
                response.setContentType("application/json;charset=UTF-8");
                PrintWriter out = response.getWriter();
                out.print("{\"success\":true,\"redirect\":\"" + request.getContextPath()
                        + "/dashboard.jsp\",\"message\":\"Registration successful!\",\"user\":"
                        + sessionUser.toJson() + "}");
                out.flush();
            } else {
                response.sendRedirect(request.getContextPath() + "/dashboard.jsp");
            }

        } catch (InvalidInputException e) {
            if (isAjax) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("application/json;charset=UTF-8");
                PrintWriter out = response.getWriter();
                out.print("{\"success\":false,\"message\":\"" + escapeJson(e.getMessage()) + "\"}");
                out.flush();
            } else {
                String errorMsg = URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8.toString());
                response.sendRedirect(request.getContextPath() + "/register.html?error=" + errorMsg);
            }
        }
    }

    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private User sessionUser(User user) {
        return new User(
                user.getUsername(),
                null,
                user.getEmail(),
                user.getStudentId(),
                user.getStudentName(),
                user.getRole()
        );
    }
}
