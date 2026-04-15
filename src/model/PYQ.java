package model;

import java.sql.Timestamp;

public class PYQ {
    private int id;
    private int userId;
    private String uploaderName;
    private String subject;
    private int year;
    private String title;
    private String description;
    private String filePath;
    private String filename;
    private Timestamp uploadedAt;

    public PYQ() {}

    public int getId()                      { return id; }
    public void setId(int id)              { this.id = id; }
    public int getUserId()                  { return userId; }
    public void setUserId(int u)            { this.userId = u; }
    public String getUploaderName()         { return uploaderName; }
    public void setUploaderName(String n)   { this.uploaderName = n; }
    public String getSubject()              { return subject; }
    public void setSubject(String s)        { this.subject = s; }
    public int getYear()                    { return year; }
    public void setYear(int y)              { this.year = y; }
    public String getTitle()                { return title; }
    public void setTitle(String t)          { this.title = t; }
    public String getDescription()          { return description; }
    public void setDescription(String d)    { this.description = d; }
    public String getFilePath()             { return filePath; }
    public void setFilePath(String p)       { this.filePath = p; }
    public String getFilename()             { return filename; }
    public void setFilename(String f)       { this.filename = f; }
    public Timestamp getUploadedAt()        { return uploadedAt; }
    public void setUploadedAt(Timestamp t)  { this.uploadedAt = t; }
}
