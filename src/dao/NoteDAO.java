package dao;

import db.DBConnection;
import model.Note;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NoteDAO {

    public Note create(Note note) throws SQLException {
        String sql = "INSERT INTO notes (user_id, title, content, subject) VALUES (?,?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, note.getUserId());
            ps.setString(2, note.getTitle());
            ps.setString(3, note.getContent());
            ps.setString(4, note.getSubject());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) note.setId(keys.getInt(1));
        }
        return note;
    }

    public void update(Note note) throws SQLException {
        String sql = "UPDATE notes SET title=?, content=?, subject=?, updated_at=NOW() WHERE id=? AND user_id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, note.getTitle());
            ps.setString(2, note.getContent());
            ps.setString(3, note.getSubject());
            ps.setInt(4, note.getId());
            ps.setInt(5, note.getUserId());
            ps.executeUpdate();
        }
    }

    public void delete(int noteId, int userId) throws SQLException {
        String sql = "DELETE FROM notes WHERE id=? AND user_id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, noteId);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }

    /** Get all notes — all users can view (read-only for non-owners) */
    public List<Note> getAll() throws SQLException {
        return query("SELECT n.*, u.full_name as owner_name FROM notes n " +
                     "JOIN users u ON n.user_id=u.id ORDER BY n.updated_at DESC", null);
    }

    public List<Note> getByUser(int userId) throws SQLException {
        return query("SELECT n.*, u.full_name as owner_name FROM notes n " +
                     "JOIN users u ON n.user_id=u.id WHERE n.user_id=? ORDER BY n.updated_at DESC", userId);
    }

    public List<Note> search(String keyword) throws SQLException {
        String sql = "SELECT n.*, u.full_name as owner_name FROM notes n " +
                     "JOIN users u ON n.user_id=u.id " +
                     "WHERE n.title LIKE ? OR n.content LIKE ? OR n.subject LIKE ? " +
                     "ORDER BY n.updated_at DESC";
        List<Note> notes = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            String kw = "%" + keyword + "%";
            ps.setString(1, kw); ps.setString(2, kw); ps.setString(3, kw);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) notes.add(mapRow(rs));
        }
        return notes;
    }

    private List<Note> query(String sql, Integer userId) throws SQLException {
        List<Note> notes = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            if (userId != null) ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) notes.add(mapRow(rs));
        }
        return notes;
    }

    private Note mapRow(ResultSet rs) throws SQLException {
        Note n = new Note();
        n.setId(rs.getInt("id"));
        n.setUserId(rs.getInt("user_id"));
        n.setOwnerName(rs.getString("owner_name"));
        n.setTitle(rs.getString("title"));
        n.setContent(rs.getString("content"));
        n.setSubject(rs.getString("subject"));
        n.setCreatedAt(rs.getTimestamp("created_at"));
        n.setUpdatedAt(rs.getTimestamp("updated_at"));
        return n;
    }
}
