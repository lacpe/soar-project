package ch.unil.doplab.recipe.domain;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import ch.unil.doplab.recipe.domain.Utils;

public class UserProfile {
    private UUID userId;                             // Unique identifier for the user
    private String username;                         // Username of the user
    private String password;                         // Password, hashed as soon as it is created
    private DietType dietType;                       // User's preferred diet as an enum
    private Set<String> allergies = new HashSet<>(); // Set of ingredients the user is allergic to, initialized
    private Set<String> dislikedIngredients = new HashSet<>(); // Ingredients the user dislikes, initialized
    private Optional<Integer> dailyCalorieTarget = Optional.empty(); // Daily calorie goal, initialized
    private MealPlanPreference mealPlanPreference = MealPlanPreference.DAILY; // Default to DAILY

    public enum MealPlanPreference {
        DAILY,
        WEEK
    }

    // Enum for supported diets
    public enum DietType {
        VEGETARIAN,
        VEGAN,
        PALEO,
        KETOGENIC,
        GLUTEN_FREE,
        LACTO_VEGETARIAN,
        OVO_VEGETARIAN,
        PESCETARIAN,
        PRIMAL,
        LOW_FODMAP,
        WHOLE30
    }

    // Constructor to initialize the user profile (case where there is no UUID)
    public UserProfile(String username, String password) {
        this.userId = UUID.randomUUID();
        this.username = username;
        this.password = Utils.hashPassword(password);
    }

    // Another constructor to handle the case where there IS a UUID provided
    public UserProfile(UUID userId, String username, String password) {
        this.userId = userId;
        this.username = username;
        this.password = Utils.hashPassword(password);
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setPassword(String password) {
        this.password = Utils.hashPassword(password);
    }

    // Setters for dietary preferences
    public void setDietType(DietType dietType) {
        this.dietType = dietType;
    }

    public void setAllergies(Set<String> allergies) {
        this.allergies = allergies;
    }
    public void setDislikedIngredients(Set<String> dislikedIngredients) {
        this.dislikedIngredients = dislikedIngredients;
    }

    public void setDailyCalorieTarget(int dailyCalorieTarget) {
        this.dailyCalorieTarget = Optional.of(dailyCalorieTarget);
    }

    public void setMealPlanPreference(MealPlanPreference mealPlanPreference) {
        this.mealPlanPreference = mealPlanPreference;
    }

    // Methods to add allergies and disliked ingredients
    public void addAllergy(String allergy) {
        this.allergies.add(allergy);
    }

    public void removeAllergy(String allergy) {
        this.allergies.remove(allergy);
    }

    public void addDislikedIngredient(String ingredient) {
        this.dislikedIngredients.add(ingredient);
    }

    public void removeDislikedIngredient(String ingredient) {
        this.dislikedIngredients.remove(ingredient);
    }

    // Getters for user preferences and restrictions
    public UUID getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public DietType getDietType() {
        return dietType;
    }

    public Set<String> getAllergies() {
        return allergies;
    }

    public Set<String> getDislikedIngredients() {
        return dislikedIngredients;
    }

    public Optional<Integer> getDailyCalorieTarget() {
        return dailyCalorieTarget;
    }

    public MealPlanPreference getMealPlanPreference() {
        return mealPlanPreference;
    }

    // Optional method to display user preferences for debugging or user settings
    public void displayUserPreferences() {
        System.out.println("User: " + username + " (" + userId + ")");
        System.out.println("Diet Type: " + (dietType != null ? dietType : "None"));
        System.out.println("Daily Calorie Target: " + (dailyCalorieTarget.isPresent() ? dailyCalorieTarget.get() : "Not set"));
        System.out.println("Allergies: " + String.join(", ", allergies));
        System.out.println("Disliked Ingredients: " + String.join(", ", dislikedIngredients));
        System.out.println("Meal Plan Preference: " + mealPlanPreference);
    }

    public void replaceWithUser(UserProfile newUserProfile) {
        this.username = newUserProfile.getUsername();
        this.password = newUserProfile.getPassword();
        this.allergies = newUserProfile.getAllergies();
        this.dislikedIngredients = newUserProfile.getDislikedIngredients();
        this.dailyCalorieTarget = newUserProfile.getDailyCalorieTarget();
        this.mealPlanPreference = newUserProfile.getMealPlanPreference();
    }
}
