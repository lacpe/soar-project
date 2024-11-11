package ch.unil.doplab.recipe.domain;

import java.util.*;

public class MealPlan {
    private Map<String, List<Meal>> dailyMeals; // Key: Day, Value: List of Meals (breakfast, lunch, dinner)
    private UserProfile userProfile; // reference to the user's profile for personalized adjustments
    private UUID userId;
    private UUID mealPlanId;
    private int calorieTarget;

    // Constructor
    public MealPlan(UserProfile userProfile, Map<String, List<Meal>> dailyMeals) {
        this.userProfile = userProfile;
        this.userId = userProfile.getUserId();
        this.mealPlanId = UUID.randomUUID();
        this.dailyMeals = dailyMeals != null ? dailyMeals : new LinkedHashMap<>();
    }

    public MealPlan(UserProfile userProfile) {
        this(userProfile, new LinkedHashMap<>());
    }

    /**
     * Generates a consolidated grocery list from all ingredients in each meal in the meal plan.
     * @return GroceryList containing all ingredients needed for the meal plan
     */
    public List<Meal> getMealsForWeek() {
        List<Meal> allMeals = new ArrayList<>();
        for (List<Meal> dailyMeals : dailyMeals.values()) {
            allMeals.addAll(dailyMeals);
        }
        return allMeals;
    }

    // Getters and setters for each attribute

    public int getCalorieTarget() {
        return calorieTarget;
    }

    public void setCalorieTarget(int calorieTarget) {
        this.calorieTarget = calorieTarget;
    }

    public Map<String, List<Meal>> getDailyMeals() {
        return dailyMeals;
    }

    public void setDailyMeals(Map<String, List<Meal>> dailyMeals) {
        this.dailyMeals = dailyMeals;
    }

    public UUID getMealPlanId() {
        return mealPlanId;
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public UUID getUserId() {
        return userId;
    }
}
