package model;

import java.sql.Timestamp;

public class Note {
    private int id;
    private int userId;
    private String ownerName;
    private String title;
    private String content;
    private String subject;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Note() {}

    public Note(int userId, String title, String content, String subject) {
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.subject = subject;
    }

    public int getId()                      { return id; }
    public void setId(int id)              { this.id = id; }
    public int getUserId()                  { return userId; }
    public void setUserId(int uid)          { this.userId = uid; }
    public String getOwnerName()            { return ownerName; }
    public void setOwnerName(String n)      { this.ownerName = n; }
    public String getTitle()                { return title; }
    public void setTitle(String t)          { this.title = t; }
    public String getContent()              { return content; }
    public void setContent(String c)        { this.content = c; }
    public String getSubject()              { return subject; }
    public void setSubject(String s)        { this.subject = s; }
    public Timestamp getCreatedAt()         { return createdAt; }
    public void setCreatedAt(Timestamp t)   { this.createdAt = t; }
    public Timestamp getUpdatedAt()         { return updatedAt; }
    public void setUpdatedAt(Timestamp t)   { this.updatedAt = t; }

    @Override
    public String toString() { return title; }
}
