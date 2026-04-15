package dao;

import db.DBConnection;
import model.PYQ;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PYQDAO {

    public PYQ create(PYQ pyq) throws SQLException {
        String sql = "INSERT INTO pyq (user_id, subject, year, title, description, file_path, filename) VALUES (?,?,?,?,?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, pyq.getUserId());
            ps.setString(2, pyq.getSubject());
            ps.setInt(3, pyq.getYear());
            ps.setString(4, pyq.getTitle());
            ps.setString(5, pyq.getDescription());
            ps.setString(6, pyq.getFilePath());
            ps.setString(7, pyq.getFilename());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) pyq.setId(keys.getInt(1));
        }
        return pyq;
    }

    public List<PYQ> getAll() throws SQLException {
        String sql = "SELECT p.*, u.full_name as uploader_name FROM pyq p " +
                     "JOIN users u ON p.user_id=u.id ORDER BY p.year DESC, p.subject ASC";
        return query(sql, null);
    }

    public List<PYQ> getBySubject(String subject) throws SQLException {
        String sql = "SELECT p.*, u.full_name as uploader_name FROM pyq p " +
                     "JOIN users u ON p.user_id=u.id WHERE p.subject=? ORDER BY p.year DESC";
        return query(sql, subject);
    }

    public void delete(int id, int userId) throws SQLException {
        String sql = "DELETE FROM pyq WHERE id=? AND user_id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }

    private List<PYQ> query(String sql, String param) throws SQLException {
        List<PYQ> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            if (param != null) ps.setString(1, param);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                PYQ p = new PYQ();
                p.setId(rs.getInt("id"));
                p.setUserId(rs.getInt("user_id"));
                p.setUploaderName(rs.getString("uploader_name"));
                p.setSubject(rs.getString("subject"));
                p.setYear(rs.getInt("year"));
                p.setTitle(rs.getString("title"));
                p.setDescription(rs.getString("description"));
                p.setFilePath(rs.getString("file_path"));
                p.setFilename(rs.getString("filename"));
                p.setUploadedAt(rs.getTimestamp("uploaded_at"));
                list.add(p);
            }
        }
        return list;
    }
}
