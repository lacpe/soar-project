package ch.unil.doplab.recipe.rest;

import ch.unil.doplab.recipe.domain.UserProfile;
import ch.unil.doplab.recipe.domain.Utils;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.HashMap;
import java.util.Map;

@ApplicationScoped // Makes this class a CDI-managed bean
public class RecipEService {

    private final Map<String, UserProfile> userDatabase = new HashMap<>();

    /**
     * Register a new user in the system.
     *
     * @param user the UserProfile object to register
     * @return true if registration is successful, false if the email already exists
     */
    public boolean registerUser(UserProfile user) {
        if (userDatabase.containsKey(user.getUsername())) {
            return false; // User with this email already exists
        }

        userDatabase.put(user.getUsername(), user);
        return true; // Registration successful
    }

    /**
     * Authenticate a user with the given email and password.
     *
     * @param email    the user's email (used as the username)
     * @param password the user's plain-text password
     * @return the authenticated UserProfile, or null if authentication fails
     */
    public UserProfile authenticateUser(String email, String password) {
        UserProfile user = userDatabase.get(email);
        if (user != null && Utils.checkPassword(password, user.getPassword())) {
            return user; // Authentication successful
        }
        return null; // Authentication failed
    }
}

