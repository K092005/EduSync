package service;

import dao.PYQDAO;
import model.PYQ;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class PYQService {
    private final PYQDAO dao = new PYQDAO();
    public static final String PYQ_DIR = "uploads/pyq/";

    public String uploadPYQ(File file, String subject, int year, String title, String description) {
        try {
            Path dir = Paths.get(PYQ_DIR);
            if (!Files.exists(dir)) Files.createDirectories(dir);

            String storedName = System.currentTimeMillis() + "_" + file.getName();
            Path   dest       = dir.resolve(storedName);
            Files.copy(file.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);

            PYQ p = new PYQ();
            p.setUserId(SessionManager.getCurrentUserId());
            p.setSubject(subject);
            p.setYear(year);
            p.setTitle(title);
            p.setDescription(description);
            p.setFilePath(dest.toAbsolutePath().toString());
            p.setFilename(file.getName());
            dao.create(p);
            return null;
        } catch (IOException | java.sql.SQLException e) {
            return "Upload failed: " + e.getMessage();
        }
    }

    public List<PYQ> getAll() {
        try { return dao.getAll(); }
        catch (Exception e) { e.printStackTrace(); return new ArrayList<>(); }
    }

    public List<PYQ> getBySubject(String subject) {
        try { return dao.getBySubject(subject); }
        catch (Exception e) { e.printStackTrace(); return new ArrayList<>(); }
    }
}
