package ch.unil.doplab.recipe.ui;

import ch.unil.doplab.recipe.RecipEService;
import ch.unil.doplab.recipe.domain.UserProfile;
import jakarta.annotation.PostConstruct;
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
    RecipEService recipEService;

    @Inject
    MealPlanBean mealPlanBean;

    @Inject
    GroceryListBean groceryListBean;

    @PostConstruct
    public void init() {

    }

    public UserProfileBean() {
        super();
        this.currentUserProfile = new UserProfile();
    }

    /*
     * Utility Methods for Allergies and Disliked Ingredients
     */

    // Get allergies as a comma-separated string
    public String getAllergiesAsString() {
        return String.join(", ", getAllergies());
    }

    // Set allergies from a comma-separated string
    public void setAllergiesAsString(String allergies) {
        Set<String> allergiesSet = new HashSet<>(Arrays.asList(allergies.split("\\s*,\\s*")));
        setAllergies(allergiesSet);
    }

    // Get disliked ingredients as a comma-separated string
    public String getDislikedIngredientsAsString() {
        return String.join(", ", getDislikedIngredients());
    }

    // Set disliked ingredients from a comma-separated string
    public void setDislikedIngredientsAsString(String dislikedIngredients) {
        Set<String> dislikedSet = new HashSet<>(Arrays.asList(dislikedIngredients.split("\\s*,\\s*")));
        setDislikedIngredients(dislikedSet);
    }

    /*
     * Getters and Setters for Additional Properties
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

    // Getter for calorieLimit (unwrap dailyCalorieTarget)
    public int getCalorieLimit() {
        return getDailyCalorieTarget();
    }

    // Setter for calorieLimit (wrap value into dailyCalorieTarget)
    public void setCalorieLimit(Integer calorieLimit) {
        if (calorieLimit != null) {
            setDailyCalorieTarget(calorieLimit);
        } else {
            setDailyCalorieTarget(0);
        }
    }

    /*
     * Operations for Managing UserProfiles
     */

    // Load a specific user profile
    public void loadUserProfile(UUID userId) {
        try {
            currentUserProfile = recipEService.getUserProfile(userId.toString());
            this.replaceWithUser(currentUserProfile);
            changed = false;
        } catch (Exception e) {
            dialogMessage = "Error loading user profile: " + e.getMessage() + " user ID: " + userId.toString();
        }
    }

    // Save changes to the current user profile
    public void saveUserProfile() {
        try {
            if (this.getUserId() != null) {
                System.out.println("Saving user profile for ID: " + this.getUserId());
                recipEService.updateUserProfile(this.getUserId().toString(), this);
                dialogMessage = "User profile updated successfully.";
                changed = false;
                if (recipEService.getMealPlanByUserId(currentUserProfile.getUserId().toString()) == null) {
                    mealPlanBean.setMealPlan(recipEService.generateMealPlan(currentUserProfile.getUserId().toString()));
                }
                else {
                    mealPlanBean.setMealPlan(recipEService.regenerateMealPlan(currentUserProfile.getUserId().toString()));
                }
                if (recipEService.getGroceryListByUserId(currentUserProfile.getUserId().toString()) == null) {
                    groceryListBean.setGroceryList(recipEService.generateGroceryList(currentUserProfile.getUserId().toString()));
                }
                else {
                    groceryListBean.setGroceryList(recipEService.regenerateGroceryList(currentUserProfile.getUserId().toString()));
                }
            } else {
                dialogMessage = "Error: User ID is missing.";
            }
        } catch (Exception e) {
            dialogMessage = "Error saving user profile: " + e.getMessage() + " user ID: " + currentUserProfile.getUserId().toString();
        }
    }

    // Replace the fields of this bean with another UserProfile
    public void replaceWithUser(UserProfile newUserProfile) {
        this.setUserId(newUserProfile.getUserId());
        this.setUsername(newUserProfile.getUsername());
        this.setPassword(newUserProfile.getPassword());
        this.setDietType(newUserProfile.getDietType());
        this.setAllergies(newUserProfile.getAllergies());
        this.setDislikedIngredients(newUserProfile.getDislikedIngredients());
        this.setDailyCalorieTarget(newUserProfile.getDailyCalorieTarget());
        this.setMealPlanPreference(newUserProfile.getMealPlanPreference());
    }

    public List<String> getDietTypes() {
        List<String> dietTypes = new ArrayList<>();
        for (DietType type : DietType.values()) {
            dietTypes.add(type.toString());
        }
        dietTypes.add(null);
        return dietTypes;
    }

}