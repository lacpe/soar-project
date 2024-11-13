package ch.unil.doplab.recipe.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        APIHandler apiHandler = new APIHandler();

        // Set up a test user profile with meal plan preference set directly
        UserProfile userProfile = new UserProfile(null, "testUser", "testPassword", UserProfile.DietType.VEGETARIAN,
                new HashSet<>(), new HashSet<>(), 2000, UserProfile.MealPlanPreference.WEEK);
        userProfile.setDietType(UserProfile.DietType.VEGETARIAN);
        userProfile.setDailyCalorieTarget(2000);
        userProfile.setMealPlanPreference(UserProfile.MealPlanPreference.WEEK);
        userProfile.setDesiredServings(100); // Set desired servings

        // Generate the meal plan based on the user’s preference
        MealPlan mealPlan = apiHandler.generateMealPlan(userProfile);

        // Display the meal plan
        mealPlan.displayMealPlan();

        // Generate and display a consolidated grocery list if preference is weekly
        if (userProfile.getMealPlanPreference() == UserProfile.MealPlanPreference.WEEK) {
            // Collect all meals in a single list for the week
            List<Meal> allMeals = new ArrayList<>();
            for (List<Meal> meals : mealPlan.getDailyMeals().values()) {
                allMeals.addAll(meals);
            }

            // Generate and display the grocery list organized by aisle
            GroceryList groceryList = apiHandler.generateConsolidatedShoppingList(allMeals);
            groceryList.displayGroceryList();
        }
    }
}