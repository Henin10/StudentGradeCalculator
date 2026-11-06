package service;

import model.User;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

final class UserJsonStore {
    private static final Path FILE = dataFile();

    private UserJsonStore() {
    }

    static List<User> load() throws IOException {
        if (!Files.exists(FILE)) {
            return new ArrayList<>();
        }

        String json = new String(Files.readAllBytes(FILE), StandardCharsets.UTF_8);
        FlatJsonArrayReader reader = new FlatJsonArrayReader(json);
        List<User> users = new ArrayList<>();
        for (Map<String, String> fields : reader.readObjects()) {
            String username = required(fields, "username");
            String passwordHash = required(fields, "passwordHash");
            User user = new User(
                    username,
                    passwordHash,
                    required(fields, "email"),
                    required(fields, "studentId"),
                    required(fields, "studentName"),
                    required(fields, "role")
            );
            user.setCourse(fields.getOrDefault("course", "MCA"));
            user.setSemester(parseSemester(fields.get("semester")));
            users.add(user);
        }
        return users;
    }

    static void save(Iterable<User> users) throws IOException {
        Files.createDirectories(FILE.getParent());
        StringBuilder json = new StringBuilder("[\n");
        boolean first = true;
        for (User user : users) {
            if (!first) {
                json.append(",\n");
            }
            first = false;
            json.append("  {")
                    .append("\"username\":").append(quote(user.getUsername())).append(',')
                    .append("\"passwordHash\":").append(quote(user.getPassword())).append(',')
                    .append("\"email\":").append(quote(user.getEmail())).append(',')
                    .append("\"studentId\":").append(quote(user.getStudentId())).append(',')
                    .append("\"studentName\":").append(quote(user.getStudentName())).append(',')
                    .append("\"role\":").append(quote(user.getRole())).append(',')
                    .append("\"course\":").append(quote(user.getCourse())).append(',')
                    .append("\"semester\":").append(quote(Integer.toString(user.getSemester())))
                    .append('}');
        }
        json.append("\n]\n");

        Path temporaryFile = FILE.resolveSibling(FILE.getFileName() + ".tmp");
        Files.write(temporaryFile, json.toString().getBytes(StandardCharsets.UTF_8));
        try {
            Files.move(temporaryFile, FILE, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException atomicMoveFailure) {
            Files.move(temporaryFile, FILE, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static Path dataFile() {
        String catalinaBase = System.getProperty("catalina.base");
        Path base = catalinaBase == null || catalinaBase.trim().isEmpty()
                ? Paths.get(System.getProperty("user.dir"))
                : Paths.get(catalinaBase);
        return base.resolve("data").resolve("users.json");
    }

    private static String required(Map<String, String> fields, String name) throws IOException {
        String value = fields.get(name);
        if (value == null) {
            throw new IOException("Missing '" + name + "' in " + FILE);
        }
        return value;
    }

    private static int parseSemester(String value) throws IOException {
        try {
            return value == null ? 1 : Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IOException("Invalid semester in " + FILE, e);
        }
    }

    private static String quote(String value) {
        StringBuilder escaped = new StringBuilder("\"");
        if (value != null) {
            for (int i = 0; i < value.length(); i++) {
                char character = value.charAt(i);
                switch (character) {
                    case '"': escaped.append("\\\""); break;
                    case '\\': escaped.append("\\\\"); break;
                    case '\b': escaped.append("\\b"); break;
                    case '\f': escaped.append("\\f"); break;
                    case '\n': escaped.append("\\n"); break;
                    case '\r': escaped.append("\\r"); break;
                    case '\t': escaped.append("\\t"); break;
                    default:
                        if (character < 0x20) {
                            escaped.append(String.format("\\u%04x", (int) character));
                        } else {
                            escaped.append(character);
                        }
                }
            }
        }
        return escaped.append('"').toString();
    }

    private static final class FlatJsonArrayReader {
        private final String json;
        private int index;

        private FlatJsonArrayReader(String json) {
            this.json = json;
        }

        private List<Map<String, String>> readObjects() throws IOException {
            List<Map<String, String>> objects = new ArrayList<>();
            skipWhitespace();
            expect('[');
            skipWhitespace();
            if (consume(']')) {
                return objects;
            }
            while (true) {
                objects.add(readObject());
                skipWhitespace();
                if (consume(']')) {
                    break;
                }
                expect(',');
                skipWhitespace();
            }
            skipWhitespace();
            if (index != json.length()) {
                throw invalidJson();
            }
            return objects;
        }

        private Map<String, String> readObject() throws IOException {
            Map<String, String> fields = new java.util.LinkedHashMap<>();
            expect('{');
            skipWhitespace();
            if (consume('}')) {
                return fields;
            }
            while (true) {
                String key = readString();
                skipWhitespace();
                expect(':');
                skipWhitespace();
                String value = readString();
                fields.put(key, value);
                skipWhitespace();
                if (consume('}')) {
                    return fields;
                }
                expect(',');
                skipWhitespace();
            }
        }

        private String readString() throws IOException {
            expect('"');
            StringBuilder value = new StringBuilder();
            while (index < json.length()) {
                char character = json.charAt(index++);
                if (character == '"') {
                    return value.toString();
                }
                if (character != '\\') {
                    value.append(character);
                    continue;
                }
                if (index >= json.length()) {
                    throw invalidJson();
                }
                char escape = json.charAt(index++);
                switch (escape) {
                    case '"': value.append('"'); break;
                    case '\\': value.append('\\'); break;
                    case '/': value.append('/'); break;
                    case 'b': value.append('\b'); break;
                    case 'f': value.append('\f'); break;
                    case 'n': value.append('\n'); break;
                    case 'r': value.append('\r'); break;
                    case 't': value.append('\t'); break;
                    case 'u':
                        if (index + 4 > json.length()) {
                            throw invalidJson();
                        }
                        try {
                            value.append((char) Integer.parseInt(json.substring(index, index + 4), 16));
                        } catch (NumberFormatException e) {
                            throw invalidJson();
                        }
                        index += 4;
                        break;
                    default: throw invalidJson();
                }
            }
            throw invalidJson();
        }

        private void expect(char expected) throws IOException {
            if (index >= json.length() || json.charAt(index++) != expected) {
                throw invalidJson();
            }
        }

        private boolean consume(char expected) {
            if (index < json.length() && json.charAt(index) == expected) {
                index++;
                return true;
            }
            return false;
        }

        private void skipWhitespace() {
            while (index < json.length() && Character.isWhitespace(json.charAt(index))) {
                index++;
            }
        }

        private IOException invalidJson() {
            return new IOException("Invalid JSON in " + FILE + " near character " + index);
        }
    }
}
