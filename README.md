# Student Grade Calculator

The project contains all four phases: Java models and grade calculation, service-based validation, the browser interface, and server-side registration/login with session tracking.

## Run the integrated application locally

With JDK 11 or newer, compile the project from the project folder:

```powershell
javac -cp "lib/*" -d out src\*.java src\app\*.java src\exception\*.java src\model\*.java src\service\*.java src\servlet\*.java src\util\*.java
java -cp out app.Server
```

Open `http://localhost:8080/login.html`. The demo account is `rahul101` with password `Rahul@123`. Registration and login are validated by the Java server; the dashboard requires its session, and logout uses POST to invalidate it.

## Deploy the Servlet/JSP application to Tomcat 9

This project uses the `javax.servlet` API, so use Tomcat 9 (Tomcat 10+ uses the incompatible `jakarta.servlet` API).

1. Install JDK 11 or newer, Maven, and Apache Tomcat 9.
2. From the project folder, run `mvn clean package` to create `target\StudentGradeCalculator.war`.
3. Copy the WAR into Tomcat's `webapps` folder and start Tomcat. Tomcat will expand the WAR automatically.
4. Open `http://localhost:8080/StudentGradeCalculator/login.html`.

When Maven is unavailable, build with the included JDK and Tomcat installation:

```powershell
$tomcat = "C:\path\to\apache-tomcat-9"
$stage = "$env:TEMP\StudentGradeCalculator-war"
New-Item -ItemType Directory -Force "$stage\WEB-INF\classes" | Out-Null
javac --release 11 -encoding UTF-8 -cp "$tomcat\lib\servlet-api.jar" -d "$stage\WEB-INF\classes" src\*.java src\app\*.java src\exception\*.java src\model\*.java src\service\*.java src\servlet\*.java src\util\*.java
Copy-Item frontend\* $stage -Recurse -Force
New-Item -ItemType Directory -Force target | Out-Null
jar --create --file target\StudentGradeCalculator.war -C $stage .
Copy-Item target\StudentGradeCalculator.war "$tomcat\webapps\" -Force
```

Registered accounts are persisted in JSON at `%CATALINA_BASE%\data\users.json` when running under Tomcat. Passwords are stored as salted hashes, not plaintext. On the first run, the built-in demo and admin accounts are added automatically.

After signing in, the Students page and grade calculator load registered student profiles from the server, so accounts created through registration appear in the student list. Other student records created through the Students page remain browser-local.

To list registered account profile details as JSON, sign in with the built-in admin account and request `/users` using the same session. The endpoint is admin-only and never returns passwords. For production, use a database, HTTPS, and secure session-cookie settings.
