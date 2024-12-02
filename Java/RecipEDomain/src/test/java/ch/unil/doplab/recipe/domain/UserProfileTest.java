package ch.unil.doplab.recipe.domain;

import ch.unil.doplab.recipe.domain.UserProfile;
import ch.unil.doplab.recipe.domain.UserProfile.DietType;
import ch.unil.doplab.recipe.domain.UserProfile.MealPlanPreference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class UserProfileTest {

    private UserProfile userProfile;
    private static final String USERNAME = "testUser";
    private static final String PASSWORD = "password";
    UUID uuid = UUID.randomUUID();


    @BeforeEach
    public void setUp() {
        userProfile = new UserProfile(uuid, USERNAME, PASSWORD, UserProfile.DietType.VEGETARIAN,
                new HashSet<>(), new HashSet<>(), 2000, UserProfile.MealPlanPreference.DAY);
    }

    @Test
    public void testUserProfileInitialization() {
        assertEquals(USERNAME, userProfile.getUsername(), "Username should match the initialized value");
        assertNotNull(userProfile.getPassword(), "Password should not be null after hashing");
    }

    @Test
    public void testSetAndGetDietType() {
        userProfile.setDietType(DietType.VEGAN);
        assertEquals(DietType.VEGAN, userProfile.getDietType(), "Diet type should be set to VEGAN");
    }

    @Test
    public void testSetAndGetDailyCalorieTarget() {
        userProfile.setDailyCalorieTarget(2000);
        assertTrue(userProfile.getDailyCalorieTarget() != 0, "Daily calorie target should be present");
        assertEquals(2000, userProfile.getDailyCalorieTarget(), "Daily calorie target should be 2000");
    }

    @Test
    public void testAddAndRemoveAllergies() {
        userProfile.addAllergy("Peanuts");
        assertTrue(userProfile.getAllergies().contains("Peanuts"), "Allergies should contain 'Peanuts'");

        userProfile.removeAllergy("Peanuts");
        assertFalse(userProfile.getAllergies().contains("Peanuts"), "Allergies should not contain 'Peanuts' after removal");
    }

    @Test
    public void testAddAndRemoveDislikedIngredients() {
        userProfile.addDislikedIngredient("Broccoli");
        assertTrue(userProfile.getDislikedIngredients().contains("Broccoli"), "Disliked ingredients should contain 'Broccoli'");

        userProfile.removeDislikedIngredient("Broccoli");
        assertFalse(userProfile.getDislikedIngredients().contains("Broccoli"), "Disliked ingredients should not contain 'Broccoli' after removal");
    }

    @Test
    public void testSetAndGetMealPlanPreference() {
        userProfile.setMealPlanPreference(MealPlanPreference.WEEK);
        assertEquals(MealPlanPreference.WEEK, userProfile.getMealPlanPreference(), "Meal plan preference should be set to WEEK");
    }

    @Test
    public void testReplaceWithUser() {
        UserProfile newUserProfile = new UserProfile(uuid, "newUser", "newPassword", UserProfile.DietType.VEGETARIAN,
                new HashSet<>(), new HashSet<>(), 2000, UserProfile.MealPlanPreference.WEEK);
        newUserProfile.setDietType(DietType.PALEO);
        newUserProfile.setDailyCalorieTarget(1800);
        newUserProfile.addAllergy("Gluten");
        newUserProfile.addDislikedIngredient("Mushrooms");
        newUserProfile.setMealPlanPreference(MealPlanPreference.DAY);

        userProfile.replaceWithUser(newUserProfile);

        assertEquals("newUser", userProfile.getUsername(), "Username should be replaced with newUser");
        assertEquals(newUserProfile.getPassword(), userProfile.getPassword(), "Password should be replaced with new password hash");
        assertEquals(DietType.PALEO, userProfile.getDietType(), "Diet type should be replaced with PALEO");
        assertTrue(userProfile.getDailyCalorieTarget() != 0, "Daily calorie target should be present after replacement");
        assertEquals(1800, userProfile.getDailyCalorieTarget(), "Daily calorie target should be replaced with 1800");
        assertTrue(userProfile.getAllergies().contains("Gluten"), "Allergies should include 'Gluten' after replacement");
        assertTrue(userProfile.getDislikedIngredients().contains("Mushrooms"), "Disliked ingredients should include 'Mushrooms' after replacement");
        assertEquals(MealPlanPreference.DAY, userProfile.getMealPlanPreference(), "Meal plan preference should be replaced with DAILY");
    }
}
