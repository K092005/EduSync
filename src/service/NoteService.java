package service;

import dao.NoteDAO;
import model.Note;

import java.util.ArrayList;
import java.util.List;

public class NoteService {
    private final NoteDAO dao = new NoteDAO();

    public String saveNote(Note note) {
        if (note.getTitle() == null || note.getTitle().trim().isEmpty())
            return "Title cannot be empty.";
        note.setUserId(SessionManager.getCurrentUserId());
        try {
            if (note.getId() == 0) dao.create(note);
            else dao.update(note);
            return null;
        } catch (Exception e) {
            return "Could not save note: " + e.getMessage();
        }
    }

    /** Permission check: only owner can delete */
    public String deleteNote(int noteId, int ownerId) {
        if (SessionManager.getCurrentUserId() != ownerId)
            return "Permission denied. You can only delete your own notes.";
        try {
            dao.delete(noteId, SessionManager.getCurrentUserId());
            return null;
        } catch (Exception e) {
            return "Could not delete note: " + e.getMessage();
        }
    }

    public List<Note> getAllNotes() {
        try { return dao.getAll(); }
        catch (Exception e) { e.printStackTrace(); return new ArrayList<>(); }
    }

    public List<Note> searchNotes(String keyword) {
        try { return dao.search(keyword); }
        catch (Exception e) { e.printStackTrace(); return new ArrayList<>(); }
    }
}
