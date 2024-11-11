package ch.unil.doplab.recipe.domain;

import java.util.List;


public class Main {
    public static void main(String[] args) {
        APIHandler apiHandler = new APIHandler();

        // Set up a test user profile with meal plan preference set directly
        UserProfile userProfile = new UserProfile("testuser", "password123");
        userProfile.setDietType(UserProfile.DietType.VEGETARIAN);
        userProfile.setDailyCalorieTarget(2000);
        userProfile.setMealPlanPreference(UserProfile.MealPlanPreference.WEEK); // Set preference to "daily" or "weekly"

        // Generate the meal plan based on the user’s preference
        MealPlan mealPlan = apiHandler.generateMealPlan(userProfile); // Initialize mealPlan by calling the APIHandler method

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
        if (userProfile.getMealPlanPreference().equals(UserProfile.MealPlanPreference.WEEK)) {
            // Generate the consolidated grocery list using APIHandler
            List<Meal> weeklyMeals = mealPlan.getMealsForWeek();
            List<Ingredient> consolidatedIngredients = apiHandler.generateConsolidatedShoppingList(weeklyMeals);

            // Create and set the grocery list
            GroceryList groceryList = new GroceryList();
            groceryList.setIngredients(consolidatedIngredients);

            // Display the consolidated grocery list
            System.out.println("\nGenerated Grocery List for the Week:");
            groceryList.displayGroceryList();
        }
    }
}