package ch.unil.doplab.recipe.ui;

import ch.unil.doplab.recipe.domain.UserProfile;
import ch.unil.doplab.recipe.rest.RecipEService;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.*;

@SessionScoped
@Named
public class UserProfileBean extends UserProfile implements Serializable {
    private static final long serialVersionUID = 1L;

    private UserProfile currentUserProfile;
    private boolean changed;
    private String dialogMessage;

    @Inject
    private RecipEService recipEService;

    public UserProfileBean() {
        super(); // Call UserProfile constructor
        this.currentUserProfile = new UserProfile();
    }

    /*
     * Getters and Setters for additional properties
     */

    public UserProfile getCurrentUserProfile() {
        return currentUserProfile;
    }

    public void setCurrentUserProfile(UserProfile currentUserProfile) {
        this.currentUserProfile = currentUserProfile;
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public String getDialogMessage() {
        return dialogMessage;
    }

    public void setDialogMessage(String dialogMessage) {
        this.dialogMessage = dialogMessage;
    }

    /*
     * Operations for managing UserProfiles


    // Load all user profiles
    public List<UserProfile> getAllUserProfiles() {
        try {
            Map<UUID, UserProfile> profiles = recipEService.getAllUserProfiles();
            return new ArrayList<>(profiles.values());
        } catch (Exception e) {
            dialogMessage = "Error loading user profiles: " + e.getMessage();
            return Collections.emptyList();
        }
    }

    // Load a specific user profile
    public void loadUserProfile(UUID userId) {
        try {
            currentUserProfile = recipEService.getUserProfile(userId.toString());
            this.replaceWith(currentUserProfile);
            changed = false;
        } catch (Exception e) {
            dialogMessage = "Error loading user profile: " + e.getMessage();
        }
    }

    // Save changes to the current user profile
    public void saveUserProfile() {
        try {
            if (this.getUserId() != null) {
                recipEService.updateUserProfile(this.getUserId().toString(), this);
                dialogMessage = "User profile updated successfully.";
                changed = false;
            } else {
                dialogMessage = "Error: User ID is missing.";
            }
        } catch (Exception e) {
            dialogMessage = "Error saving user profile: " + e.getMessage();
        }
    }

    // Create a new user profile
    public void createUserProfile() {
        try {
            UserProfile createdProfile = recipEService.createUserProfile(this);
            if (createdProfile != null) {
                this.replaceWith(createdProfile);
                dialogMessage = "User profile created successfully.";
                changed = false;
            }
        } catch (Exception e) {
            dialogMessage = "Error creating user profile: " + e.getMessage();
        }
    }

    // Delete the current user profile
    public void deleteUserProfile() {
        try {
            if (this.getUserId() != null) {
                recipEService.deleteUserProfile(this.getUserId().toString());
                dialogMessage = "User profile deleted successfully.";
                this.replaceWith(new UserProfile());
                changed = false;
            } else {
                dialogMessage = "Error: User ID is missing.";
            }
        } catch (Exception e) {
            dialogMessage = "Error deleting user profile: " + e.getMessage();
        }
    }

    // Undo changes to the current user profile
    public void undoChanges() {
        this.replaceWith(currentUserProfile);
        changed = false;
    }

    // Check if any changes have been made
    public void checkIfChanged() {
        changed = !currentUserProfile.equals(this);
    }

    /*
     * Utility Methods


    // Replace the fields of the current bean with another UserProfile
    public void replaceWith(UserProfile userProfile) {
        this.setUserId(userProfile.getUserId());
        this.setUsername(userProfile.getUsername());
        this.setDietType(userProfile.getDietType());
        this.setCalorieLimit(userProfile.getCalorieLimit());
        this.setMealPlanPreferences(userProfile.getMealPlanPreferences());
    }
    */
}