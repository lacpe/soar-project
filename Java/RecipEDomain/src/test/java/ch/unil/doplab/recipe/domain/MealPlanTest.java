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
        userProfile = new UserProfile(null, "testUser", "testPassword", UserProfile.DietType.VEGETARIAN,
                new HashSet<>(), new HashSet<>(), 2000, UserProfile.MealPlanPreference.DAY);;
        mealPlan = new MealPlan();
    }

    @Test
    public void testMealPlanInitialization() {
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
        Map<String, DailyMealSet> dailyMeals = new LinkedHashMap<>();
        dailyMeals.put("Monday", new DailyMealSet(List.of(breakfast, lunch)));

        mealPlan.setDailyMeals(dailyMeals);

        Map<String, DailyMealSet> retrievedMeals = mealPlan.getDailyMeals();
        assertEquals(1, retrievedMeals.size(), "Daily meals should contain one day of meals");
        assertTrue(retrievedMeals.containsKey("Monday"), "Daily meals should contain meals for Monday");
        assertEquals(2, retrievedMeals.get("Monday").getMeals().size(), "Monday should contain two meals");
    }

    @Test
    public void testUserProfileLink() {
        UUID userId = userProfile.getUserId();
    }
}
