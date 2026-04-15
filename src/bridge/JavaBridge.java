package bridge;

import javafx.application.Platform;
import javafx.scene.web.WebEngine;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.*;
import service.*;

import java.io.File;
import java.util.List;

/**
 * JavaBridge exposes Java methods to JavaScript running in WebView.
 * JS calls: window.java.methodName(args)
 */
public class JavaBridge {

    private final WebEngine engine;
    private final Stage     stage;

    private final UserService     userService     = new UserService();
    private final NoteService     noteService     = new NoteService();
    private final ResourceService resourceService = new ResourceService();
    private final ChatService     chatService     = new ChatService();
    private final PYQService      pyqService      = new PYQService();

    public JavaBridge(WebEngine engine, Stage stage) {
        this.engine = engine;
        this.stage  = stage;
    }

    // ── Helpers ─────────────────────────────────────────────

    private void runJS(String script) {
        Platform.runLater(() -> engine.executeScript(script));
    }

    private String escJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "")
                .replace("\t", "  ");
    }

    // ── AUTH ─────────────────────────────────────────────────

    public String login(String username, String password) {
        String err = userService.login(username, password);
        if (err != null) return "{\"error\":\"" + escJson(err) + "\"}";
        User u = SessionManager.getCurrentUser();
        return "{\"id\":" + u.getId() +
               ",\"username\":\"" + escJson(u.getUsername()) + "\"" +
               ",\"fullName\":\"" + escJson(u.getFullName()) + "\"" +
               ",\"initials\":\"" + escJson(u.getInitials()) + "\"" +
               ",\"token\":\"" + SessionManager.getSessionToken() + "\"}";
    }

    public String register(String username, String fullName, String email, String password) {
        String err = userService.register(username, fullName, email, password);
        if (err != null) return "{\"error\":\"" + escJson(err) + "\"}";
        return "{\"ok\":true}";
    }

    public void logout() {
        userService.logout();
    }

    // ── NOTES ────────────────────────────────────────────────

    public String getNotes() {
        List<Note> notes = noteService.getAllNotes();
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < notes.size(); i++) {
            Note n = notes.get(i);
            if (i > 0) sb.append(",");
            sb.append("{")
              .append("\"id\":").append(n.getId()).append(",")
              .append("\"userId\":").append(n.getUserId()).append(",")
              .append("\"ownerName\":\"").append(escJson(n.getOwnerName())).append("\",")
              .append("\"title\":\"").append(escJson(n.getTitle())).append("\",")
              .append("\"content\":\"").append(escJson(n.getContent())).append("\",")
              .append("\"subject\":\"").append(escJson(n.getSubject())).append("\",")
              .append("\"updatedAt\":\"").append(n.getUpdatedAt() != null ? n.getUpdatedAt().toString().substring(0,10) : "").append("\"")
              .append("}");
        }
        sb.append("]");
        return sb.toString();
    }

    public String saveNote(int id, String title, String content, String subject) {
        Note n = new Note();
        n.setId(id);
        n.setTitle(title);
        n.setContent(content);
        n.setSubject(subject);
        String err = noteService.saveNote(n);
        if (err != null) return "{\"error\":\"" + escJson(err) + "\"}";
        return "{\"ok\":true,\"id\":" + n.getId() + "}";
    }

    public String deleteNote(int noteId, int ownerId) {
        String err = noteService.deleteNote(noteId, ownerId);
        if (err != null) return "{\"error\":\"" + escJson(err) + "\"}";
        return "{\"ok\":true}";
    }

    public String searchNotes(String keyword) {
        List<Note> notes = noteService.searchNotes(keyword);
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < notes.size(); i++) {
            Note n = notes.get(i);
            if (i > 0) sb.append(",");
            sb.append("{")
              .append("\"id\":").append(n.getId()).append(",")
              .append("\"userId\":").append(n.getUserId()).append(",")
              .append("\"ownerName\":\"").append(escJson(n.getOwnerName())).append("\",")
              .append("\"title\":\"").append(escJson(n.getTitle())).append("\",")
              .append("\"content\":\"").append(escJson(n.getContent())).append("\",")
              .append("\"subject\":\"").append(escJson(n.getSubject())).append("\",")
              .append("\"updatedAt\":\"").append(n.getUpdatedAt() != null ? n.getUpdatedAt().toString().substring(0,10) : "").append("\"")
              .append("}");
        }
        sb.append("]");
        return sb.toString();
    }

    // ── RESOURCES ────────────────────────────────────────────

    public String getResources() {
        List<Resource> list = resourceService.getAllResources();
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            Resource r = list.get(i);
            if (i > 0) sb.append(",");
            sb.append("{")
              .append("\"id\":").append(r.getId()).append(",")
              .append("\"userId\":").append(r.getUserId()).append(",")
              .append("\"ownerName\":\"").append(escJson(r.getOwnerName())).append("\",")
              .append("\"filename\":\"").append(escJson(r.getFilename())).append("\",")
              .append("\"fileType\":\"").append(escJson(r.getFileType())).append("\",")
              .append("\"fileSize\":\"").append(escJson(r.getFormattedSize())).append("\",")
              .append("\"filePath\":\"").append(escJson(r.getFilePath())).append("\",")
              .append("\"uploadedAt\":\"").append(r.getUploadedAt() != null ? r.getUploadedAt().toString().substring(0,10) : "").append("\"")
              .append("}");
        }
        sb.append("]");
        return sb.toString();
    }

    /** Opens native file chooser, uploads selected file, refreshes UI */
    public void openFileChooser(int noteId) {
        Platform.runLater(() -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Select File to Upload");
            fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Files", "*.*"),
                new FileChooser.ExtensionFilter("PDF", "*.pdf"),
                new FileChooser.ExtensionFilter("Images", "*.jpg","*.jpeg","*.png","*.gif"),
                new FileChooser.ExtensionFilter("Documents", "*.doc","*.docx","*.ppt","*.pptx")
            );
            File file = fc.showOpenDialog(stage);
            if (file != null) {
                String err = resourceService.uploadFile(file, noteId);
                if (err == null) {
                    engine.executeScript("showToast('Uploaded: " + escJson(file.getName()) + "','success'); loadResources();");
                } else {
                    engine.executeScript("showToast('" + escJson(err) + "','error');");
                }
            }
        });
    }

    public String deleteResource(int id, int ownerId, String filePath) {
        String err = resourceService.deleteResource(id, ownerId, filePath);
        if (err != null) return "{\"error\":\"" + escJson(err) + "\"}";
        return "{\"ok\":true}";
    }

    /** Open a file with the system default application */
    public void openFile(String filePath) {
        Platform.runLater(() -> {
            try {
                java.awt.Desktop.getDesktop().open(new File(filePath));
            } catch (Exception e) {
                engine.executeScript("showToast('Cannot open file: " + escJson(e.getMessage()) + "','error');");
            }
        });
    }

    // ── CHAT ─────────────────────────────────────────────────

    public String getMessages(String channel) {
        List<Message> list = chatService.getMessages(channel);
        int myId = SessionManager.getCurrentUserId();
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            Message m = list.get(i);
            if (i > 0) sb.append(",");
            // Build initials from sender name
            String name = m.getSenderName() != null ? m.getSenderName() : "?";
            String[] parts = name.trim().split("\\s+");
            String initials = parts.length == 1
                ? parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase()
                : ("" + parts[0].charAt(0) + parts[parts.length-1].charAt(0)).toUpperCase();
            String time = m.getSentAt() != null
                ? m.getSentAt().toLocalDateTime().toLocalTime().toString().substring(0,5)
                : "";
            sb.append("{")
              .append("\"id\":").append(m.getId()).append(",")
              .append("\"userId\":").append(m.getUserId()).append(",")
              .append("\"senderName\":\"").append(escJson(name)).append("\",")
              .append("\"initials\":\"").append(initials).append("\",")
              .append("\"content\":\"").append(escJson(m.getContent())).append("\",")
              .append("\"time\":\"").append(time).append("\",")
              .append("\"mine\":").append(m.getUserId() == myId)
              .append("}");
        }
        sb.append("]");
        return sb.toString();
    }

    public String sendMessage(String channel, String content) {
        String err = chatService.sendMessage(channel, content);
        if (err != null) return "{\"error\":\"" + escJson(err) + "\"}";
        return "{\"ok\":true}";
    }

    // ── PYQ ──────────────────────────────────────────────────

    public String getPYQs(String subject) {
        List<PYQ> list = subject == null || subject.equals("all")
            ? pyqService.getAll()
            : pyqService.getBySubject(subject);
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            PYQ p = list.get(i);
            if (i > 0) sb.append(",");
            sb.append("{")
              .append("\"id\":").append(p.getId()).append(",")
              .append("\"subject\":\"").append(escJson(p.getSubject())).append("\",")
              .append("\"year\":").append(p.getYear()).append(",")
              .append("\"title\":\"").append(escJson(p.getTitle())).append("\",")
              .append("\"description\":\"").append(escJson(p.getDescription())).append("\",")
              .append("\"filename\":\"").append(escJson(p.getFilename() != null ? p.getFilename() : "")).append("\",")
              .append("\"filePath\":\"").append(escJson(p.getFilePath() != null ? p.getFilePath() : "")).append("\"")
              .append("}");
        }
        sb.append("]");
        return sb.toString();
    }

    public void openPYQChooser() {
        Platform.runLater(() -> {
            // First: pick the file
            FileChooser fc = new FileChooser();
            fc.setTitle("Select PYQ PDF");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            File file = fc.showOpenDialog(stage);
            if (file == null) return;

            // Then: get metadata from user via JS prompt-like dialog
            engine.executeScript(
                "openPYQMetaDialog('" + escJson(file.getAbsolutePath()) + "','" + escJson(file.getName()) + "');"
            );
        });
    }

    public String uploadPYQ(String filePath, String subject, int year, String title, String description) {
        File file = new File(filePath);
        String err = pyqService.uploadPYQ(file, subject, year, title, description);
        if (err != null) return "{\"error\":\"" + escJson(err) + "\"}";
        return "{\"ok\":true}";
    }

    // ── STATS ─────────────────────────────────────────────────

    public String getStats() {
        int notes = noteService.getAllNotes().size();
        int files = resourceService.getAllResources().size();
        int pyqs  = pyqService.getAll().size();
        return "{\"notes\":" + notes + ",\"files\":" + files + ",\"pyqs\":" + pyqs + "}";
    }
}
