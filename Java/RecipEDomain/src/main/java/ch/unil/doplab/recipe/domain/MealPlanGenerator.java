package ch.unil.doplab.recipe.domain;

import java.util.*;

public class MealPlanGenerator {

    public static MealPlan createDummyMealPlan(UserProfile userProfile) {
        // Create an ordered map for days of the week
        Map<String, List<Meal>> orderedDailyMeals = new LinkedHashMap<>();

        // Define dummy meals for each day of the week
        orderedDailyMeals.put("Monday", List.of(
                createDummyMeal(1, "Pancakes", "https://example.com/pancakes.jpg", 250)
        ));
        orderedDailyMeals.put("Tuesday", List.of(
                createDummyMeal(2, "Veggie Stir Fry", "https://example.com/veggie-stir-fry.jpg", 350)
        ));
        orderedDailyMeals.put("Wednesday", List.of(
                createDummyMeal(3, "Spaghetti Bolognese", "https://example.com/spaghetti-bolognese.jpg", 600)
        ));
        orderedDailyMeals.put("Thursday", List.of(
                createDummyMeal(4, "Chicken Soup", "https://example.com/chicken-soup.jpg", 300)
        ));
        orderedDailyMeals.put("Friday", List.of(
                createDummyMeal(5, "Fish Tacos", "https://example.com/fish-tacos.jpg", 400)
        ));
        orderedDailyMeals.put("Saturday", List.of(
                createDummyMeal(6, "BBQ Chicken Pizza", "https://example.com/bbq-chicken-pizza.jpg", 700)
        ));
        orderedDailyMeals.put("Sunday", List.of(
                createDummyMeal(7, "Avocado Toast", "https://example.com/avocado-toast.jpg", 200)
        ));

        // Create and return the MealPlan with dummy data
        return new MealPlan(userProfile, orderedDailyMeals);
    }

    // Helper method to create a dummy Meal object
    static Meal createDummyMeal(int id, String title, String imageUrl, int calories) {
        NutritionalInfo nutritionalInfo = new NutritionalInfo(calories, 10, 20, 30); // dummy nutritional values

        // Create the meal with basic details
        Meal meal = new Meal(id, title, imageUrl, nutritionalInfo);

        // Add dummy ingredients to the meal
        meal.setIngredients(List.of(
                new Ingredient("Flour", 2, "cups"),
                new Ingredient("Milk", 1.5, "cups"),
                new Ingredient("Eggs", 2, "units")
        ));

        // Add dummy instructions to the meal
        meal.setInstructions(List.of(
                "Mix all ingredients together.",
                "Heat a pan over medium heat.",
                "Pour batter onto the pan and cook until golden brown."
        ));

        return meal;
    }
}

