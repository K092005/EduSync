package model;

import java.sql.Timestamp;

public class Message {
    private int id;
    private int userId;
    private String senderName;
    private String senderInitials;
    private String channel;
    private String content;
    private Timestamp sentAt;

    public Message() {}

    public Message(int userId, String channel, String content) {
        this.userId = userId;
        this.channel = channel;
        this.content = content;
    }

    public int getId()                      { return id; }
    public void setId(int id)              { this.id = id; }
    public int getUserId()                  { return userId; }
    public void setUserId(int u)            { this.userId = u; }
    public String getSenderName()           { return senderName; }
    public void setSenderName(String n)     { this.senderName = n; }
    public String getSenderInitials()       { return senderInitials; }
    public void setSenderInitials(String i) { this.senderInitials = i; }
    public String getChannel()              { return channel; }
    public void setChannel(String c)        { this.channel = c; }
    public String getContent()              { return content; }
    public void setContent(String c)        { this.content = c; }
    public Timestamp getSentAt()            { return sentAt; }
    public void setSentAt(Timestamp t)      { this.sentAt = t; }
}
