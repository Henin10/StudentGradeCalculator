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

public class UserListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final UserOperations userOperations = new UserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Cache-Control", "no-store");

        HttpSession session = request.getSession(false);
        if (session == null || !"Admin".equals(session.getAttribute("role"))) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().print("{\"success\":false,\"message\":\"Administrator access required.\"}");
            return;
        }

        List<User> users = userOperations.getAllUsers();
        StringBuilder json = new StringBuilder("{\"success\":true,\"users\":[");
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            if (i > 0) {
                json.append(',');
            }
            json.append(new User(
                    user.getUsername(),
                    null,
                    user.getEmail(),
                    user.getStudentId(),
                    user.getStudentName(),
                    user.getRole()
            ).toJson());
        }
        json.append("]}");
        response.getWriter().print(json.toString());
    }
}
