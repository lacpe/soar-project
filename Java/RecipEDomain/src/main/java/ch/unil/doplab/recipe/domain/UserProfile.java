package ch.unil.doplab.recipe.domain;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import ch.unil.doplab.recipe.domain.Utils;
import jakarta.persistence.*;

import javax.swing.text.html.Option;

@Entity
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "UUID", updatable = false, nullable = false)
    private UUID userId;                             // Unique identifier for the user
    private String username;                         // Username of the user
    private String password;                         // Password, hashed as soon as it is created
    private DietType dietType;                       // User's preferred diet as an enum
    @ElementCollection
    private Set<String> allergies = new HashSet<>(); // Set of ingredients the user is allergic to, initialized
    @ElementCollection
    private Set<String> dislikedIngredients = new HashSet<>(); // Ingredients the user dislikes, initialized
    private int dailyCalorieTarget = 0; // Daily calorie goal, initialized
    private MealPlanPreference mealPlanPreference = MealPlanPreference.DAY; // Default to DAY
    private int desiredServings = 1;


    public enum MealPlanPreference {
        DAY,
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

    public UserProfile() {
        this(null, null, null, null, new HashSet<>(), new HashSet<>(), 0, null);
    }

    public UserProfile(String username, String password, DietType dietType, Set<String> allergies,
                       Set<String> dislikedIngredients, int dailyCalorieTarget, MealPlanPreference mealPlanPreference) {
        this(null, username, password, dietType, allergies, dislikedIngredients, dailyCalorieTarget, mealPlanPreference);
    }

    public UserProfile(UUID userId, String username, String password, DietType dietType, Set<String> allergies,
                       Set<String> dislikedIngredients, int dailyCalorieTarget, MealPlanPreference mealPlanPreference) {
        this.userId = userId;
        this.username = username;
        this.password = Utils.hashPassword(password);
        this.dietType = dietType;
        this.allergies = allergies;
        this.dislikedIngredients = dislikedIngredients;
        this.dailyCalorieTarget = 0;
        this.mealPlanPreference = mealPlanPreference;
    }

    // Getters and setters
    public int getDesiredServings() {
        return desiredServings;
    }

    public void setDesiredServings(int desiredServings) {
        this.desiredServings = desiredServings;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public DietType getDietType() {
        return dietType;
    }

    public void setDietType(DietType dietType) {
        this.dietType = dietType;
    }

    public Set<String> getAllergies() {
        return allergies;
    }

    public void setAllergies(Set<String> allergies) {
        this.allergies = allergies;
    }

    public Set<String> getDislikedIngredients() {
        return dislikedIngredients;
    }

    public void setDislikedIngredients(Set<String> dislikedIngredients) {
        this.dislikedIngredients = dislikedIngredients;
    }

    public int getDailyCalorieTarget() {
        return dailyCalorieTarget;
    }

    public void setDailyCalorieTarget(int dailyCalorieTarget) {
        this.dailyCalorieTarget = dailyCalorieTarget;
    }

    public MealPlanPreference getMealPlanPreference() {
        return mealPlanPreference;
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



    // Optional method to display user preferences for debugging or user settings
    public void displayUserPreferences() {
        System.out.println("User: " + username + " (" + userId + ")");
        System.out.println("Diet Type: " + (dietType != null ? dietType : "None"));
        System.out.println("Daily Calorie Target: " + (dailyCalorieTarget !=0 ? dailyCalorieTarget : "Not set"));
        System.out.println("Allergies: " + String.join(", ", allergies));
        System.out.println("Disliked Ingredients: " + String.join(", ", dislikedIngredients));
        System.out.println("Meal Plan Preference: " + mealPlanPreference);
    }

    public void replaceWithUser(UserProfile newUserProfile) {
        this.username = newUserProfile.getUsername();
        this.password = newUserProfile.getPassword();
        this.dietType = newUserProfile.getDietType();
        this.allergies = newUserProfile.getAllergies();
        this.dislikedIngredients = newUserProfile.getDislikedIngredients();
        this.dailyCalorieTarget = newUserProfile.getDailyCalorieTarget();
        this.mealPlanPreference = newUserProfile.getMealPlanPreference();
    }
}
