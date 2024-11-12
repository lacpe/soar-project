package ch.unil.doplab.recipe.domain;

import java.util.HashSet;
import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        boolean apiPOINTS = false;
        APIHandler apiHandler = new APIHandler();

        // Set up a test user profile with meal plan preference set directly
        UserProfile userProfile = new UserProfile("testuser", "password123",
                UserProfile.DietType.VEGETARIAN, new HashSet<>(), new HashSet<>(), 2000,
                UserProfile.MealPlanPreference.WEEK);
        userProfile.setDietType(UserProfile.DietType.VEGETARIAN);
        userProfile.setDailyCalorieTarget(2000);
        userProfile.setMealPlanPreference(UserProfile.MealPlanPreference.WEEK); // Set preference to "daily" or "weekly"

        // Generate the meal plan based on the user’s preference
        // no more points left from APO
        MealPlan mealPlan;
        if (apiPOINTS) {
            mealPlan = apiHandler.generateMealPlan(userProfile);
        } else {
            mealPlan = MealPlanGenerator.createDummyMealPlan(userProfile);
        }

        System.out.println("\nGenerated Meal Plan:");

        // Display meals day by day
        for (String day : mealPlan.getDailyMeals().keySet()) {
            System.out.println("Day: " + day);
            for (Meal meal : mealPlan.getDailyMeals().get(day)) {
                meal.displayMealInfo();         // Show basic meal info (title, calories, etc.)
                meal.displayInstructions();      // Show instructions
                meal.displayMealIngredients();   // Show ingredients
            }
            System.out.println();
        }

        // If the meal plan preference is weekly, generate and display a consolidated grocery list
        if (userProfile.getMealPlanPreference().equals("week")) {
            GroceryList groceryList = mealPlan.generateGroceryList();
            System.out.println("\nGenerated Grocery List for the Week:");
            groceryList.displayGroceryList();
        }
    }
}
