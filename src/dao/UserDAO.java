package dao;

import db.DBConnection;
import model.User;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.*;

public class UserDAO {

    /** SHA-256 hash of a password */
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Hashing failed", e);
        }
    }

    /** Register a new user. Returns null on success, error string on failure. */
    public String register(String username, String fullName, String email, String password) throws SQLException {
        if (usernameExists(username)) return "Username already taken.";
        String sql = "INSERT INTO users (username, password_hash, full_name, email) VALUES (?,?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            ps.setString(2, hashPassword(password));
            ps.setString(3, fullName.trim());
            ps.setString(4, email.trim());
            ps.executeUpdate();
            return null;
        }
    }

    /** Login. Returns User on success, null if credentials wrong. */
    public User login(String username, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE username=? AND password_hash=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            ps.setString(2, hashPassword(password));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        }
        return null;
    }

    public boolean usernameExists(String username) throws SQLException {
        String sql = "SELECT id FROM users WHERE username=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            return ps.executeQuery().next();
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setUsername(rs.getString("username"));
        u.setFullName(rs.getString("full_name"));
        u.setEmail(rs.getString("email"));
        u.setRole(rs.getString("role"));
        return u;
    }
}
