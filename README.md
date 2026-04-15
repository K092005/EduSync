# EduSync — Student Collaboration Platform
### Java + JavaFX WebView + MySQL

---

## What this project covers (for your viva)

| Concept | Where used |
|---|---|
| OOP — Classes, Encapsulation | model/ package (User, Note, Resource, Message, PYQ) |
| OOP — Abstraction | Service layer hides DAO details |
| OOP — Inheritance / Polymorphism | (extendable — User → Admin / Student) |
| JDBC | dao/ package — all CRUD with PreparedStatement |
| Exception Handling | try-catch in every DAO and Service |
| File Handling | ResourceService, PYQService — Java NIO Files.copy |
| Collections | List<Note>, List<Resource> in all service methods |
| Event-Driven Programming | JS onclick → Java bridge calls |
| Layered Architecture | UI → Service → DAO → DB |
| Session Management | SessionManager singleton |
| Password Security | SHA-256 hashing (UserDAO.hashPassword) |
| Permissions | Owner-only edit/delete enforced in Service layer |

---

## Project Structure

```
EduSync/
├── src/
│   ├── MainApp.java              ← JavaFX entry point
│   ├── model/
│   │   ├── User.java
│   │   ├── Note.java
│   │   ├── Resource.java
│   │   ├── Message.java
│   │   └── PYQ.java
│   ├── db/
│   │   └── DBConnection.java     ← Singleton JDBC connection
│   ├── dao/
│   │   ├── UserDAO.java
│   │   ├── NoteDAO.java
│   │   ├── ResourceDAO.java
│   │   ├── MessageDAO.java
│   │   └── PYQDAO.java
│   ├── service/
│   │   ├── SessionManager.java   ← Tracks logged-in user
│   │   ├── UserService.java
│   │   ├── NoteService.java
│   │   ├── ResourceService.java
│   │   ├── ChatService.java
│   │   └── PYQService.java
│   └── bridge/
│       └── JavaBridge.java       ← Java ↔ JavaScript bridge
├── ui/
│   └── index.html                ← Full UI (HTML/CSS/JS)
├── sql/
│   └── setup.sql                 ← Run this ONCE in MySQL
├── uploads/                      ← Created automatically
│   └── pyq/
├── pom.xml                       ← Maven build file
└── README.md
```

---

## ── STEP-BY-STEP SETUP ──────────────────────────────────

### STEP 1 — Install required software

You need all of these installed:

1. **JDK 17+** — https://adoptium.net
   - After install: `java -version` should show 17+

2. **MySQL 8+** — https://dev.mysql.com/downloads/mysql/
   - Remember your root password

3. **Maven 3.8+** — https://maven.apache.org/download.cgi
   - After install: `mvn -version` should work
   - (Or use IntelliJ which has Maven built in)

4. **IntelliJ IDEA Community** — https://www.jetbrains.com/idea/download/
   - This is the easiest way to run the project

---

### STEP 2 — Set up the database

1. Open **MySQL Workbench** (or any MySQL client)
2. Connect to your local MySQL server
3. Open the file: `sql/setup.sql`
4. Run the entire file (Ctrl+Shift+Enter or the lightning bolt button)

This creates:
- Database: `edusync`
- Tables: `users`, `notes`, `resources`, `messages`, `pyq`
- Sample data (4 users, notes, messages, PYQs)

Demo login credentials:
- Username: `demo`  Password: `password`
- Username: `isha`  Password: `password`
- Username: `sadaf` Password: `password`

---

### STEP 3 — Configure database password

Open `src/db/DBConnection.java` and change line 11:

```java
private static final String DB_PASS = "your_password_here"; // ← PUT YOUR MYSQL ROOT PASSWORD HERE
```

---

### STEP 4A — Run in IntelliJ IDEA (Recommended)

1. Open IntelliJ IDEA
2. File → Open → select the `EduSync/` folder
3. Wait for Maven to download dependencies (bottom progress bar)
4. Once ready, open `src/MainApp.java`
5. Right-click → **Run 'MainApp.main()'**
6. The app window opens!

If you see "JavaFX runtime components are missing":
- Go to Run → Edit Configurations
- Add VM options: `--module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.web,javafx.fxml`
- (IntelliJ usually handles this automatically with Maven)

---

### STEP 4B — Run from Terminal (Maven)

```bash
# Navigate to project folder
cd EduSync

# Download dependencies and compile
mvn clean compile

# Run the app
mvn javafx:run
```

---

### STEP 4C — Run in Eclipse

1. File → Import → Maven → Existing Maven Projects → select EduSync folder
2. Wait for build
3. Right-click `src/MainApp.java` → Run As → Java Application
4. If JavaFX error: Add JavaFX to the build path via Project Properties

---

## ── HOW THE APP WORKS ───────────────────────────────────

### Login / Signup
- Enter username + password → Java's `UserService.login()` → `UserDAO` → MySQL
- Password stored as SHA-256 hash (never plain text)
- On success → `SessionManager.login(user)` stores user in memory

### Notes
- View all notes from all users (read-only for others)
- Only owner sees Edit/Delete buttons (enforced in `NoteService`)
- Search works across title, content, subject

### Resources
- Click "Upload File" → JavaFX `FileChooser` opens
- File is copied to `uploads/` folder with a timestamp prefix
- Path + metadata saved to MySQL
- All users can Open; only uploader can Delete

### Chat
- 4 channels stored in `messages` table
- Messages loaded from DB on channel switch
- New messages saved immediately to DB

### PYQ Bank
- Click "Upload PYQ" → file chooser → metadata modal
- PDF saved to `uploads/pyq/`, record in `pyq` table
- Filter by subject using chips

### Permissions
- Permission rules shown in the Permissions panel
- Enforced in `NoteService.deleteNote()` and `ResourceService.deleteResource()`

---

## ── WHAT TO SAY IN VIVA ────────────────────────────────

> "EduSync is a Java-based student collaboration platform built using JavaFX WebView with a modern HTML/CSS frontend rendered inside the desktop app. The architecture follows a strict 4-layer pattern: UI layer (HTML/JS), Service layer (business logic), DAO layer (JDBC database operations), and Model layer (POJOs).
>
> The UI communicates with Java through a JavaBridge class that exposes Java methods to JavaScript via window.java — this is JavaFX's JSObject bridge mechanism.
>
> Key features include secure authentication with SHA-256 password hashing, a full note management system with owner-based permissions enforced in the service layer, file upload using Java NIO, a real-time chat board with 4 subject channels, and a PYQ bank. Session management is handled by a Singleton SessionManager that tracks the logged-in user throughout the application lifecycle without any external libraries."

---

## ── TROUBLESHOOTING ─────────────────────────────────────

| Problem | Fix |
|---|---|
| `Communications link failure` | MySQL not running. Start MySQL service. |
| `Access denied for user 'root'` | Wrong password in `DBConnection.java` |
| `ClassNotFoundException: com.mysql.cj.jdbc.Driver` | mysql-connector JAR not in classpath. Check Maven downloaded it. |
| `WebEngine failed to load page` | `ui/index.html` not found. Make sure the `ui/` folder is in the project root. |
| Blank white screen | Check console for JS errors. Usually a `java.xxx` call failed. |
| Font not loading | Normal if no internet — Google Fonts fallback to system fonts. |
| `java.lang.module` errors | Using Java 9+ module system. Add `--add-opens` flags or use Maven plugin. |

---

## ── QUICK REFERENCE ─────────────────────────────────────

### Default ports
- MySQL: `3306`
- App: Desktop app (no port)

### Files that need editing before first run
1. `src/db/DBConnection.java` — set your MySQL password
2. That's it!

### Keyboard shortcuts in the app
- `Ctrl+N` — New note (anywhere in the app)
- `Enter` on login screen — Submit login
