package ch.unil.doplab.recipe.domain;

import java.util.*;

public class MealPlan {
    private UUID mealPlanId;
    private Map<String, List<Meal>> dailyMeals; // Key: Day, Value: List of Meals (breakfast, lunch, dinner)
    private int calorieTarget;
    private UserProfile userProfile; // Reference to the user's profile for desired servings and other preferences

    // Updated constructor to include UserProfile
    public MealPlan(UserProfile userProfile, Map<String, List<Meal>> dailyMeals) {
        this.userProfile = userProfile;
        this.mealPlanId = UUID.randomUUID();
        this.dailyMeals = dailyMeals != null ? dailyMeals : new LinkedHashMap<>();
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
                // Check if ingredients are available for the meal
                if (meal.getIngredients() != null) {
                    // Get ingredients of the meal and add each to the grocery list
                    for (Ingredient ingredient : meal.getIngredients()) {
                        groceryList.addIngredient("General", ingredient);
                    }
                } else {
                    System.out.println("No ingredients available for meal: " + meal.getTitle());
                }
            }
        }
        return groceryList;
    }

    /**
     * Displays the meal plan with desired servings information.
     */
    public void displayMealPlan() {
        int desiredServings = userProfile.getDesiredServings(); // Access desiredServings from UserProfile instance
        String servingsText = desiredServings == 1 ? "serving" : "servings";
        System.out.println("\nGenerated Meal Plan: " + desiredServings + " " + servingsText);

        for (String day : dailyMeals.keySet()) {
            System.out.println("Day: " + day);
            for (Meal meal : dailyMeals.get(day)) {
                meal.displayMealInfo();
                meal.displayInstructions();
                meal.displayMealIngredients();
            }
            System.out.println();
        }
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

    public void setMealPlanId(UUID mealPlanId) {
        this.mealPlanId = mealPlanId;
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }
}
