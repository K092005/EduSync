package service;

import dao.UserDAO;
import model.User;

public class UserService {
    private final UserDAO dao = new UserDAO();

    /** Returns null on success, error message on failure */
    public String login(String username, String password) {
        if (username == null || username.trim().isEmpty()) return "Username cannot be empty.";
        if (password == null || password.isEmpty())        return "Password cannot be empty.";
        try {
            User user = dao.login(username, password);
            if (user == null) return "Invalid username or password.";
            SessionManager.login(user);
            return null;
        } catch (Exception e) {
            return "Login error: " + e.getMessage();
        }
    }

    /** Returns null on success, error message on failure */
    public String register(String username, String fullName, String email, String password) {
        if (username == null || username.trim().isEmpty())  return "Username cannot be empty.";
        if (fullName == null || fullName.trim().isEmpty())  return "Full name cannot be empty.";
        if (password == null || password.length() < 6)     return "Password must be at least 6 characters.";
        try {
            return dao.register(username, fullName, email, password);
        } catch (Exception e) {
            return "Registration error: " + e.getMessage();
        }
    }

    public void logout() { SessionManager.logout(); }
}
