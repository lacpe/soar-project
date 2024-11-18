package ch.unil.doplab.recipe.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class APIHandlerTest {

    private APIHandler apiHandler;
    private UserProfile userProfile;

    @BeforeEach
    public void setUp() {
        apiHandler = Mockito.mock(APIHandler.class);
        userProfile = new UserProfile(null, "testUser", "testPassword", UserProfile.DietType.VEGETARIAN,
                new HashSet<>(), new HashSet<>(), 2000, UserProfile.MealPlanPreference.DAY);
    }

    @Test
    public void testGenerateMealPlan() {
        // Set up a mock meal plan with dummy data
        MealPlan dummyMealPlan = new MealPlan();
        when(apiHandler.generateMealPlan(userProfile)).thenReturn(dummyMealPlan);

        // Call the method
        MealPlan mealPlan = apiHandler.generateMealPlan(userProfile);

        // Assertions
        assertNotNull(mealPlan, "Generated meal plan should not be null");

        // Verify that generateMealPlan was called once
        verify(apiHandler, times(1)).generateMealPlan(userProfile);
    }
}
