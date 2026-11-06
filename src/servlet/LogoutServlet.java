package servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Phase 4 - Logout Servlet (CLO1, CLO2, CLO3)
 * Handles session invalidation and redirects the user to the login page.
 */
public class LogoutServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setHeader("Allow", "POST");
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processLogout(request, response);
    }

    private void processLogout(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        // Phase 4 Requirement: Implement logout and session invalidation
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        Cookie sessionCookie = new Cookie("JSESSIONID", "");
        String contextPath = request.getContextPath();
        sessionCookie.setPath(contextPath.isEmpty() ? "/" : contextPath);
        sessionCookie.setMaxAge(0);
        sessionCookie.setHttpOnly(true);
        sessionCookie.setSecure(request.isSecure());
        response.addCookie(sessionCookie);
        response.setHeader("Cache-Control", "no-store");
        response.sendRedirect(request.getContextPath() + "/login.html?loggedOut=true");
    }
}
