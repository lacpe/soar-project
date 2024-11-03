import ch.unil.doplab.recipe.domain.*;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        // Create UserProfile
        UserProfile userProfile = new UserProfile("testuser", "password123");
        userProfile.setDietType("Vegetarian");
        userProfile.setDailyCalorieTarget(2000);
        userProfile.addAllergy("peanuts");
        userProfile.addDislikedIngredient("broccoli");

        // Display user preferences
        System.out.println("Testing UserProfile Display:");
        userProfile.displayUserPreferences();
        
        // Generate Meal Plan
        APIHandler apiHandler = new APIHandler();
        MealPlan mealPlan = apiHandler.generateMealPlan("daily", userProfile);

        // Display Meal Plan
        System.out.println("\nGenerated Meal Plan:");
        for (String day : mealPlan.getDailyMeals().keySet()) {
            System.out.println("Day: " + day);
            for (Meal meal : mealPlan.getDailyMeals().get(day)) {
                meal.displayMealInfo();
                meal.displayInstructions();
            }
        }

        // Generate Grocery List
        GroceryList groceryList = mealPlan.generateGroceryList();
        System.out.println("\nGenerated Grocery List:");
        groceryList.displayGroceryList();
    }
}
