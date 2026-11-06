package servlet;

import model.User;
import service.UserOperations;
import service.UserService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

public class StudentListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final UserOperations userOperations = new UserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Cache-Control", "no-store");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().print("{\"success\":false,\"message\":\"Please sign in to view student records.\"}");
            return;
        }

        List<User> users = userOperations.getAllUsers();
        StringBuilder json = new StringBuilder("{\"success\":true,\"students\":[");
        boolean first = true;
        for (User user : users) {
            if (!"Student".equalsIgnoreCase(user.getRole())) {
                continue;
            }
            if (!first) {
                json.append(',');
            }
            first = false;
            json.append("{\"studentId\":\"").append(escapeJson(user.getStudentId()))
                    .append("\",\"studentName\":\"").append(escapeJson(user.getStudentName()))
                    .append("\",\"course\":\"").append(escapeJson(user.getCourse()))
                    .append("\",\"semester\":").append(user.getSemester()).append('}');
        }
        json.append("]}");
        response.getWriter().print(json.toString());
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
