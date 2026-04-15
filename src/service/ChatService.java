package service;

import dao.MessageDAO;
import model.Message;

import java.util.ArrayList;
import java.util.List;

public class ChatService {
    private final MessageDAO dao = new MessageDAO();

    public String sendMessage(String channel, String content) {
        if (content == null || content.trim().isEmpty()) return "Message cannot be empty.";
        try {
            Message m = new Message(SessionManager.getCurrentUserId(), channel, content.trim());
            dao.create(m);
            return null;
        } catch (Exception e) {
            return "Could not send message: " + e.getMessage();
        }
    }

    public List<Message> getMessages(String channel) {
        try { return dao.getByChannel(channel); }
        catch (Exception e) { e.printStackTrace(); return new ArrayList<>(); }
    }
}
