package ch.unil.doplab.recipe.domain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class MealPlanTest {

    private UserProfile userProfile;
    private MealPlan mealPlan;

    @BeforeEach
    public void setUp() {
        userProfile = new UserProfile("testUser", "testPassword", UserProfile.DietType.VEGETARIAN,
                new HashSet<>(), new HashSet<>(), 2000, UserProfile.MealPlanPreference.DAILY);
        mealPlan = new MealPlan(userProfile);
    }

    @Test
    public void testMealPlanInitialization() {
        assertNotNull(mealPlan.getMealPlanId(), "MealPlan ID should not be null");
        assertEquals(userProfile, mealPlan.getUserProfile(), "MealPlan should be associated with the provided user profile");
        assertNotNull(mealPlan.getDailyMeals(), "Daily meals should be initialized and not null");
        assertTrue(mealPlan.getDailyMeals().isEmpty(), "Daily meals should be empty upon initialization");
    }

    @Test
    public void testSetAndGetCalorieTarget() {
        mealPlan.setCalorieTarget(2000);
        assertEquals(2000, mealPlan.getCalorieTarget(), "Calorie target should be set to 2000");
    }

    @Test
    public void testSetAndGetDailyMeals() {
        // Create dummy meals and add them to daily meals
        Meal breakfast = new Meal(1, "Pancakes", "https://example.com/pancakes.jpg", null);
        Meal lunch = new Meal(2, "Salad", "https://example.com/salad.jpg", null);
        Map<String, List<Meal>> dailyMeals = new LinkedHashMap<>();
        dailyMeals.put("Monday", List.of(breakfast, lunch));

        mealPlan.setDailyMeals(dailyMeals);

        Map<String, List<Meal>> retrievedMeals = mealPlan.getDailyMeals();
        assertEquals(1, retrievedMeals.size(), "Daily meals should contain one day of meals");
        assertTrue(retrievedMeals.containsKey("Monday"), "Daily meals should contain meals for Monday");
        assertEquals(2, retrievedMeals.get("Monday").size(), "Monday should contain two meals");
    }

    @Test
    public void testGenerateGroceryList() {
        // Create ingredients for meals
        Ingredient flour = new Ingredient("Flour", 2.0, "cups");
        Ingredient milk = new Ingredient("Milk", 1.5, "cups");
        Ingredient eggs = new Ingredient("Eggs", 2, "units");

        // Create meals with ingredients
        Meal pancakes = new Meal(1, "Pancakes", "https://example.com/pancakes.jpg", null);
        pancakes.setIngredients(List.of(flour, milk, eggs));

        Meal omelette = new Meal(2, "Omelette", "https://example.com/omelette.jpg", null);
        omelette.setIngredients(List.of(eggs, milk));

        // Add meals to daily meals for the meal plan
        Map<String, List<Meal>> dailyMeals = new LinkedHashMap<>();
        dailyMeals.put("Monday", List.of(pancakes, omelette));
        mealPlan.setDailyMeals(dailyMeals);

        // Generate and verify the grocery list
        GroceryList groceryList = mealPlan.generateGroceryList();
        Map<String, Ingredient> ingredients = groceryList.getIngredients();
        assertEquals(3, ingredients.size(), "Grocery list should contain three unique ingredients");

        // Verify consolidated quantities
        assertTrue(ingredients.containsKey("Flour_cups"), "Grocery list should contain Flour with unit cups");
        assertEquals(2.0, ingredients.get("Flour_cups").getQuantity(), "Flour quantity should be 2.0 cups");

        assertTrue(ingredients.containsKey("Milk_cups"), "Grocery list should contain Milk with unit cups");
        assertEquals(3.0, ingredients.get("Milk_cups").getQuantity(), "Milk quantity should be consolidated to 3.0 cups");

        assertTrue(ingredients.containsKey("Eggs_units"), "Grocery list should contain Eggs with unit units");
        assertEquals(4, ingredients.get("Eggs_units").getQuantity(), "Eggs quantity should be consolidated to 4 units");
    }

    @Test
    public void testUserProfileLink() {
        UUID userId = userProfile.getUserId();
        assertEquals(userId, mealPlan.getUserId(), "MealPlan's userId should match the user profile's userId");
    }
}
