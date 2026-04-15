package service;

import model.User;
import java.util.UUID;

/**
 * In-memory session — holds the currently logged-in user.
 * Resets on logout or app restart.
 */
public class SessionManager {
    private static User currentUser;
    private static String sessionToken;

    private SessionManager() {}

    public static void login(User user) {
        currentUser   = user;
        sessionToken  = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        System.out.println("[Session] Logged in: " + user.getUsername() + " | token=" + sessionToken);
    }

    public static void logout() {
        System.out.println("[Session] Logged out: " + (currentUser != null ? currentUser.getUsername() : "?"));
        currentUser  = null;
        sessionToken = null;
    }

    public static boolean isLoggedIn()      { return currentUser != null; }
    public static User    getCurrentUser()   { return currentUser; }
    public static String  getSessionToken()  { return sessionToken; }
    public static int     getCurrentUserId() { return currentUser != null ? currentUser.getId() : -1; }
}
