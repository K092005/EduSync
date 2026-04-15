package model;

public class User {
    private int id;
    private String username;
    private String passwordHash;
    private String fullName;
    private String email;
    private String role;

    public User() {}

    public User(int id, String username, String fullName, String email, String role) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
    }

    public int getId()                      { return id; }
    public void setId(int id)              { this.id = id; }
    public String getUsername()             { return username; }
    public void setUsername(String u)       { this.username = u; }
    public String getPasswordHash()         { return passwordHash; }
    public void setPasswordHash(String p)   { this.passwordHash = p; }
    public String getFullName()             { return fullName; }
    public void setFullName(String n)       { this.fullName = n; }
    public String getEmail()                { return email; }
    public void setEmail(String e)          { this.email = e; }
    public String getRole()                 { return role; }
    public void setRole(String r)           { this.role = r; }

    public String getInitials() {
        if (fullName == null || fullName.isEmpty()) return "?";
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length == 1) return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        return ("" + parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase();
    }

    @Override
    public String toString() { return fullName; }
}
