package ch.unil.doplab.recipe.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MealTest {

    private Meal meal;
    private static final int MEAL_ID = 1;
    private static final String MEAL_TITLE = "Pancakes";
    private static final String IMAGE_URL = "https://example.com/pancakes.jpg";
    private NutritionalInfo nutritionalInfo;

    @BeforeEach
    public void setUp() {
        nutritionalInfo = new NutritionalInfo(100, 5, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0); // example values: calories, protein, fat, carbs
        meal = new Meal(MEAL_ID, MEAL_TITLE, IMAGE_URL, nutritionalInfo);
    }

    @Test
    public void testMealInitialization() {
        assertEquals(MEAL_ID, meal.getId(), "Meal ID should match initialized value");
        assertEquals(MEAL_TITLE, meal.getTitle(), "Meal title should match initialized value");
        assertEquals(IMAGE_URL, meal.getImageUrl(), "Image URL should match initialized value");
        assertEquals(nutritionalInfo, meal.getNutritionalInfo(), "Nutritional information should match initialized value");
    }

    @Test
    public void testSetAndGetIngredients() {
        Ingredient flour = new Ingredient("Flour", 2, "cups");
        Ingredient milk = new Ingredient("Milk", 1.5, "cups");
        Ingredient eggs = new Ingredient("Eggs", 2, "units");

        List<Ingredient> ingredients = List.of(flour, milk, eggs);
        meal.setIngredients(ingredients);

        assertNotNull(meal.getIngredients(), "Ingredients should not be null after setting");
        assertEquals(3, meal.getIngredients().size(), "Ingredients list should contain 3 items");
        assertTrue(meal.getIngredients().contains(flour), "Ingredients list should contain flour");
    }

    @Test
    public void testSetAndGetInstructions() {
        List<String> instructions = List.of(
                "Mix all ingredients together.",
                "Heat a pan over medium heat.",
                "Pour batter onto the pan and cook until golden brown."
        );

        meal.setInstructions(instructions);

        assertNotNull(meal.getInstructions(), "Instructions should not be null after setting");
        assertEquals(3, meal.getInstructions().size(), "Instructions list should contain 3 steps");
        assertEquals("Mix all ingredients together.", meal.getInstructions().get(0), "First instruction should match");
    }

    @Test
    public void testSetAndGetNutritionalInfo() {
        NutritionalInfo newNutritionalInfo = new NutritionalInfo(250, 12, 7, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);;
        meal.setNutritionalInfo(newNutritionalInfo);

        assertNotNull(meal.getNutritionalInfo(), "Nutritional info should not be null after setting");
        assertEquals(newNutritionalInfo, meal.getNutritionalInfo(), "Nutritional info should be updated correctly");
    }

    @Test
    public void testSetTitle() {
        meal.setTitle("New Pancakes Title");
        assertEquals("New Pancakes Title", meal.getTitle(), "Title should be updated to 'New Pancakes Title'");
    }

    @Test
    public void testSetImageUrl() {
        String newImageUrl = "https://example.com/new-pancakes.jpg";
        meal.setImageUrl(newImageUrl);
        assertEquals(newImageUrl, meal.getImageUrl(), "Image URL should be updated to new URL");
    }

    @Test
    public void testDisplayMealIngredients() {
        // This test ensures that ingredients can be displayed
        // but does not assert console output (console output generally isn't tested in unit tests).
        meal.setIngredients(List.of(
                new Ingredient("Flour", 2, "cups"),
                new Ingredient("Milk", 1.5, "cups"),
                new Ingredient("Eggs", 2, "units")
        ));

        assertDoesNotThrow(() -> meal.displayMealIngredients(), "displayMealIngredients should execute without exceptions");
    }

    @Test
    public void testDisplayMealInfo() {
        assertDoesNotThrow(() -> meal.displayMealInfo(), "displayMealInfo should execute without exceptions");
    }

    @Test
    public void testDisplayInstructions() {
        meal.setInstructions(List.of(
                "Mix all ingredients together.",
                "Heat a pan over medium heat.",
                "Pour batter onto the pan and cook until golden brown."
        ));

        assertDoesNotThrow(() -> meal.displayInstructions(), "displayInstructions should execute without exceptions");
    }
}
