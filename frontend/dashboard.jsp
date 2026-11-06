<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.User" %>
<%!
    private String escapeHtml(String value) {
        if (value == null) return "";
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
%>
<%
    response.setHeader("Cache-Control", "no-store");
    User currentUser = (User) session.getAttribute("user");
    if (currentUser == null) {
        response.sendRedirect(request.getContextPath() + "/login.html?error=Access+restricted.+Please+sign+in+first.");
        return;
    }

    String displayName = (currentUser.getStudentName() != null && !currentUser.getStudentName().trim().isEmpty())
            ? currentUser.getStudentName()
            : currentUser.getUsername();
    String studentId = currentUser.getStudentId() != null ? currentUser.getStudentId() : "N/A";
    String role = currentUser.getRole() != null ? currentUser.getRole() : "Student";
    String serverUserJson = currentUser.toJson()
            .replace("<", "\\u003c")
            .replace(">", "\\u003e")
            .replace("&", "\\u0026");
%>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>JSP Dashboard - Student Grade Calculator</title>
    <link rel="stylesheet" href="style.css">
</head>

<body>

    <div class="app-container">

        <!-- JSP NAVBAR WITH REAL-TIME SESSION TRACKING -->
        <nav class="navbar">
            <a href="dashboard.jsp" class="nav-brand">
                <div class="brand-icon">
                    <svg viewBox="0 0 24 24"><path d="M12 3L1 9L12 15L21 10.09V17H23V9M5 13.18V17.18L12 21L19 17.18V13.18L12 17L5 13.18Z"/></svg>
                </div>
                <span>Grade Calculator (JSP / Servlet)</span>
            </a>

            <ul class="nav-links">
                <li><a href="dashboard.jsp" class="active">Dashboard (JSP)</a></li>
                <li><a href="student.html">Students</a></li>
                <li><a href="index.html">Calculator</a></li>
                <li><a href="result.html">Report Card</a></li>
            </ul>

            <div class="nav-user">
                <span class="user-tag">User: <strong><%= escapeHtml(displayName) %></strong> (<%= escapeHtml(role) %>)</span>
                <form action="<%= request.getContextPath() %>/logout" method="post">
                    <button type="submit" class="btn btn-secondary btn-sm">Logout</button>
                </form>
            </div>
        </nav>

        <!-- MAIN DASHBOARD CARD -->
        <div class="card">

            <div class="card-header">
                <h1 class="card-title">
                    🎓 Academic Performance Dashboard (JSP)
                </h1>
                <p class="card-subtitle">
                    Server-Side Rendered JSP Dashboard with active <code>HttpSession</code> tracking.
                </p>
            </div>

            <!-- USER SESSION BANNER -->
            <div style="background: #f0f4ff; border: 1px solid #c8d7fb; border-radius: 12px; padding: 18px 22px; margin-bottom: 24px; display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 12px;">
                <div>
                    <h3 style="color: #2b3a8a; font-size: 17px; margin-bottom: 4px;">Welcome back, <%= escapeHtml(displayName) %>!</h3>
                    <p style="color: #555; font-size: 13px; margin: 0;">
                        Session ID: <code style="background: #e2e8f0; padding: 2px 6px; border-radius: 4px;"><%= escapeHtml(session.getId()) %></code> &nbsp;|&nbsp;
                        Student ID: <strong><%= escapeHtml(studentId) %></strong> &nbsp;|&nbsp;
                        Role: <strong><%= escapeHtml(role) %></strong>
                    </p>
                </div>
                <div style="font-size: 12px; color: #38a169; background: #def7ec; padding: 6px 12px; border-radius: 20px; font-weight: bold;">
                    ● Active Session Authenticated
                </div>
            </div>

            <!-- STATISTICS WIDGETS -->
            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-number" id="statTotalStudents">0</div>
                    <div class="stat-label">Total Students</div>
                </div>
                <div class="stat-card">
                    <div class="stat-number" id="statEvaluated" style="color: #3182ce;">0</div>
                    <div class="stat-label">Evaluated Exams</div>
                </div>
                <div class="stat-card">
                    <div class="stat-number" id="statPassed" style="color: #38a169;">0</div>
                    <div class="stat-label">Students Passed</div>
                </div>
                <div class="stat-card">
                    <div class="stat-number" id="statAvgPercentage" style="color: #805ad5;">0.0%</div>
                    <div class="stat-label">Average Performance</div>
                </div>
            </div>

            <!-- MANAGEMENT MODULES -->
            <h2 class="section-accent-title">Management Modules & Quick Actions</h2>

            <div class="actions-grid">
                <a href="student.html" class="action-tile">
                    <div class="action-icon">
                        <svg viewBox="0 0 24 24"><path d="M15 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm-9-2V7H4v3H1v2h3v3h2v-3h3v-2H6zm9 4c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/></svg>
                    </div>
                    <h3>Add / Manage Students</h3>
                    <p>Register student profiles, view enrolled records, edit information, or delete records.</p>
                </a>

                <a href="index.html" class="action-tile">
                    <div class="action-icon">
                        <svg viewBox="0 0 24 24"><path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-7 3c1.66 0 3 1.34 3 3s-1.34 3-3 3-3-1.34-3-3 1.34-3 3-3zm6 12H6v-1.4c0-2 4-3.1 6-3.1s6 1.1 6 3.1V18z"/></svg>
                    </div>
                    <h3>Calculate Grades</h3>
                    <p>Enter subject-wise marks, compute total scores, percentage, assign grades, and determine status.</p>
                </a>

                <a href="result.html" class="action-tile">
                    <div class="action-icon">
                        <svg viewBox="0 0 24 24"><path d="M19 3H5c-1.11 0-2 .9-2 2v14c0 1.1.89 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-5 14H7v-2h7v2zm3-4H7v-2h10v2zm0-4H7V7h10v2z"/></svg>
                    </div>
                    <h3>Academic Report Card</h3>
                    <p>Inspect the official academic transcript with subject marks, percentage breakdown, and printable report.</p>
                </a>
            </div>

            <!-- RECENT EVALUATIONS TABLE -->
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 14px;">
                <h2 class="section-accent-title" style="margin-bottom: 0;">Recent Grade Evaluations</h2>
                <a href="index.html" class="btn btn-primary btn-sm">+ New Evaluation</a>
            </div>

            <div class="table-responsive">
                <table>
                    <thead>
                        <tr>
                            <th>Student ID</th>
                            <th>Student Name</th>
                            <th>Course</th>
                            <th>Total Marks</th>
                            <th>Percentage</th>
                            <th>Grade</th>
                            <th>Status</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody id="evaluationsTableBody">
                        <!-- Populated dynamically -->
                    </tbody>
                </table>
            </div>

        </div>

        <div class="footer">
            Student Grade Calculator Management System • Java EE Servlets, JSP & HttpSession Session Tracking
        </div>

    </div>

    <script src="script.js?v=20261028-2"></script>
    <script>
        // Synchronize client store with server session user
        const serverUser = <%= serverUserJson %>;
        setCurrentUser(serverUser);

        // Load stats and evaluations table
        function loadDashboardData() {
            const stats = getSystemStats();
            document.getElementById("statTotalStudents").textContent = stats.totalStudents;
            document.getElementById("statEvaluated").textContent = stats.totalEvaluations;
            document.getElementById("statPassed").textContent = stats.passCount;
            document.getElementById("statAvgPercentage").textContent = stats.avgPercentage + "%";

            const evaluations = getAllEvaluations();
            const tbody = document.getElementById("evaluationsTableBody");

            if (evaluations.length === 0) {
                tbody.innerHTML = `
                    <tr>
                        <td colspan="8" style="text-align: center; color: #888; padding: 24px;">
                            No evaluations calculated yet. Click "+ New Evaluation" to grade students!
                        </td>
                    </tr>
                `;
                return;
            }

            tbody.innerHTML = evaluations.map(ev => `
                <tr>
                    <td><strong>\${ev.studentId}</strong></td>
                    <td>\${ev.studentName}</td>
                    <td>\${ev.course} (Sem \${ev.semester})</td>
                    <td>\${ev.totalMarks} / \${ev.maxMarks}</td>
                    <td><strong>\${ev.percentage.toFixed(2)}%</strong></td>
                    <td><span class="badge badge-grade">\${ev.grade}</span></td>
                    <td><span class="badge \${ev.status === 'PASS' ? 'badge-pass' : 'badge-fail'}">\${ev.status}</span></td>
                    <td>
                        <button type="button" class="btn btn-secondary btn-sm" onclick="viewStudentResult('\${ev.studentId}')">
                            View Result
                        </button>
                    </td>
                </tr>
            `).join('');
        }

        function viewStudentResult(studentId) {
            const ev = getAllEvaluations().find(e => e.studentId.trim().toUpperCase() === studentId.trim().toUpperCase());
            if (ev) {
                sessionStorage.setItem("gradeCalc_latestResult", JSON.stringify(ev));
                window.location.href = "result.html";
            }
        }

        loadDashboardData();
    </script>

</body>

</html>
