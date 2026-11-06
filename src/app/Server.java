package app;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import exception.InvalidInputException;
import model.User;
import service.UserOperations;
import service.UserService;

import java.awt.Desktop;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Phase 4 - Full Integrated Java Web Server
 * Serves the web application directly and executes server-side logic:
 * - Serves Static Web Interface (HTML, CSS, JS)
 * - Processes Registration and Login (POST / GET)
 * - Server-side validation using UserService (CLO1, CLO2)
 * - Server-side session tracking using session IDs (CLO3)
 * - Renders JSP dashboard with access restriction
 * - Handles Logout and session invalidation
 */
public class Server {

    private static final int PORT = 8080;
    private static final String FRONTEND_DIR = "frontend";
    private static final long SESSION_TIMEOUT_MILLIS = 30 * 60 * 1000L;

    private static final UserOperations userOperations = new UserService();
    private static final Map<String, SessionRecord> SESSIONS = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

            // Web handlers mapping to Phase 4 endpoints
            server.createContext("/", new UnifiedWebHandler());
            server.setExecutor(null);
            server.start();

            String url = "http://localhost:" + PORT + "/login.html";
            System.out.println("===============================================================");
            System.out.println("   STUDENT GRADE CALCULATOR - SERVER & APPLICATION READY       ");
            System.out.println("   Phase 1 to Phase 4 Requirements Fully Integrated (CLO1-3)   ");
            System.out.println("===============================================================");
            System.out.println(">>> Server running at : " + url);
            System.out.println(">>> Servlets/Endpoints: /login, /register, /logout, /dashboard.jsp");
            System.out.println(">>> Press Ctrl + C in this terminal to stop the server.");
            System.out.println("===============================================================\n");

            try {
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    Desktop.getDesktop().browse(new URI(url));
                    System.out.println("[INFO] Browser opened automatically to: " + url);
                } else {
                    System.out.println("[INFO] Open your browser and navigate to: " + url);
                }
            } catch (Exception e) {
                System.out.println("[INFO] Open your browser and navigate to: " + url);
            }

        } catch (IOException e) {
            System.err.println("[ERROR] Failed to start Java Web Server: " + e.getMessage());
        }
    }

    static class UnifiedWebHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod().toUpperCase();

            if (path == null || path.equals("/") || path.isEmpty()) {
                path = "/login.html";
            }

            // Route 1: POST /login
            if (path.equalsIgnoreCase("/login") && method.equals("POST")) {
                handleLoginPost(exchange);
                return;
            }

            // Route 2: GET /login
            if (path.equalsIgnoreCase("/login") && method.equals("GET")) {
                redirect(exchange, "/login.html");
                return;
            }

            // Route 3: POST /register
            if (path.equalsIgnoreCase("/register") && method.equals("POST")) {
                handleRegisterPost(exchange);
                return;
            }

            // Route 4: GET /register
            if (path.equalsIgnoreCase("/register") && method.equals("GET")) {
                redirect(exchange, "/register.html");
                return;
            }

            // Route 5: POST /logout - Invalidate session
            if (path.equalsIgnoreCase("/logout")) {
                if (method.equals("POST")) {
                    handleLogout(exchange);
                } else {
                    exchange.getResponseHeaders().set("Allow", "POST");
                    exchange.sendResponseHeaders(405, -1);
                    exchange.close();
                }
                return;
            }

            // Route 6: /dashboard.jsp (Check Session & Restrict Access)
            if (path.equalsIgnoreCase("/dashboard.jsp")) {
                handleDashboardJsp(exchange);
                return;
            }

            if (path.equalsIgnoreCase("/dashboard.html")) {
                redirect(exchange, "/dashboard.jsp");
                return;
            }

            // Route 7: Static file serving
            serveStaticFile(exchange, path);
        }

        private void handleLoginPost(HttpExchange exchange) throws IOException {
            Map<String, String> params = parseFormData(exchange);
            String username = params.get("username");
            String password = params.get("password");

            boolean isAjax = "XMLHttpRequest".equalsIgnoreCase(exchange.getRequestHeaders().getFirst("X-Requested-With"));

            try {
                User user = userOperations.login(username, password);

                String sessionId = UUID.randomUUID().toString();
                User sessionUser = sessionUser(user);
                SESSIONS.put(sessionId, new SessionRecord(sessionUser));

                exchange.getResponseHeaders().add("Set-Cookie",
                        "JSESSIONID=" + sessionId + "; Path=/; HttpOnly; SameSite=Lax");

                if (isAjax) {
                    sendJson(exchange, 200, "{\"success\":true,\"redirect\":\"/dashboard.jsp\",\"user\":"
                            + sessionUser.toJson() + "}");
                } else {
                    redirect(exchange, "/dashboard.jsp");
                }
            } catch (InvalidInputException e) {
                if (isAjax) {
                    sendJson(exchange, 400, "{\"success\":false,\"message\":\"" + escapeJson(e.getMessage()) + "\"}");
                } else {
                    redirect(exchange, "/login.html?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8.toString()));
                }
            }
        }

        private void handleRegisterPost(HttpExchange exchange) throws IOException {
            Map<String, String> params = parseFormData(exchange);
            String username = params.get("username");
            String password = params.get("password");
            String confirmPassword = params.get("confirmPassword");
            String studentId = params.get("studentId");
            String studentName = params.get("studentName");
            String email = params.get("email");
            boolean isAjax = "XMLHttpRequest".equalsIgnoreCase(exchange.getRequestHeaders().getFirst("X-Requested-With"));

            try {
                if (password == null || !password.equals(confirmPassword)) {
                    throw new InvalidInputException("Passwords do not match. Please re-enter.");
                }

                User newUser = new User(
                        username != null ? username.trim() : "",
                        password,
                        email != null ? email.trim() : "",
                        studentId != null ? studentId.trim() : "",
                        studentName != null ? studentName.trim() : "",
                        "Student"
                );

                User registeredUser = userOperations.register(newUser);

                String sessionId = UUID.randomUUID().toString();
                User sessionUser = sessionUser(registeredUser);
                SESSIONS.put(sessionId, new SessionRecord(sessionUser));
                exchange.getResponseHeaders().add("Set-Cookie",
                        "JSESSIONID=" + sessionId + "; Path=/; HttpOnly; SameSite=Lax");

                if (isAjax) {
                    sendJson(exchange, 200, "{\"success\":true,\"redirect\":\"/dashboard.jsp\",\"user\":"
                            + sessionUser.toJson() + "}");
                } else {
                    redirect(exchange, "/dashboard.jsp");
                }
            } catch (InvalidInputException e) {
                if (isAjax) {
                    sendJson(exchange, 400, "{\"success\":false,\"message\":\"" + escapeJson(e.getMessage()) + "\"}");
                } else {
                    redirect(exchange, "/register.html?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8.toString()));
                }
            }
        }

        private void handleLogout(HttpExchange exchange) throws IOException {
            String sessionId = getSessionId(exchange);
            if (sessionId != null) {
                SESSIONS.remove(sessionId);
            }
            exchange.getResponseHeaders().add("Set-Cookie",
                    "JSESSIONID=; Path=/; Max-Age=0; HttpOnly; SameSite=Lax");
            redirect(exchange, "/login.html?loggedOut=true");
        }

        private void handleDashboardJsp(HttpExchange exchange) throws IOException {
            String sessionId = getSessionId(exchange);
            User currentUser = (sessionId != null) ? getSessionUser(sessionId) : null;

            if (currentUser == null) {
                redirect(exchange, "/login.html?error=" + URLEncoder.encode("Access restricted. Please sign in first.", StandardCharsets.UTF_8.toString()));
                return;
            }

            File jspFile = new File(FRONTEND_DIR, "dashboard.jsp");
            if (!jspFile.exists()) {
                redirect(exchange, "/dashboard.html");
                return;
            }

            // Read JSP and evaluate dynamic expressions
            String content = readFileToString(jspFile);
            String displayName = currentUser.getStudentName() != null && !currentUser.getStudentName().isEmpty()
                    ? currentUser.getStudentName()
                    : currentUser.getUsername();

            String studentId = currentUser.getStudentId() != null ? currentUser.getStudentId() : "N/A";
            String role = currentUser.getRole() != null ? currentUser.getRole() : "Student";
            String safeUserJson = currentUser.toJson()
                    .replace("<", "\\u003c")
                    .replace(">", "\\u003e")
                    .replace("&", "\\u0026");
            content = content.replace("<%= escapeHtml(displayName) %>", escapeHtml(displayName));
            content = content.replace("<%= escapeHtml(role) %>", escapeHtml(role));
            content = content.replace("<%= escapeHtml(studentId) %>", escapeHtml(studentId));
            content = content.replace("<%= escapeHtml(session.getId()) %>", escapeHtml(sessionId));
            content = content.replace("<%= serverUserJson %>", safeUserJson);
            content = content.replace("<%= request.getContextPath() %>", "");

            content = content.replaceAll("<%[\\s\\S]*?%>", "");

            byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.getResponseHeaders().set("Cache-Control", "no-store");
            exchange.sendResponseHeaders(200, bytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(bytes);
            os.close();
        }

        private void serveStaticFile(HttpExchange exchange, String path) throws IOException {
            File file = new File(FRONTEND_DIR, path);

            if (!file.getCanonicalPath().startsWith(new File(FRONTEND_DIR).getCanonicalPath()) || !file.exists() || file.isDirectory()) {
                String response = "404 (Not Found)\nFile not found: " + path;
                exchange.sendResponseHeaders(404, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
                return;
            }

            String mime = "text/plain";
            String lower = file.getName().toLowerCase();
            if (lower.endsWith(".html") || lower.endsWith(".htm")) mime = "text/html; charset=UTF-8";
            else if (lower.endsWith(".css")) mime = "text/css; charset=UTF-8";
            else if (lower.endsWith(".js")) mime = "application/javascript; charset=UTF-8";
            else if (lower.endsWith(".png")) mime = "image/png";
            else if (lower.endsWith(".svg")) mime = "image/svg+xml";

            exchange.getResponseHeaders().set("Content-Type", mime);
            exchange.sendResponseHeaders(200, file.length());

            try (FileInputStream fis = new FileInputStream(file);
                 OutputStream os = exchange.getResponseBody()) {
                byte[] buffer = new byte[4096];
                int count;
                while ((count = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, count);
                }
            }
        }

        private String getSessionId(HttpExchange exchange) {
            String cookieHeader = exchange.getRequestHeaders().getFirst("Cookie");
            if (cookieHeader != null) {
                for (String cookie : cookieHeader.split(";")) {
                    String[] parts = cookie.trim().split("=");
                    if (parts.length == 2 && parts[0].equalsIgnoreCase("JSESSIONID")) {
                        return parts[1];
                    }
                }
            }
            return null;
        }

        private User getSessionUser(String sessionId) {
            SessionRecord record = SESSIONS.get(sessionId);
            if (record == null) {
                return null;
            }

            long now = System.currentTimeMillis();
            if (now - record.lastAccessed > SESSION_TIMEOUT_MILLIS) {
                SESSIONS.remove(sessionId, record);
                return null;
            }

            record.lastAccessed = now;
            return record.user;
        }

        private Map<String, String> parseFormData(HttpExchange exchange) throws IOException {
            InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            Map<String, String> map = new HashMap<>();
            String raw = sb.toString();
            if (!raw.isEmpty()) {
                for (String pair : raw.split("&")) {
                    int separator = pair.indexOf('=');
                    String rawKey = separator < 0 ? pair : pair.substring(0, separator);
                    String rawValue = separator < 0 ? "" : pair.substring(separator + 1);
                    String key = URLDecoder.decode(rawKey, StandardCharsets.UTF_8.toString());
                    String value = URLDecoder.decode(rawValue, StandardCharsets.UTF_8.toString());
                    map.put(key, value);
                }
            }
            return map;
        }

        private void redirect(HttpExchange exchange, String location) throws IOException {
            exchange.getResponseHeaders().set("Location", location);
            exchange.sendResponseHeaders(302, -1);
            exchange.close();
        }

        private void sendJson(HttpExchange exchange, int status, String json) throws IOException {
            byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.sendResponseHeaders(status, bytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(bytes);
            os.close();
        }

        private String readFileToString(File file) throws IOException {
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            }
            return sb.toString();
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

        private String escapeHtml(String value) {
            return value.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#39;");
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

    private static class SessionRecord {
        private final User user;
        private volatile long lastAccessed;

        private SessionRecord(User user) {
            this.user = user;
            this.lastAccessed = System.currentTimeMillis();
        }
    }
}
