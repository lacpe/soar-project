package ch.unil.doplab.recipe.domain;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import ch.unil.doplab.recipe.domain.Utils;

public class UserProfile {
    private UUID userId;                            // Unique identifier for the user
    private String username;                        // Username of the user
    private String password;                        // Password, hashed as soon as it is created
    private String dietType;                        // User's preferred diet (e.g., "vegetarian", "vegan")
    private Set<String> allergies;                  // Set of ingredients the user is allergic to
    private Set<String> dislikedIngredients;        // Set of ingredients the user dislikes
    private Optional<Integer> dailyCalorieTarget;   // User's daily calorie goal
    private static final Set<String> SUPPORTED_DIETS = Set.of("Vegetarian", "Vegan", "Paleo", "Ketogenic", "Gluten Free", "Lacto-Vegetarian", "Ovo-Vegetarian", "Pescetarian", "Primal", "Low FODMAP", "Whole30");
    // Constructor to initialize the user profile (case where there is no UUID)
    public UserProfile(String username, String password) {
        this.userId = UUID.randomUUID();
        this.username = username;
        this.password = Utils.hashPassword(password);
        this.allergies = new HashSet<>();
        this.dislikedIngredients = new HashSet<>();
        this.dailyCalorieTarget = Optional.empty();
    }
    // Another constructor to handle the case where there IS a UUID provided
    public UserProfile(UUID userId, String username, String password) {
        this.userId = userId;
        this.username = username;
        this.password = Utils.hashPassword(password);
        this.allergies = new HashSet<>();
        this.dislikedIngredients = new HashSet<>();
        this.dailyCalorieTarget = Optional.empty();
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
    public UUID getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {return password;}

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

    public void replaceWithUser(UserProfile newUserProfile) {
        this.username = newUserProfile.getUsername();
        this.password = newUserProfile.getPassword();
        this.allergies = newUserProfile.getAllergies();
        this.dislikedIngredients = newUserProfile.getDislikedIngredients();
        this.dailyCalorieTarget = newUserProfile.getDailyCalorieTarget();
    }
}