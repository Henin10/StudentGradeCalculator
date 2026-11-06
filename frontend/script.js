/**
 * ==========================================================================
 * Student Grade Calculator Management System - Centralized Client Engine
 * ==========================================================================
 */

const STORAGE_KEYS = {
    USER: 'gradeCalc_user',
    STUDENTS: 'gradeCalc_students',
    EVALUATIONS: 'gradeCalc_evaluations'
};

// Initial default seed students
const DEFAULT_STUDENTS = [
    {
        studentId: "MCA101",
        studentName: "Rahul Kumar",
        course: "MCA",
        semester: 1
    },
    {
        studentId: "MCA102",
        studentName: "Priya Sharma",
        course: "MCA",
        semester: 1
    },
    {
        studentId: "MCA103",
        studentName: "Amit Verma",
        course: "MCA",
        semester: 2
    }
];

// Initial default seed evaluations
const DEFAULT_EVALUATIONS = [
    {
        studentId: "MCA101",
        studentName: "Rahul Kumar",
        course: "MCA",
        semester: 1,
        marks: {
            "Java Programming": 88.5,
            "Data Structures": 92.0,
            "Database Management": 78.5,
            "Computer Networks": 85.0,
            "Web Technology": 90.0
        },
        totalMarks: 434.0,
        maxMarks: 500.0,
        percentage: 86.8,
        grade: "A",
        status: "PASS",
        date: new Date().toLocaleDateString()
    }
];

// ==========================================
// 1. DATA INITIALIZATION & STORAGE HELPERS
// ==========================================

function initDataStore() {
    if (!localStorage.getItem(STORAGE_KEYS.STUDENTS)) {
        localStorage.setItem(STORAGE_KEYS.STUDENTS, JSON.stringify(DEFAULT_STUDENTS));
    }
    if (!localStorage.getItem(STORAGE_KEYS.EVALUATIONS)) {
        localStorage.setItem(STORAGE_KEYS.EVALUATIONS, JSON.stringify(DEFAULT_EVALUATIONS));
    }
}

// Ensure store is ready
initDataStore();

// ==========================================
// 2. AUTHENTICATION & SESSION MANAGEMENT
// ==========================================

function getCurrentUser() {
    const raw = sessionStorage.getItem(STORAGE_KEYS.USER);
    if (!raw) return null;
    try {
        return JSON.parse(raw);
    } catch (e) {
        return null;
    }
}

function setCurrentUser(user) {
    sessionStorage.setItem(STORAGE_KEYS.USER, JSON.stringify(user));
}

function logout() {
    sessionStorage.removeItem(STORAGE_KEYS.USER);
    localStorage.removeItem(STORAGE_KEYS.USER);
    const form = document.createElement("form");
    form.method = "POST";
    form.action = "logout";
    document.body.appendChild(form);
    form.submit();
}

function requireAuth() {
    const user = getCurrentUser();
    if (!user) {
        window.location.href = "login.html";
    }
    return user;
}

// Renders the global top navigation bar
function renderNavbar(activePage = '') {
    const navPlaceholder = document.getElementById("navbarPlaceholder");
    if (!navPlaceholder) return;

    const user = getCurrentUser();
    const displayName = user ? (user.studentName || user.username) : "Guest";

    navPlaceholder.innerHTML = `
        <nav class="navbar">
            <a href="dashboard.html" class="nav-brand">
                <div class="brand-icon">
                    <svg viewBox="0 0 24 24"><path d="M12 3L1 9L12 15L21 10.09V17H23V9M5 13.18V17.18L12 21L19 17.18V13.18L12 17L5 13.18Z"/></svg>
                </div>
                <span>Grade Calculator MS</span>
            </a>

            <ul class="nav-links">
                <li><a href="dashboard.html" class="${activePage === 'dashboard' ? 'active' : ''}">Dashboard</a></li>
                <li><a href="student.html" class="${activePage === 'student' ? 'active' : ''}">Students</a></li>
                <li><a href="index.html" class="${activePage === 'calculator' ? 'active' : ''}">Calculator</a></li>
                <li><a href="result.html" class="${activePage === 'result' ? 'active' : ''}">Report Card</a></li>
            </ul>

            <div class="nav-user">
                <span class="user-tag">User: <strong>${displayName}</strong></span>
                <button type="button" class="btn btn-secondary btn-sm" onclick="logout()">Logout</button>
            </div>
        </nav>
    `;
}

// ==========================================
// 3. STUDENT CRUD OPERATIONS
// ==========================================

function getAllStudents() {
    try {
        return JSON.parse(localStorage.getItem(STORAGE_KEYS.STUDENTS)) || [];
    } catch(e) {
        return [];
    }
}

function saveAllStudents(students) {
    localStorage.setItem(STORAGE_KEYS.STUDENTS, JSON.stringify(students));
}

async function syncRegisteredStudents() {
    const response = await fetch("students", {
        headers: { "Accept": "application/json" },
        cache: "no-store"
    });
    const result = await response.json();
    if (!response.ok || !result.success || !Array.isArray(result.students)) {
        throw new Error(result.message || "Unable to load registered student accounts.");
    }

    const students = getAllStudents();
    result.students.forEach(registeredStudent => {
        const studentId = registeredStudent.studentId.trim().toUpperCase();
        const existing = students.find(student => student.studentId.trim().toUpperCase() === studentId);
        const syncedStudent = {
            studentId,
            studentName: registeredStudent.studentName,
            course: registeredStudent.course || "MCA",
            semester: Number(registeredStudent.semester) || 1
        };
        if (existing) {
            Object.assign(existing, syncedStudent);
        } else {
            students.push(syncedStudent);
        }
    });
    saveAllStudents(students);
    return students;
}

function getStudentById(studentId) {
    const students = getAllStudents();
    return students.find(s => s.studentId.trim().toUpperCase() === studentId.trim().toUpperCase());
}

function addStudent(student) {
    const students = getAllStudents();
    const idUpper = student.studentId.trim().toUpperCase();

    // Prevent duplicate Student IDs
    if (students.some(s => s.studentId.trim().toUpperCase() === idUpper)) {
        throw new Error(`Student with ID '${student.studentId}' already exists!`);
    }

    const newStudent = {
        studentId: idUpper,
        studentName: student.studentName.trim(),
        course: student.course.trim(),
        semester: parseInt(student.semester, 10)
    };

    students.push(newStudent);
    saveAllStudents(students);
    return newStudent;
}

function updateStudent(studentId, updatedFields) {
    const students = getAllStudents();
    const index = students.findIndex(s => s.studentId.trim().toUpperCase() === studentId.trim().toUpperCase());

    if (index === -1) {
        throw new Error(`Student with ID '${studentId}' not found.`);
    }

    students[index].studentName = updatedFields.studentName.trim();
    students[index].course = updatedFields.course.trim();
    students[index].semester = parseInt(updatedFields.semester, 10);

    saveAllStudents(students);
    return students[index];
}

function deleteStudent(studentId) {
    let students = getAllStudents();
    const originalLength = students.length;
    students = students.filter(s => s.studentId.trim().toUpperCase() !== studentId.trim().toUpperCase());

    if (students.length === originalLength) {
        throw new Error(`Student with ID '${studentId}' not found.`);
    }

    saveAllStudents(students);

    // Also remove any existing evaluations
    let evaluations = getAllEvaluations();
    evaluations = evaluations.filter(e => e.studentId.trim().toUpperCase() !== studentId.trim().toUpperCase());
    saveAllEvaluations(evaluations);

    return true;
}

function searchStudents(query) {
    if (!query || !query.trim()) return getAllStudents();
    const q = query.trim().toLowerCase();
    return getAllStudents().filter(s =>
        s.studentId.toLowerCase().includes(q) ||
        s.studentName.toLowerCase().includes(q) ||
        s.course.toLowerCase().includes(q)
    );
}

// ==========================================
// 4. GRADE CALCULATIONS & EVALUATIONS
// ==========================================

function getAllEvaluations() {
    try {
        return JSON.parse(localStorage.getItem(STORAGE_KEYS.EVALUATIONS)) || [];
    } catch(e) {
        return [];
    }
}

function saveAllEvaluations(evaluations) {
    localStorage.setItem(STORAGE_KEYS.EVALUATIONS, JSON.stringify(evaluations));
}

function getGradeForPercentage(percentage) {
    if (percentage >= 90.0) return "A+";
    if (percentage >= 80.0) return "A";
    if (percentage >= 70.0) return "B";
    if (percentage >= 60.0) return "C";
    if (percentage >= 50.0) return "D";
    return "F";
}

function determinePassFail(marksObj, percentage) {
    for (const sub in marksObj) {
        if (marksObj[sub] < 40.0) {
            return "FAIL";
        }
    }
    return percentage >= 50.0 ? "PASS" : "FAIL";
}

function saveEvaluation(evaluationData) {
    let evaluations = getAllEvaluations();
    // Replace existing if already evaluated, else append
    const existingIndex = evaluations.findIndex(e => e.studentId.trim().toUpperCase() === evaluationData.studentId.trim().toUpperCase());

    if (existingIndex >= 0) {
        evaluations[existingIndex] = evaluationData;
    } else {
        evaluations.unshift(evaluationData);
    }

    saveAllEvaluations(evaluations);
    // Also save as latest active result
    sessionStorage.setItem("gradeCalc_latestResult", JSON.stringify(evaluationData));
    return evaluationData;
}

function getLatestResult() {
    const raw = sessionStorage.getItem("gradeCalc_latestResult");
    if (raw) {
        try { return JSON.parse(raw); } catch(e){}
    }
    const evaluations = getAllEvaluations();
    return evaluations.length > 0 ? evaluations[0] : null;
}

// ==========================================
// 5. SYSTEM STATISTICS (FOR DASHBOARD)
// ==========================================

function getSystemStats() {
    const students = getAllStudents();
    const evaluations = getAllEvaluations();

    const totalStudents = students.length;
    const totalEvaluations = evaluations.length;

    let passCount = 0;
    let failCount = 0;
    let totalPercentageSum = 0;

    evaluations.forEach(ev => {
        if (ev.status === "PASS") passCount++;
        else failCount++;
        totalPercentageSum += (ev.percentage || 0);
    });

    const avgPercentage = totalEvaluations > 0 ? (totalPercentageSum / totalEvaluations).toFixed(1) : "0.0";

    return {
        totalStudents,
        totalEvaluations,
        passCount,
        failCount,
        avgPercentage
    };
}
