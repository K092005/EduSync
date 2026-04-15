package model;

import java.sql.Timestamp;

public class Resource {
    private int id;
    private int userId;
    private int noteId;
    private String ownerName;
    private String filename;
    private String storedName;
    private String filePath;
    private String fileType;
    private long fileSize;
    private String category;
    private Timestamp uploadedAt;

    public Resource() {}

    public Resource(int userId, int noteId, String filename, String storedName,
                    String filePath, String fileType, long fileSize, String category) {
        this.userId = userId;
        this.noteId = noteId;
        this.filename = filename;
        this.storedName = storedName;
        this.filePath = filePath;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.category = category;
    }

    public int getId()                      { return id; }
    public void setId(int id)              { this.id = id; }
    public int getUserId()                  { return userId; }
    public void setUserId(int u)            { this.userId = u; }
    public int getNoteId()                  { return noteId; }
    public void setNoteId(int n)            { this.noteId = n; }
    public String getOwnerName()            { return ownerName; }
    public void setOwnerName(String n)      { this.ownerName = n; }
    public String getFilename()             { return filename; }
    public void setFilename(String f)       { this.filename = f; }
    public String getStoredName()           { return storedName; }
    public void setStoredName(String s)     { this.storedName = s; }
    public String getFilePath()             { return filePath; }
    public void setFilePath(String p)       { this.filePath = p; }
    public String getFileType()             { return fileType; }
    public void setFileType(String t)       { this.fileType = t; }
    public long getFileSize()               { return fileSize; }
    public void setFileSize(long s)         { this.fileSize = s; }
    public String getCategory()             { return category; }
    public void setCategory(String c)       { this.category = c; }
    public Timestamp getUploadedAt()        { return uploadedAt; }
    public void setUploadedAt(Timestamp t)  { this.uploadedAt = t; }

    public String getFormattedSize() {
        if (fileSize < 1024) return fileSize + " B";
        else if (fileSize < 1024 * 1024) return String.format("%.1f KB", fileSize / 1024.0);
        else return String.format("%.1f MB", fileSize / (1024.0 * 1024));
    }

    @Override
    public String toString() { return filename; }
}
