package ch.unil.doplab.recipe.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        APIHandler apiHandler = new APIHandler();

        // Set up a test user profile with meal plan preference set directly
        UserProfile userProfile = new UserProfile(null, "testUser", "testPassword", UserProfile.DietType.VEGETARIAN,
                new HashSet<>(), new HashSet<>(), 2000, UserProfile.MealPlanPreference.DAILY);
        userProfile.setDietType(UserProfile.DietType.VEGETARIAN);
        userProfile.setDailyCalorieTarget(2000);
        userProfile.setMealPlanPreference(UserProfile.MealPlanPreference.WEEK);

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

        // Generate and display a consolidated grocery list if preference is weekly
        if (userProfile.getMealPlanPreference() == UserProfile.MealPlanPreference.WEEK) {
            // Collect all meals in a single list for the week
            List<Meal> allMeals = new ArrayList<>();
            for (List<Meal> meals : mealPlan.getDailyMeals().values()) {
                allMeals.addAll(meals);
            }

            // Generate and display the consolidated grocery list organized by aisle
            Map<String, List<Ingredient>> groceryListByAisle = apiHandler.generateConsolidatedShoppingList(allMeals);
            System.out.println("\nGenerated Grocery List for the Week (Organized by Aisle):");

            // Display each aisle with its ingredients
            for (String aisle : groceryListByAisle.keySet()) {
                System.out.println("Aisle: " + aisle);
                for (Ingredient ingredient : groceryListByAisle.get(aisle)) {
                    System.out.println(" - " + ingredient.getQuantity() + " " + ingredient.getUnit() + " " + ingredient.getName());
                }
                System.out.println(); // Blank line between aisles
            }
        }
    }
}