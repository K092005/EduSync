package service;

import dao.ResourceDAO;
import model.Resource;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class ResourceService {
    private final ResourceDAO dao = new ResourceDAO();
    public static final String UPLOAD_DIR = "uploads/";

    public String uploadFile(File file, int noteId) {
        try {
            Path dir = Paths.get(UPLOAD_DIR);
            if (!Files.exists(dir)) Files.createDirectories(dir);

            String ext         = getExtension(file.getName());
            String storedName  = System.currentTimeMillis() + "_" + file.getName();
            Path   dest        = dir.resolve(storedName);
            Files.copy(file.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);

            Resource r = new Resource(
                SessionManager.getCurrentUserId(),
                noteId,
                file.getName(),
                storedName,
                dest.toAbsolutePath().toString(),
                classifyType(ext),
                file.length(),
                "General"
            );
            dao.create(r);
            return null;
        } catch (IOException | java.sql.SQLException e) {
            return "Upload failed: " + e.getMessage();
        }
    }

    /** Permission check: only uploader can delete */
    public String deleteResource(int id, int ownerId, String filePath) {
        if (SessionManager.getCurrentUserId() != ownerId)
            return "Permission denied. You can only delete your own files.";
        try {
            new File(filePath).delete();
            dao.delete(id, SessionManager.getCurrentUserId());
            return null;
        } catch (Exception e) {
            return "Delete failed: " + e.getMessage();
        }
    }

    public List<Resource> getAllResources() {
        try { return dao.getAll(); }
        catch (Exception e) { e.printStackTrace(); return new ArrayList<>(); }
    }

    private String getExtension(String filename) {
        int dot = filename.lastIndexOf('.');
        return dot >= 0 ? filename.substring(dot + 1).toLowerCase() : "";
    }

    private String classifyType(String ext) {
        return switch (ext) {
            case "pdf"             -> "PDF";
            case "jpg","jpeg","png","gif","webp" -> "Image";
            case "doc","docx"      -> "Doc";
            case "ppt","pptx"      -> "PPT";
            case "xls","xlsx"      -> "Excel";
            case "zip","rar"       -> "Archive";
            default                -> "Other";
        };
    }
}
