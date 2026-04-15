package dao;

import db.DBConnection;
import model.Message;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO {

    public Message create(Message msg) throws SQLException {
        String sql = "INSERT INTO messages (user_id, channel, content) VALUES (?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, msg.getUserId());
            ps.setString(2, msg.getChannel());
            ps.setString(3, msg.getContent());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) msg.setId(keys.getInt(1));
        }
        return msg;
    }

    public List<Message> getByChannel(String channel) throws SQLException {
        String sql = "SELECT m.*, u.full_name as sender_name FROM messages m " +
                     "JOIN users u ON m.user_id=u.id " +
                     "WHERE m.channel=? ORDER BY m.sent_at ASC LIMIT 100";
        List<Message> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, channel);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Message m = new Message();
                m.setId(rs.getInt("id"));
                m.setUserId(rs.getInt("user_id"));
                m.setSenderName(rs.getString("sender_name"));
                m.setChannel(rs.getString("channel"));
                m.setContent(rs.getString("content"));
                m.setSentAt(rs.getTimestamp("sent_at"));
                list.add(m);
            }
        }
        return list;
    }
}
