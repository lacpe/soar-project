package ch.unil.doplab.recipe.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        APIHandler apiHandler = new APIHandler();
        UUID uuid = UUID.randomUUID();

        // Set up a test user profile with meal plan preference set directly
        UserProfile userProfile = new UserProfile(uuid, "testUser", "testPassword", UserProfile.DietType.VEGETARIAN,
                new HashSet<>(), new HashSet<>(), 2000, UserProfile.MealPlanPreference.DAY);
        userProfile.setDietType(UserProfile.DietType.VEGETARIAN);
        userProfile.setDailyCalorieTarget(2000);
        userProfile.setMealPlanPreference(UserProfile.MealPlanPreference.DAY);
        userProfile.setDesiredServings(100); // Set desired servings

        // Generate the meal plan based on the user’s preference
        MealPlan mealPlan = apiHandler.generateMealPlan(userProfile);

        // Display the meal plan
        mealPlan.displayMealPlan();

        // Display the pre-generated grocery list for the meal plan
        GroceryList groceryList = apiHandler.generateConsolidatedShoppingList(mealPlan.getAllMeals());
        groceryList.displayGroceryList();
    }
}