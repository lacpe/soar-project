package ch.unil.doplab;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MealPlan {
    private String planType; // e.g., "day" or "week"
    private Map<String, List<Meal>> dailyMeals; // Key: Day, Value: List of Meals (breakfast, lunch, dinner)
    private UserProfile userProfile; // reference to the user's profile for personalized adjustments
    private UUID userId;
    private UUID mealPlanId;
    private int calorieTarget;

    // Constructor
    public MealPlan(String planType, UserProfile userProfile, Map<String, List<Meal>> dailyMeals) {
        this.planType = planType;
        this.userProfile = userProfile;
        this.userId = userProfile.getUserId();
        this.mealPlanId = UUID.randomUUID();
        this.dailyMeals = dailyMeals != null ? dailyMeals : new HashMap<>();
    }

    public MealPlan(String planType, UserProfile userProfile) {
        this(planType, userProfile, new HashMap<>());
    }

    /**
     * Generates a consolidated grocery list from all ingredients in each meal in the meal plan.
     * @return GroceryList containing all ingredients needed for the meal plan
     */
    public GroceryList generateGroceryList() {
        GroceryList groceryList = new GroceryList();

        // Iterate through each day and each meal
        for (List<Meal> meals : dailyMeals.values()) {
            for (Meal meal : meals) {
                // Get ingredients of the meal and add each to the grocery list
                for (Ingredient ingredient : meal.getIngredients()) {
                    groceryList.addIngredient(ingredient);
                }
            }
        }

        return groceryList;
    }

    // Getters and setters for each attribute
    public String getPlanType() {
        return planType;
    }

    public void setPlanType(String planType) {
        this.planType = planType;
    }

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

    public UUID getUserId() {return userId}
}