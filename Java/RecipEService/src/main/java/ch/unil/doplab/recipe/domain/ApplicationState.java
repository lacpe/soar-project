package ch.unil.doplab.recipe.domain;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.*;

@ApplicationScoped
public class ApplicationState {
    private Map<UUID, UserProfile> userProfiles;
    private Map<String, UUID> usernames;
    // We use both a userProfiles and usernames list so requests can be made using both the UUID and the username

    @PostConstruct
    public void init() {
        userProfiles = new TreeMap<>();
        usernames = new TreeMap<>();
        populateApplication();
    }

    // In order : getters, add operations, remove operations
    public UserProfile getUserProfile(UUID id) {return userProfiles.get(id);}

    public Map<UUID, UserProfile> getAllUserProfiles() {return userProfiles;}

    public UserProfile addUserProfile(UserProfile userProfile) {
        if (userProfile.getUserId() != null) {
            return addUserProfile(userProfile.getUserId(), userProfile);
        }
        return addUserProfile(UUID.randomUUID(), userProfile);
    }

    public UserProfile addUserProfile(UUID id, UserProfile userProfile) {
        if (userProfile.getUsername() == null | userProfile.getUsername().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty.");
        }
        if (userProfiles.containsKey(id)) {
            throw new IllegalArgumentException("User with ID " + id + " already exists.");
        }
        if (usernames.containsKey(userProfile.getUsername())) {
            throw new IllegalArgumentException("Username " + userProfile.getUsername() + " already in use.");
        }
        userProfiles.put(id, userProfile);
        usernames.put(userProfile.getUsername(), id);
        return userProfile;
    }

    public boolean setUserProfile(UUID id, UserProfile userProfile) {
        // This function changes the data to whatever userProfile is saved at UUID id to whatever data
        // is in userProfile
        UserProfile oldUserProfile = userProfiles.get(id);
        if (userProfile == null) {
            return false;
        }
        // Translating the following condition : if the old & new user profiles have the same (non-null) username
        // but their UUIDs are different (i.e. you are trying to set the same username for two different users
        // then throw an exception
        if (oldUserProfile.getUsername().equals(userProfile.getUsername())
                && oldUserProfile.getUsername() != null
                && !usernames.get(userProfile.getUsername()).equals(id)) {
            throw new IllegalArgumentException("A user with username " + userProfile.getUsername() + " already exists.");
        }
        oldUserProfile.replaceWithUser(userProfile);
        return true;
    }

    public void removeUserProfile(String username){
        removeUserProfile(usernames.get(username));
    }

    public void removeUserProfile(UUID id) {
        usernames.remove(userProfiles.get(id).getUsername());
        userProfiles.remove(id);
    }

    private void populateApplication() {
        UserProfile testuser = new UserProfile(UUID.randomUUID(), "test", "password123");
        addUserProfile(testuser);
    }

}
