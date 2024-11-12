package ch.unil.doplab.recipe.domain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class MealPlanGeneratorTest {

    private UserProfile userProfile;

    @BeforeEach
    public void setUp() {
        userProfile = new UserProfile("testUser", "testPassword", UserProfile.DietType.VEGETARIAN,
                new HashSet<>(), new HashSet<>(), 2000, UserProfile.MealPlanPreference.DAILY);
    }

    @Test
    public void testCreateDummyMealPlan() {
        // Act: Generate a dummy meal plan
        MealPlan mealPlan = MealPlanGenerator.createDummyMealPlan(userProfile);

        // Assert: Verify that the meal plan is not null and is linked to the correct user
        assertNotNull(mealPlan, "Generated meal plan should not be null");
        assertEquals(userProfile, mealPlan.getUserProfile(), "Meal plan should be linked to the provided user profile");

        // Assert: Verify that the meal plan contains all seven days of the week
        Map<String, List<Meal>> dailyMeals = mealPlan.getDailyMeals();
        assertEquals(7, dailyMeals.size(), "Meal plan should contain seven days");

        // Check that each day of the week has a meal entry
        assertTrue(dailyMeals.containsKey("Monday"), "Meal plan should contain meals for Monday");
        assertTrue(dailyMeals.containsKey("Tuesday"), "Meal plan should contain meals for Tuesday");
        assertTrue(dailyMeals.containsKey("Wednesday"), "Meal plan should contain meals for Wednesday");
        assertTrue(dailyMeals.containsKey("Thursday"), "Meal plan should contain meals for Thursday");
        assertTrue(dailyMeals.containsKey("Friday"), "Meal plan should contain meals for Friday");
        assertTrue(dailyMeals.containsKey("Saturday"), "Meal plan should contain meals for Saturday");
        assertTrue(dailyMeals.containsKey("Sunday"), "Meal plan should contain meals for Sunday");
    }

    @Test
    public void testCreateDummyMeal() {
        // Act: Create a dummy meal
        Meal meal = MealPlanGenerator.createDummyMeal(1, "Pancakes", "https://example.com/pancakes.jpg", 250);

        // Assert: Verify that the meal properties are set correctly
        assertNotNull(meal, "Dummy meal should not be null");
        assertEquals(1, meal.getId(), "Meal ID should match the provided ID");
        assertEquals("Pancakes", meal.getTitle(), "Meal title should match the provided title");
        assertEquals("https://example.com/pancakes.jpg", meal.getImageUrl(), "Meal image URL should match the provided URL");

        // Verify nutritional information
        NutritionalInfo nutritionalInfo = meal.getNutritionalInfo();
        assertNotNull(nutritionalInfo, "Nutritional info should not be null");
        assertEquals(250, nutritionalInfo.getCalories(), "Calories should match the provided value");

        // Assert: Check that the meal has the correct ingredients
        List<Ingredient> ingredients = meal.getIngredients();
        assertNotNull(ingredients, "Ingredients should not be null");
        assertEquals(3, ingredients.size(), "There should be three ingredients");
        assertEquals("Flour", ingredients.get(0).getName(), "First ingredient should be Flour");
        assertEquals("Milk", ingredients.get(1).getName(), "Second ingredient should be Milk");
        assertEquals("Eggs", ingredients.get(2).getName(), "Third ingredient should be Eggs");

        // Assert: Check that the meal has instructions
        List<String> instructions = meal.getInstructions();
        assertNotNull(instructions, "Instructions should not be null");
        assertEquals(3, instructions.size(), "There should be three instructions");
        assertEquals("Mix all ingredients together.", instructions.get(0), "First instruction should match");
        assertEquals("Heat a pan over medium heat.", instructions.get(1), "Second instruction should match");
        assertEquals("Pour batter onto the pan and cook until golden brown.", instructions.get(2), "Third instruction should match");
    }

    @Test
    public void testCreateDummyMealPlanHasCorrectMeals() {
        // Act: Generate a dummy meal plan and retrieve meals for specific days
        MealPlan mealPlan = MealPlanGenerator.createDummyMealPlan(userProfile);
        Map<String, List<Meal>> dailyMeals = mealPlan.getDailyMeals();

        // Assert: Verify that each day has exactly one meal
        for (String day : dailyMeals.keySet()) {
            List<Meal> meals = dailyMeals.get(day);
            assertNotNull(meals, day + " should have a meal list");
            assertEquals(1, meals.size(), day + " should contain exactly one meal");
        }

        // Assert: Check specific properties of meals on a few days
        Meal mondayMeal = dailyMeals.get("Monday").get(0);
        assertEquals("Pancakes", mondayMeal.getTitle(), "Monday's meal title should be 'Pancakes'");
        assertEquals(250, mondayMeal.getNutritionalInfo().getCalories(), "Monday's meal should have 250 calories");

        Meal tuesdayMeal = dailyMeals.get("Tuesday").get(0);
        assertEquals("Veggie Stir Fry", tuesdayMeal.getTitle(), "Tuesday's meal title should be 'Veggie Stir Fry'");
        assertEquals(350, tuesdayMeal.getNutritionalInfo().getCalories(), "Tuesday's meal should have 350 calories");
    }
}
