package org.pawconnect.controller;

import org.pawconnect.dao.UserDAO;
import org.pawconnect.model.User;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller managing user authentication, active sessions, and role listeners.
 */
public class AuthController {
    private static AuthController instance;
    private final UserDAO userDAO;
    private User currentUser;
    private final List<AuthListener> listeners;

    public interface AuthListener {
        void onUserChanged(User newUser);
    }

    private AuthController() {
        this.userDAO = new UserDAO();
        this.listeners = new ArrayList<>();
        // Default to guest / adopter session
        this.currentUser = new User(1, "admin", "Dr. Sarah Jenkins (Shelter Director)", "ADMIN");
    }

    public static synchronized AuthController getInstance() {
        if (instance == null) {
            instance = new AuthController();
        }
        return instance;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean login(String username, String password) {
        User user = userDAO.authenticate(username, password);
        if (user != null) {
            this.currentUser = user;
            notifyListeners();
            return true;
        }
        return false;
    }

    public void setSession(User user) {
        this.currentUser = user;
        notifyListeners();
    }

    public void addListener(AuthListener listener) {
        this.listeners.add(listener);
    }

    private void notifyListeners() {
        for (AuthListener l : listeners) {
            l.onUserChanged(currentUser);
        }
    }
}
