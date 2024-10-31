package ch.unil.doplab;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class UserProfile {
    private String userId;                // Unique identifier for the user
    private String username;              // Username of the user
    private String dietType;              // User's preferred diet (e.g., "vegetarian", "vegan")
    private Set<String> allergies;        // Set of ingredients the user is allergic to
    private Set<String> dislikedIngredients; // Set of ingredients the user dislikes
    private Optional<Integer> dailyCalorieTarget;     // User's daily calorie goal

    private static final Set<String> SUPPORTED_DIETS = Set.of("Vegetarian", "Vegan", "Paleo", "Ketogenic", "Gluten Free", "Lacto-Vegetarian", "Ovo-Vegetarian", "Pescetarian", "Primal", "Low FODMAP", "Whole30");
    // Constructor to initialize the user profile
    public UserProfile(String userId, String username) {
        this.userId = userId;
        this.username = username;
        this.allergies = new HashSet<>();
        this.dislikedIngredients = new HashSet<>();
        this.dailyCalorieTarget = Optional.empty();; // Default to 0 if not set
    }

    // Setters for dietary preferences
    public void setDietType(String dietType) {
        if (SUPPORTED_DIETS.contains(dietType)) {
            this.dietType = dietType;
        } else {
            throw new IllegalArgumentException("Unsupported diet type: " + dietType);
        }
    }

    public void setDailyCalorieTarget(int dailyCalorieTarget) {
        this.dailyCalorieTarget = Optional.of(dailyCalorieTarget);
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
    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getDietType() {
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

    // Optional method to display user preferences for debugging or user settings
    public void displayUserPreferences() {
        System.out.println("User: " + username + " (" + userId + ")");
        System.out.println("Diet Type: " + (dietType != null ? dietType : "None"));
        System.out.println("Daily Calorie Target: " + ((dailyCalorieTarget.get().intValue() > 0) ? dailyCalorieTarget : "Not set"));
        System.out.println("Allergies: " + String.join(", ", allergies));
        System.out.println("Disliked Ingredients: " + String.join(", ", dislikedIngredients));
    }
}