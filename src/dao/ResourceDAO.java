package dao;

import db.DBConnection;
import model.Resource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResourceDAO {

    public Resource create(Resource r) throws SQLException {
        String sql = "INSERT INTO resources (user_id, note_id, filename, stored_name, file_path, file_type, file_size, category) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, r.getUserId());
            ps.setInt(2, r.getNoteId());
            ps.setString(3, r.getFilename());
            ps.setString(4, r.getStoredName());
            ps.setString(5, r.getFilePath());
            ps.setString(6, r.getFileType());
            ps.setLong(7, r.getFileSize());
            ps.setString(8, r.getCategory());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) r.setId(keys.getInt(1));
        }
        return r;
    }

    public void delete(int id, int userId) throws SQLException {
        String sql = "DELETE FROM resources WHERE id=? AND user_id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }

    public List<Resource> getAll() throws SQLException {
        String sql = "SELECT r.*, u.full_name as owner_name FROM resources r " +
                     "JOIN users u ON r.user_id=u.id ORDER BY r.uploaded_at DESC";
        return query(sql, null);
    }

    public List<Resource> getByUser(int userId) throws SQLException {
        String sql = "SELECT r.*, u.full_name as owner_name FROM resources r " +
                     "JOIN users u ON r.user_id=u.id WHERE r.user_id=? ORDER BY r.uploaded_at DESC";
        return query(sql, userId);
    }

    private List<Resource> query(String sql, Integer userId) throws SQLException {
        List<Resource> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            if (userId != null) ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    private Resource mapRow(ResultSet rs) throws SQLException {
        Resource r = new Resource();
        r.setId(rs.getInt("id"));
        r.setUserId(rs.getInt("user_id"));
        r.setOwnerName(rs.getString("owner_name"));
        r.setNoteId(rs.getInt("note_id"));
        r.setFilename(rs.getString("filename"));
        r.setStoredName(rs.getString("stored_name"));
        r.setFilePath(rs.getString("file_path"));
        r.setFileType(rs.getString("file_type"));
        r.setFileSize(rs.getLong("file_size"));
        r.setCategory(rs.getString("category"));
        r.setUploadedAt(rs.getTimestamp("uploaded_at"));
        return r;
    }
}
